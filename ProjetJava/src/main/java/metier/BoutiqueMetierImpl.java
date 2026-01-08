package metier;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import dao.Boutique;
import dao.Caisse;
import dao.GenericDao;
import dao.Proprietaire;
import dao.Reparateur;
import exception.BoutiqueException;

public class BoutiqueMetierImpl implements IBoutiqueMetier {

    private GenericDao dao = new GenericDao();

    @Override
    public Reparateur creerReparateur(
            Boutique boutique,
            String nom,
            String prenom,
            String email,
            String motDePasse,
            float pourcentageGain
    ) throws BoutiqueException {

        EntityManager em = JPAUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            if (boutique == null)
                throw new BoutiqueException("Boutique obligatoire");
            
            if (nom == null || nom.isEmpty())
                throw new BoutiqueException("Nom obligatoire");
            
            if (prenom == null || prenom.isEmpty())
                throw new BoutiqueException("Prénom obligatoire");

            if (email == null || email.isEmpty())
                throw new BoutiqueException("Email obligatoire");

            if (motDePasse == null || motDePasse.isEmpty())
                throw new BoutiqueException("Mot de passe obligatoire");
            
            if (pourcentageGain < 0 || pourcentageGain > 100)
                throw new BoutiqueException("Pourcentage de gain doit être entre 0 et 100");

            // Vérifier si l'email existe déjà
            Reparateur existant = dao.findOneByProperty(Reparateur.class, "email", email);
            if (existant != null)
                throw new BoutiqueException("Cet email est déjà utilisé");

            Reparateur r = new Reparateur();
            r.setNom(nom);
            r.setPrenom(prenom);
            r.setEmail(email);
            r.setMdp(motDePasse);
            r.setPourcentageGain(pourcentageGain);
            r.setBoutique(boutique);
            
            // Créer automatiquement une caisse pour le réparateur
            Caisse caisse = new Caisse();
            caisse.setSoldeActuel(0);
            caisse.setDernierMouvement(new Date());
            r.setCaisse(caisse);

            dao.save(r);
            
            tx.commit();
            return r;
        } catch (BoutiqueException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new BoutiqueException("Erreur création réparateur", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Reparateur> listerReparateurs(Boutique boutique) throws BoutiqueException {
        if (boutique == null)
            throw new BoutiqueException("Boutique obligatoire");
        
        return dao.findByProperty(Reparateur.class, "boutique", boutique);
    }

    @Override
    public List<Boutique> listerBoutiques(Proprietaire proprietaire) throws BoutiqueException {
        if (proprietaire == null)
            throw new BoutiqueException("Propriétaire obligatoire");
        
        return dao.findByProperty(Boutique.class, "proprietaire", proprietaire);
    }

    @Override
    public Boutique modifierBoutique(
            Boutique boutique,
            String nom,
            String adresse,
            String numTelephone,
            String numPatente
    ) throws BoutiqueException {

        EntityManager em = JPAUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            if (boutique == null)
                throw new BoutiqueException("Boutique obligatoire");

            if (nom != null && !nom.isEmpty())
                boutique.setNom(nom);
            
            if (adresse != null && !adresse.isEmpty())
                boutique.setAdresse(adresse);
            
            if (numTelephone != null)
                boutique.setNumTelephone(numTelephone);
            
            if (numPatente != null)
                boutique.setNumP(numPatente);

            Boutique updated = dao.update(boutique);
            
            tx.commit();
            return updated;
        } catch (BoutiqueException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new BoutiqueException("Erreur modification boutique", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Caisse creerCaisseReparateur(Reparateur reparateur) throws BoutiqueException {

        EntityManager em = JPAUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            if (reparateur == null)
                throw new BoutiqueException("Réparateur obligatoire");
            
            if (reparateur.getCaisse() != null)
                throw new BoutiqueException("Le réparateur a déjà une caisse");

            Caisse caisse = new Caisse();
            caisse.setSoldeActuel(0);
            caisse.setDernierMouvement(new Date());
            
            dao.save(caisse);
            
            reparateur.setCaisse(caisse);
            dao.update(reparateur);
            
            tx.commit();
            return caisse;
        } catch (BoutiqueException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new BoutiqueException("Erreur création caisse", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void modifierPourcentageGain(Reparateur reparateur, float nouveauPourcentage) 
            throws BoutiqueException {

        EntityManager em = JPAUtil.getInstance().getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            if (reparateur == null)
                throw new BoutiqueException("Réparateur obligatoire");
            
            if (nouveauPourcentage < 0 || nouveauPourcentage > 100)
                throw new BoutiqueException("Pourcentage doit être entre 0 et 100");

            reparateur.setPourcentageGain(nouveauPourcentage);
            dao.update(reparateur);
            
            tx.commit();
        } catch (BoutiqueException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new BoutiqueException("Erreur modification pourcentage", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
