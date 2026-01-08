package dao;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reparateur")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reparateur extends User {

    private float pourcentageGain;

    @ManyToOne
    @JoinColumn(name = "id_boutique")
    private Boutique boutique;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_caisse")
    private Caisse caisse;
}
