package metier;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import dao.Caisse;
import dao.Emprunt;
import dao.GenericDao;
import dao.Reparateur;
import exception.EmpruntException;

public class EmpruntMetierImpl implements IEmpruntMetier {

    private GenericDao dao = new GenericDao();

    @Override
    public Emprunt creerEmprunt(
            Reparateur reparateur,
            float montant,
            String type,
            String commentaire
    ) throws EmpruntException {

        EntityManager em = JPAUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            if (reparateur == null)
                throw new EmpruntException("Réparateur obligatoire");

            if (montant <= 0)
                throw new EmpruntException("Montant invalide");

            if (type == null || type.isEmpty())
                throw new EmpruntException("Type d'emprunt obligatoire");

            // Vérifier que le type est valide
            if (!"SORTIE".equalsIgnoreCase(type) && !"ENTREE".equalsIgnoreCase(type))
                throw new EmpruntException("Type doit être SORTIE ou ENTREE");

            Emprunt e = new Emprunt();
            e.setReparateur(reparateur);
            e.setMontant(montant);
            e.setType(type);
            e.setCommentaire(commentaire);
            e.setDate(new Date());

            dao.save(e);

            // Mise à jour de la caisse selon le type d'emprunt
            Caisse caisse = reparateur.getCaisse();
            if (caisse == null)
                throw new EmpruntException("Caisse inexistante");

            if ("SORTIE".equalsIgnoreCase(type)) {
                // SORTIE = j'emprunte → l'argent ENTRE dans ma caisse
                caisse.setSoldeActuel(caisse.getSoldeActuel() + montant);
            } else if ("ENTREE".equalsIgnoreCase(type)) {
                // ENTREE = je prête → l'argent SORT de ma caisse
                if (caisse.getSoldeActuel() < montant)
                    throw new EmpruntException("Solde insuffisant pour prêter");
                caisse.setSoldeActuel(caisse.getSoldeActuel() - montant);
            }

            caisse.setDernierMouvement(new Date());
            dao.update(caisse);
            
            tx.commit();
            return e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new EmpruntException("Erreur création emprunt", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Emprunt> listerEmpruntsParReparateur(Reparateur reparateur)
            throws EmpruntException {

        if (reparateur == null)
            throw new EmpruntException("Réparateur obligatoire");

        return dao.findByProperty(Emprunt.class, "reparateur", reparateur);
    }
    @Override
    public void rembourserEmprunt(Emprunt emprunt)
            throws EmpruntException {

        EntityManager em = JPAUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            if (emprunt == null)
                throw new EmpruntException("Emprunt invalide");

            if (emprunt.isRembourse())
                throw new EmpruntException("Emprunt déjà remboursé");

            Reparateur reparateur = emprunt.getReparateur();
            if (reparateur == null)
                throw new EmpruntException("Réparateur invalide");

            Caisse caisse = reparateur.getCaisse();
            if (caisse == null)
                throw new EmpruntException("Caisse inexistante");

            float montant = emprunt.getMontant();

            if ("SORTIE".equalsIgnoreCase(emprunt.getType())) {
                // SORTIE = j'avais emprunté → je rembourse → l'argent SORT de ma caisse
                if (caisse.getSoldeActuel() < montant)
                    throw new EmpruntException("Solde insuffisant pour rembourser");
                caisse.setSoldeActuel(caisse.getSoldeActuel() - montant);
            } else if ("ENTREE".equalsIgnoreCase(emprunt.getType())) {
                // ENTREE = j'avais prêté → on me rembourse → l'argent ENTRE dans ma caisse
                caisse.setSoldeActuel(caisse.getSoldeActuel() + montant);
            } else {
                throw new EmpruntException("Type d'emprunt invalide");
            }

            caisse.setDernierMouvement(new Date());

            emprunt.setRembourse(true);
            emprunt.setDateRemboursement(new Date());

            dao.update(caisse);
            dao.update(emprunt);
            
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new EmpruntException("Erreur remboursement emprunt", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public float calculerSoldeAvecEmprunts(Reparateur reparateur)
            throws EmpruntException {

        if (reparateur == null)
            throw new EmpruntException("Réparateur obligatoire");

        // Solde réel de la caisse
        float soldeReel = 0;
        if (reparateur.getCaisse() != null) {
            soldeReel = reparateur.getCaisse().getSoldeActuel();
        }

        // Récupérer tous les emprunts non remboursés
        List<Emprunt> emprunts = listerEmpruntsParReparateur(reparateur);
        
        float ajustementEmprunts = 0;
        for (Emprunt e : emprunts) {
            if (!e.isRembourse()) {
                if ("SORTIE".equalsIgnoreCase(e.getType())) {
                    // SORTIE = j'ai emprunté → je dois rembourser → dette
                    ajustementEmprunts -= e.getMontant();
                } else if ("ENTREE".equalsIgnoreCase(e.getType())) {
                    // ENTREE = j'ai prêté → on me doit → créance
                    ajustementEmprunts += e.getMontant();
                }
            }
        }

        return soldeReel + ajustementEmprunts;
    }
}
