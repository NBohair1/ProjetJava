package metier;

import java.util.List;

import dao.Emprunt;
import dao.Reparateur;
import exception.EmpruntException;

public interface IEmpruntMetier {

    Emprunt creerEmprunt(
            Reparateur reparateur,
            float montant,
            String type,
            String commentaire
    ) throws EmpruntException;

    List<Emprunt> listerEmpruntsParReparateur(Reparateur reparateur)
            throws EmpruntException;
    
    void rembourserEmprunt(Emprunt emprunt)
            throws EmpruntException;
    
    float calculerSoldeAvecEmprunts(Reparateur reparateur)
            throws EmpruntException;
}
