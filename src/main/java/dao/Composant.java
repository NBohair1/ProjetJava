package dao;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "composant")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Composant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComposant;

    @Column(nullable = false)
    private String nom;

    private float prix;
    private int quantite;
}
