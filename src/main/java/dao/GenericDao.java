package dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class GenericDao {

    // Connexion gérée directement ici (remplace JPAUtil)
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("repairPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // --- Méthodes CRUD ---

    public <T> void save(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public <T> T update(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            T merged = em.merge(entity);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public <T> void delete(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.remove(em.merge(entity));
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public <T> T findById(Class<T> entityClass, Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    public <T> T findOneByProperty(Class<T> entityClass, String propertyName, Object value) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + propertyName + " = :val";
            List<T> res = em.createQuery(jpql, entityClass).setParameter("val", value).getResultList();
            return res.isEmpty() ? null : res.get(0);
        } finally {
            em.close();
        }
    }

    public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + propertyName + " = :val";
            return em.createQuery(jpql, entityClass).setParameter("val", value).getResultList();
        } finally {
            em.close();
        }
    }
}