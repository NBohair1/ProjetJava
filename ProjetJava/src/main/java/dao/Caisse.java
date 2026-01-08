package dao;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "caisse")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Caisse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCaisse;

    private float soldeActuel;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dernierMouvement;
    
    @OneToMany(mappedBy = "caisse", cascade = CascadeType.ALL)
    private List<MouvementCaisse> mouvements;
}
