package metier;

import dao.Composant;
import dao.GenericDao;
import exception.ComposantException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class ComposantMetierImpl implements IComposantMetier {

    private GenericDao dao = new GenericDao();

    @Override
    public void ajouterComposant(Composant composant) throws ComposantException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(composant);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ComposantException("Erreur lors de l'ajout du composant: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public void modifierComposant(Composant composant) throws ComposantException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(composant);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ComposantException("Erreur lors de la modification du composant: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public void supprimerComposant(Long id) throws ComposantException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Composant c = em.find(Composant.class, id);
            if (c != null) {
                em.remove(c);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new ComposantException("Erreur lors de la suppression du composant: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public Composant chercherComposant(Long id) throws ComposantException {
        EntityManager em = dao.getEntityManager();
        try {
            return em.find(Composant.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Composant> listerComposants() throws ComposantException {
        EntityManager em = dao.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Composant c", Composant.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Composant> chercherComposantsParNom(String nom) throws ComposantException {
        EntityManager em = dao.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Composant c WHERE c.nom LIKE :nom", Composant.class)
                    .setParameter("nom", "%" + nom + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
