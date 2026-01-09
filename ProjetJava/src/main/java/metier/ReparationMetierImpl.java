package metier;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import dao.Appareil;
import dao.Client;
import dao.Composant;
import dao.GenericDao;
import dao.Recu;
import dao.Reparateur;
import dao.Reparation;
import exception.CaisseException;
import exception.ReparationException;

public class ReparationMetierImpl implements IReparationMetier {
    
    private final GenericDao dao = new GenericDao();
    private final ICaisseMetier caisseMetier = new CaisseMetierImpl();
    
    @Override
    public Reparation creerReparation(Client client, Reparateur reparateur) throws ReparationException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // Récupérer les entités managées
            Client clientManaged = em.find(Client.class, client.getIdClient());
            Reparateur reparateurManaged = em.find(Reparateur.class, reparateur.getId());
            
            if (clientManaged == null) {
                throw new ReparationException("Client introuvable");
            }
            if (reparateurManaged == null) {
                throw new ReparationException("Réparateur introuvable");
            }
            
            Reparation r = new Reparation();
            r.setClient(clientManaged);
            r.setReparateur(reparateurManaged);
            r.setDateDepot(new Date());
            r.setEtat("EN_ATTENTE");
            r.setPrixTotal(0);
            
            em.persist(r);
            tx.commit();
            
