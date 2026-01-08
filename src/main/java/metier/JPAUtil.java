package metier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final JPAUtil instance = new JPAUtil();
    private final EntityManagerFactory emf;

    // Constructeur privé pour empêcher l'instanciation
    private JPAUtil() {
        this.emf = Persistence.createEntityManagerFactory("repairPU");
    }

    // Méthode pour récupérer l'instance unique
    public static JPAUtil getInstance() {
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Méthode pour fermer l'EntityManagerFactory (à appeler à la fin de l'application)
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
