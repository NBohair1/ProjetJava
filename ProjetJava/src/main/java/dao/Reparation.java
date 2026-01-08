package dao;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reparation")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReparation;

    @Column(unique = true)
    private String codeSuivi;

    // EN_ATTENTE | EN_COURS | TERMINEE | LIVREE | ANNULEE
    private String etat;
    private String commentaire;
    private float prixTotal;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDepot;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLivraison;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Reparateur reparateur;

    @OneToMany(mappedBy = "reparation", cascade = CascadeType.ALL)
    private List<Appareil> appareils;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Composant> composants;
    
    @OneToOne(mappedBy = "reparation", cascade = CascadeType.ALL)
    private Recu recu;
}
