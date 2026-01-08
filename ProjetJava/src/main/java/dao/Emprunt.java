package dao;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmprunt;

    private float montant;
    private String type; // ENTREE | SORTIE
    private String commentaire;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRemboursement;

    private boolean rembourse;

    @ManyToOne(optional = false)
    private Reparateur reparateur;
}

