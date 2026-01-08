package dao;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mouvement_caisse")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MouvementCaisse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMouvement;

    private float montant;
    
    // ENTREE (paiement reçu) | SORTIE (retrait) | ALIMENTATION | EMPRUNT_ENTREE | EMPRUNT_SORTIE
    private String typeMouvement;
    
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateMouvement;

    @ManyToOne
    @JoinColumn(name = "id_caisse")
    private Caisse caisse;
    
    @ManyToOne
    @JoinColumn(name = "id_reparation")
    private Reparation reparation;
}
