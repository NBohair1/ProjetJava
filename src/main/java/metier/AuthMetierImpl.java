package metier;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import dao.*;
import exception.AuthException;

public class AuthMetierImpl implements IAuthMetier {
    
    private GenericDao dao = new GenericDao();

    @Override
    public Proprietaire inscription(String nom, String prenom, String email, String motDePasse) throws AuthException {
        EntityManager em = dao.getEntityManager(); // Utilise le DAO
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (dao.findOneByProperty(Proprietaire.class, "email", email) != null) 
                throw new AuthException("Email déjà utilisé");
                
            Proprietaire p = new Proprietaire();
            p.setNom(nom); p.setPrenom(prenom); p.setEmail(email); p.setMdp(motDePasse); p.setPourcentageGain(100f);
            
            em.persist(p);
            tx.commit();
            return p;
        } catch (Exception e) {
            if(tx.isActive()) tx.rollback();
            throw new AuthException(e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public User login(String email, String motDePasse) throws AuthException {
        EntityManager em = dao.getEntityManager();
        try {
            // D'abord chercher dans Proprietaire avec chargement EAGER des boutiques
            List<Proprietaire> proprietaires = em.createQuery(
                "SELECT DISTINCT p FROM Proprietaire p LEFT JOIN FETCH p.boutiques WHERE p.email=:e", 
                Proprietaire.class)
                .setParameter("e", email).getResultList();
            
            if (!proprietaires.isEmpty() && proprietaires.get(0).getMdp().equals(motDePasse)) {
                return proprietaires.get(0);
            }
            
            // Sinon chercher dans Reparateur
            List<Reparateur> reparateurs = em.createQuery("SELECT r FROM Reparateur r WHERE r.email=:e", Reparateur.class)
                .setParameter("e", email).getResultList();
            
            if (!reparateurs.isEmpty() && reparateurs.get(0).getMdp().equals(motDePasse)) {
                return reparateurs.get(0);
            }
            
            throw new AuthException("Email ou mot de passe incorrect");
            
        } finally {
            em.close();
        }
    }

    @Override
    public Boutique creerBoutique(Proprietaire p, String nom, String adr, String tel, String pat) throws AuthException {
        EntityManager em = dao.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Boutique b = new Boutique();
            b.setNom(nom); b.setAdresse(adr); b.setNumTelephone(tel); b.setNumP(pat); b.setProprietaire(p);
            em.persist(b);
            
            Proprietaire pManaged = em.find(Proprietaire.class, p.getId());
            if(pManaged.getCaisse() == null) {
                Caisse c = new Caisse();
                c.setSoldeActuel(0);
                c.setDernierMouvement(new Date());
                em.persist(c);
                pManaged.setCaisse(c);
            }
            tx.commit();
            return b;
        } catch (Exception e) {
            if(tx.isActive()) tx.rollback();
            throw new AuthException(e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public Caisse creerCaisseProprietaire(Proprietaire p) throws AuthException {
        return new Caisse(); 
    }
}