package metier;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

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
    
    private GenericDao dao = new GenericDao();
    private ICaisseMetier caisseMetier = new CaisseMetierImpl();
    
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
        Reparation r = dao.findOneByProperty(Reparation.class, "codeSuivi", code);
        if(r == null) throw new ReparationException("Réparation introuvable");
        return r;
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
        return null; 
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
}