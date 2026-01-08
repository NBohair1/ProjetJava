package dao;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "appareil")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appareil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAppareil;

    @Column(unique = true)
    private String imei;

    private String marque;
    private String modele;
    private String typeAppareil;

    @ManyToOne
    private Reparation reparation;
}
