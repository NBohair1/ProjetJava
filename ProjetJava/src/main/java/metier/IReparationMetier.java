package metier;

import java.util.List;

import dao.Appareil;
import dao.Client;
import dao.Composant;
import dao.Recu;
import dao.Reparateur;
import dao.Reparation;
import exception.ReparationException;

public interface IReparationMetier {

    // CRUD Réparation
    Reparation creerReparation(Client client, Reparateur reparateur)
            throws ReparationException;

    void ajouterAppareil(Reparation reparation, Appareil appareil)
            throws ReparationException;

    void ajouterComposant(Reparation reparation, Composant composant)
            throws ReparationException;

    void changerEtat(Reparation reparation, String nouvelEtat)
            throws ReparationException;

    float calculerPrixTotal(Reparation reparation)
            throws ReparationException;

    List<Reparation> listerReparationsParClient(Client client)
            throws ReparationException;

    void genererCodeSuivi(Reparation reparation)
            throws ReparationException;
    
    // ===== NOUVELLES MÉTHODES =====
    
    // Lister toutes les réparations d'un réparateur
    List<Reparation> listerReparationsParReparateur(Reparateur reparateur)
            throws ReparationException;
    
    // Lister toutes les réparations
    List<Reparation> listerToutesLesReparations() throws ReparationException;
    
    // Livrer la réparation au client
    void livrerReparation(Reparation reparation) throws ReparationException;
    
    // Générer le reçu de paiement (CASH uniquement)
    Recu genererRecu(Reparation reparation, float montantPaye) throws ReparationException;
    
    // Générer le PDF du reçu
    String genererRecuPDF(Recu recu, String cheminSortie) throws ReparationException;
    
    // Rechercher une réparation par son code de suivi
    Reparation rechercherParCodeSuivi(String codeSuivi) throws ReparationException;
    
    // Annuler une réparation
    void annulerReparation(Reparation reparation) throws ReparationException;
    
    // Modifier une réparation existante
    void modifierReparation(Reparation reparation) throws ReparationException;
    
    // Supprimer une réparation
    void supprimerReparation(Reparation reparation) throws ReparationException;
    
    // Supprimer un appareil d'une réparation
    void supprimerAppareil(Reparation reparation, Appareil appareil) throws ReparationException;
    
    // Supprimer un composant d'une réparation
    void supprimerComposant(Reparation reparation, Composant composant) throws ReparationException;
}