            return r;
        } catch (Exception e) {
            if(tx.isActive()) tx.rollback();
            throw new ReparationException("Erreur lors de la création: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public Reparation rechercherParCodeSuivi(String code) throws ReparationException {
        if (code == null || code.trim().isEmpty()) {
            throw new ReparationException("Code de suivi obligatoire");
        }
        
        EntityManager em = dao.getEntityManager();
        try {
            List<Reparation> results = em.createQuery(
                "SELECT DISTINCT r FROM Reparation r " +
                "LEFT JOIN FETCH r.client " +
                "LEFT JOIN FETCH r.reparateur " +
                "LEFT JOIN FETCH r.appareils " +
                "WHERE r.codeSuivi = :code", 
                Reparation.class)
                .setParameter("code", code)
                .getResultList();
            
            if (results.isEmpty()) {
                throw new ReparationException("Réparation introuvable avec le code: " + code);
            }
            
            return results.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reparation> listerReparationsParReparateur(Reparateur r) throws ReparationException {
        EntityManager em = dao.getEntityManager();
        try {
            return em.createQuery(
                "SELECT DISTINCT r FROM Reparation r " +
                "LEFT JOIN FETCH r.client " +
                "LEFT JOIN FETCH r.reparateur " +
                "LEFT JOIN FETCH r.appareils " +
                "WHERE r.reparateur.id = :repId", 
                Reparation.class)
                .setParameter("repId", r.getId())
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Reparation> listerToutesLesReparations() throws ReparationException {
        EntityManager em = dao.getEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT r FROM Reparation r LEFT JOIN FETCH r.client LEFT JOIN FETCH r.reparateur LEFT JOIN FETCH r.appareils", Reparation.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void ajouterAppareil(Reparation r, Appareil a) throws ReparationException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            a.setReparation(r);
            em.persist(a);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ReparationException("Erreur lors de l'ajout de l'appareil: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public void ajouterComposant(Reparation r, Composant c) throws ReparationException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reparation reparation = em.find(Reparation.class, r.getIdReparation());
            Composant composant = em.find(Composant.class, c.getIdComposant());
            
            // Vérifier le stock
            if (composant.getQuantite() <= 0) {
                throw new ReparationException("Stock insuffisant pour le composant: " + composant.getNom());
            }
            
            // Décrémenter la quantité
            composant.setQuantite(composant.getQuantite() - 1);
            
            if (reparation.getComposants() == null) {
                reparation.setComposants(new java.util.ArrayList<>());
            }
            reparation.getComposants().add(composant);
            em.merge(reparation);
            em.merge(composant);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ReparationException("Erreur lors de l'ajout du composant: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public void changerEtat(Reparation r, String e) throws ReparationException {
        String ancienEtat = r.getEtat();
        r.setEtat(e);
        dao.update(r);
        
        // Si la réparation passe à LIVREE, enregistrer le paiement dans la caisse
        if ("LIVREE".equals(e) && !"LIVREE".equals(ancienEtat)) {
            if (r.getReparateur() != null && r.getReparateur().getCaisse() != null) {
                try {
                    caisseMetier.enregistrerPaiement(
                        r.getReparateur(),
                        r.getPrixTotal(),
                        "Paiement réparation " + r.getCodeSuivi(),
                        r
                    );
                } catch (CaisseException ex) {
                    throw new ReparationException("Erreur lors de l'enregistrement du paiement: " + ex.getMessage());
                }
            }
        }
    }
    
    @Override
    public float calculerPrixTotal(Reparation r) throws ReparationException { 
        return r.getPrixTotal(); 
    }
    
    @Override
    public List<Reparation> listerReparationsParClient(Client c) throws ReparationException { 
        return dao.findByProperty(Reparation.class, "client", c);
    }
    
    @Override
    public void genererCodeSuivi(Reparation r) throws ReparationException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            String code = "REP-" + System.currentTimeMillis() + "-" + r.getIdReparation();
            r.setCodeSuivi(code);
            em.merge(r);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ReparationException("Erreur lors de la génération du code: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public void livrerReparation(Reparation r) throws ReparationException {
        r.setEtat("LIVREE");
        r.setDateLivraison(new java.util.Date());
        dao.update(r);
    }
    
    @Override
    public Recu genererRecu(Reparation r, float m) throws ReparationException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation reparationManaged = em.find(Reparation.class, r.getIdReparation());
            
            Recu recu = new Recu();
            recu.setNumeroRecu("REC-" + System.currentTimeMillis());
            recu.setMontant(m);
            recu.setModePaiement("CASH");
            recu.setDate(new Date());
            recu.setReparation(reparationManaged);
            recu.setClient(reparationManaged.getClient());
            
            em.persist(recu);
            
            reparationManaged.setRecu(recu);
            em.merge(reparationManaged);
            
            tx.commit();
            return recu;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ReparationException("Erreur génération reçu: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public String genererRecuPDF(Recu recu, String cheminSortie) throws ReparationException {
        try {
            // cheminSortie est déjà le chemin complet du fichier (ex: C:/chemin/fichier.pdf)
            Document document = new Document(PageSize.A4);
            
            // Créer les répertoires parents si nécessaires
            java.io.File fichier = new java.io.File(cheminSortie);
            if (fichier.getParentFile() != null && !fichier.getParentFile().exists()) {
                fichier.getParentFile().mkdirs();
            }
            
            PdfWriter.getInstance(document, new FileOutputStream(cheminSortie));
            
            document.open();
            
            // En-tête
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            Paragraph title = new Paragraph("REÇU DE PAIEMENT", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            // Informations Boutique
            Reparation rep = recu.getReparation();
            if (rep != null && rep.getReparateur() != null && rep.getReparateur().getBoutique() != null) {
                Paragraph boutique = new Paragraph(rep.getReparateur().getBoutique().getNom(), fontBold);
                boutique.setAlignment(Element.ALIGN_CENTER);
                document.add(boutique);
                
                Paragraph adresse = new Paragraph(
                    rep.getReparateur().getBoutique().getAdresse() + " - " + 
                    rep.getReparateur().getBoutique().getNumTelephone(), fontNormal);
                adresse.setAlignment(Element.ALIGN_CENTER);
                adresse.setSpacingAfter(20);
                document.add(adresse);
            }
            
            document.add(new Paragraph(" "));
            
            // Informations Reçu
            PdfPTable tableInfo = new PdfPTable(2);
            tableInfo.setWidthPercentage(100);
            tableInfo.setSpacingBefore(10);
            tableInfo.setSpacingAfter(10);
            
            addCell(tableInfo, "N° Reçu:", fontBold);
            addCell(tableInfo, recu.getNumeroRecu(), fontNormal);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            addCell(tableInfo, "Date:", fontBold);
            addCell(tableInfo, sdf.format(recu.getDate()), fontNormal);
            
            if (rep != null) {
                addCell(tableInfo, "Code Suivi:", fontBold);
                addCell(tableInfo, rep.getCodeSuivi() != null ? rep.getCodeSuivi() : "N/A", fontNormal);
            }
            
            document.add(tableInfo);
            
            // Informations Client
            document.add(new Paragraph("INFORMATIONS CLIENT", fontBold));
            document.add(new Paragraph(" "));
            
            PdfPTable tableClient = new PdfPTable(2);
            tableClient.setWidthPercentage(100);
            
            Client client = recu.getClient();
            if (client != null) {
                addCell(tableClient, "Nom:", fontBold);
                addCell(tableClient, client.getNom() + " " + client.getPrenom(), fontNormal);
                
                addCell(tableClient, "Téléphone:", fontBold);
                addCell(tableClient, client.getTelephone() != null ? client.getTelephone() : "N/A", fontNormal);
            }
            
            document.add(tableClient);
            document.add(new Paragraph(" "));
            
            // Détails Réparation
            if (rep != null) {
                document.add(new Paragraph("DÉTAILS RÉPARATION", fontBold));
                document.add(new Paragraph(" "));
                
                PdfPTable tableRep = new PdfPTable(2);
                tableRep.setWidthPercentage(100);
                
                addCell(tableRep, "Prix Total:", fontBold);
                addCell(tableRep, String.format("%.2f DH", rep.getPrixTotal()), fontNormal);
                
                addCell(tableRep, "État:", fontBold);
                addCell(tableRep, rep.getEtat(), fontNormal);
                
                if (rep.getCommentaire() != null && !rep.getCommentaire().isEmpty()) {
                    addCell(tableRep, "Commentaire:", fontBold);
                    addCell(tableRep, rep.getCommentaire(), fontNormal);
                }
                
                document.add(tableRep);
            }
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            // Montant payé
            PdfPTable tableMontant = new PdfPTable(2);
            tableMontant.setWidthPercentage(100);
            tableMontant.setSpacingBefore(20);
            
            PdfPCell cellLabel = new PdfPCell(new Phrase("MONTANT PAYÉ:", fontTitle));
            cellLabel.setBorder(Rectangle.NO_BORDER);
            cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellLabel.setPadding(5);
            tableMontant.addCell(cellLabel);
            
            PdfPCell cellMontant = new PdfPCell(new Phrase(String.format("%.2f DH", recu.getMontant()), fontTitle));
            cellMontant.setBorder(Rectangle.NO_BORDER);
            cellMontant.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellMontant.setPadding(5);
            tableMontant.addCell(cellMontant);
            
            document.add(tableMontant);
            
            // Pied de page
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Merci pour votre confiance!", fontSubtitle);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            return cheminSortie;
            
        } catch (Exception e) {
            throw new ReparationException("Erreur génération PDF: " + e.getMessage());
        }
    }
    
    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }
    
    @Override
    public void annulerReparation(Reparation r) throws ReparationException { 
        r.setEtat("ANNULEE"); 
        dao.update(r); 
    }
    
    @Override
    public void supprimerAppareil(Reparation r, Appareil a) throws ReparationException {
        dao.delete(a);
    }
    
    @Override
    public void supprimerComposant(Reparation r, Composant c) throws ReparationException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Reparation reparation = em.find(Reparation.class, r.getIdReparation());
            reparation.getComposants().remove(c);
            em.merge(reparation);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ReparationException("Erreur: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public void modifierReparation(Reparation reparation) throws ReparationException {
        if (reparation == null) {
            throw new ReparationException("Réparation invalide");
        }
        
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation reparationManaged = em.find(Reparation.class, reparation.getIdReparation());
            if (reparationManaged == null) {
                throw new ReparationException("Réparation non trouvée");
            }
            
            // Mettre à jour les champs modifiables
            reparationManaged.setPrixTotal(reparation.getPrixTotal());
            reparationManaged.setEtat(reparation.getEtat());
            reparationManaged.setCommentaire(reparation.getCommentaire());
            
            // Si l'état passe à LIVREE, on met la date de livraison
            if ("LIVREE".equals(reparation.getEtat()) && reparationManaged.getDateLivraison() == null) {
                reparationManaged.setDateLivraison(new Date());
            }
            
            em.merge(reparationManaged);
            tx.commit();
            
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new ReparationException("Erreur lors de la modification: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public void supprimerReparation(Reparation reparation) throws ReparationException {
        if (reparation == null) {
            throw new ReparationException("Réparation invalide");
        }
        
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Reparation reparationManaged = em.find(Reparation.class, reparation.getIdReparation());
            if (reparationManaged == null) {
                throw new ReparationException("Réparation non trouvée");
            }
            
            // Supprimer les appareils associés
            if (reparationManaged.getAppareils() != null) {
                for (Appareil app : reparationManaged.getAppareils()) {
                    em.remove(em.merge(app));
                }
            }
            
            // Supprimer les reçus associés
            if (reparationManaged.getRecu() != null) {
                em.remove(em.merge(reparationManaged.getRecu()));
            }
            
            // Supprimer la réparation
            em.remove(reparationManaged);
            tx.commit();
            
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new ReparationException("Erreur lors de la suppression: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
