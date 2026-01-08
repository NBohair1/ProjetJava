package metier;

import dao.Boutique;
import dao.Caisse;
import dao.Proprietaire;
import dao.User;
import exception.AuthException;

public interface IAuthMetier {
    Proprietaire inscription(String nom, String prenom, String email, String motDePasse) throws AuthException;
    User login(String email, String motDePasse) throws AuthException;
    Boutique creerBoutique(Proprietaire proprietaire, String nom, String adresse, String numTelephone, String numPatente) throws AuthException;
    Caisse creerCaisseProprietaire(Proprietaire proprietaire) throws AuthException;
}