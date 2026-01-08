package metier;

import java.util.List;

import dao.Client;
import dao.Reparation;
import exception.ClientException;

public interface IClientMetier {

    // CRUD Client
    Client creerClient(
        String nom,
        String prenom,
        String telephone,
        String adresse,
        byte[] image
    ) throws ClientException;
    
    Client modifierClient(
        Client client,
        String nom,
        String prenom,
        String telephone,
        String adresse,
        byte[] image
    ) throws ClientException;
    
    void supprimerClient(Client client) throws ClientException;
    
    Client rechercherClientParId(Long id) throws ClientException;
    
    Client rechercherClientParTelephone(String telephone) throws ClientException;
    
    List<Client> listerTousLesClients() throws ClientException;
    
    // Gestion fidélité
    void marquerClientFidele(Client client) throws ClientException;
    
    List<Client> listerClientsFideles() throws ClientException;
    
    // Historique réparations d'un client
    List<Reparation> historqueReparationsClient(Client client) throws ClientException;
    
    // ===== SUIVI RÉPARATION PAR CODE =====
    // Cette méthode permet au client de suivre sa réparation avec le code
    Reparation suivreReparationParCode(String codeSuivi) throws ClientException;
}
