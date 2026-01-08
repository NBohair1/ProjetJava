package metier;

import java.util.List;

import dao.Boutique;
import dao.Caisse;
import dao.Proprietaire;
import dao.Reparateur;
import exception.BoutiqueException;

public interface IBoutiqueMetier {

    // Créer un réparateur dans une boutique
    Reparateur creerReparateur(
        Boutique boutique,
        String nom,
        String prenom,
        String email,
        String motDePasse,
        float pourcentageGain
    ) throws BoutiqueException;
    
    // Lister les réparateurs d'une boutique
    List<Reparateur> listerReparateurs(Boutique boutique) throws BoutiqueException;
    
    // Lister les boutiques d'un propriétaire
    List<Boutique> listerBoutiques(Proprietaire proprietaire) throws BoutiqueException;
    
    // Modifier les infos d'une boutique
    Boutique modifierBoutique(
        Boutique boutique,
        String nom,
        String adresse,
        String numTelephone,
        String numPatente
    ) throws BoutiqueException;
    
    // Créer la caisse d'un réparateur
    Caisse creerCaisseReparateur(Reparateur reparateur) throws BoutiqueException;
    
    // Modifier le pourcentage de gain d'un réparateur
    void modifierPourcentageGain(Reparateur reparateur, float nouveauPourcentage) throws BoutiqueException;
}
