package metier;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import dao.*;
import exception.CaisseException;

public class CaisseMetierImpl implements ICaisseMetier {
    private GenericDao dao = new GenericDao();

    // @Override
    // public void alimenterCaisse(Reparateur r, float m, String desc) throws CaisseException {gererMouvement(r, m, "ALIMENTATION", desc, null);}

    @Override
    public void retirerCaisse(Reparateur r, float m, String desc) throws CaisseException {
        if(consulterSolde(r) < m) throw new CaisseException("Solde insuffisant");
        gererMouvement(r, -m, "SORTIE", desc, null);
    }

    @Override
    public void enregistrerPaiement(Reparateur r, float m, String desc, Reparation rep) throws CaisseException {
        gererMouvement(r, m, "ENTREE", desc, rep);
    }

    private void gererMouvement(Reparateur r, float m, String type, String desc, Reparation rep) throws CaisseException {
        if (r == null || r.getCaisse() == null) throw new CaisseException("Pas de caisse");
        EntityManager em = dao.getEntityManager(); // Via DAO
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Caisse c = em.find(Caisse.class, r.getCaisse().getIdCaisse());
            
            if(type.equals("SORTIE")) c.setSoldeActuel(c.getSoldeActuel() + m); // m est négatif
            else c.setSoldeActuel(c.getSoldeActuel() + m);
            
            c.setDernierMouvement(new Date());
            
            MouvementCaisse mv = new MouvementCaisse();
            mv.setMontant(Math.abs(m)); mv.setTypeMouvement(type); mv.setDescription(desc);
            mv.setDateMouvement(new Date()); mv.setCaisse(c); mv.setReparation(rep);
            
            em.persist(mv);
            em.merge(c);
            tx.commit();
        } catch(Exception e) { if(tx.isActive()) tx.rollback(); throw new CaisseException(e.getMessage()); }
        finally { em.close(); }
    }

    @Override
    public float consulterSolde(Reparateur r) throws CaisseException {
        if(r == null || r.getCaisse() == null) return 0;
        EntityManager em = dao.getEntityManager();
        try {
            Caisse caisse = em.find(Caisse.class, r.getCaisse().getIdCaisse());
            return caisse != null ? caisse.getSoldeActuel() : 0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<MouvementCaisse> consulterCaisseHebdomadaire(Reparateur r, Date d1, Date d2) throws CaisseException {
        return listerMouvements(r);
    }
    @Override
    public float consulterTotalCaissesBoutique(Proprietaire p) throws CaisseException { return 0; }
    @Override
    public List<MouvementCaisse> listerMouvements(Reparateur r) throws CaisseException {
        if(r == null || r.getCaisse() == null) return new java.util.ArrayList<>();
        EntityManager em = dao.getEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM MouvementCaisse m " +
                "LEFT JOIN FETCH m.reparation " +
                "WHERE m.caisse.idCaisse = :caisseId " +
                "ORDER BY m.dateMouvement DESC",
                MouvementCaisse.class)
                .setParameter("caisseId", r.getCaisse().getIdCaisse())
                .getResultList();
        } finally {
            em.close();
        }
    }
}