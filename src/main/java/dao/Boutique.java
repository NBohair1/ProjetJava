package dao;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "boutique")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Boutique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBoutique;

    private String nom;
    private String adresse;
    private String numTelephone;
    private String numP;

    @ManyToOne
    private Proprietaire proprietaire;

    @OneToMany(mappedBy = "boutique")
    private List<Reparateur> reparateurs;
}
