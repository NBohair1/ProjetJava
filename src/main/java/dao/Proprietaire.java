package dao;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proprietaire")
@Getter @Setter
@NoArgsConstructor
public class Proprietaire extends Reparateur {

    @OneToMany(mappedBy = "proprietaire")
    private List<Boutique> boutiques;
    
    // Le propriétaire est automatiquement un réparateur avec 100% de gain
    @PrePersist
    public void initPourcentageGain() {
        if (this.getPourcentageGain() == 0) {
            this.setPourcentageGain(100f);
        }
    }
}
