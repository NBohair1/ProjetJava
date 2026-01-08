package dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recu")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecu;

    @Column(unique = true)
    private String numeroRecu;

    private float montant;
    
    private String modePaiement; // CASH uniquement

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @OneToOne
    @JoinColumn(name = "id_reparation")
    private Reparation reparation;
    
    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "id_reparateur")
    private Reparateur reparateur;
}
