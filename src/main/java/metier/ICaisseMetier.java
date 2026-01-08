package metier;

import java.util.Date;
import java.util.List;
import dao.MouvementCaisse;
import dao.Proprietaire;
import dao.Reparateur;
import dao.Reparation;
import exception.CaisseException;

public interface ICaisseMetier {
    void alimenterCaisse(Reparateur reparateur, float montant, String description) throws CaisseException;
    void retirerCaisse(Reparateur reparateur, float montant, String description) throws CaisseException;
    float consulterSolde(Reparateur reparateur) throws CaisseException;
    List<MouvementCaisse> consulterCaisseHebdomadaire(Reparateur reparateur, Date d1, Date d2) throws CaisseException;
    float consulterTotalCaissesBoutique(Proprietaire proprietaire) throws CaisseException;
    List<MouvementCaisse> listerMouvements(Reparateur reparateur) throws CaisseException;
    void enregistrerPaiement(Reparateur reparateur, float montant, String description, Reparation reparation) throws CaisseException;
}