package metier;

import dao.Composant;
import exception.ComposantException;
import java.util.List;

public interface IComposantMetier {
    void ajouterComposant(Composant composant) throws ComposantException;
    void modifierComposant(Composant composant) throws ComposantException;
    void supprimerComposant(Long id) throws ComposantException;
    Composant chercherComposant(Long id) throws ComposantException;
    List<Composant> listerComposants() throws ComposantException;
    List<Composant> chercherComposantsParNom(String nom) throws ComposantException;
}
