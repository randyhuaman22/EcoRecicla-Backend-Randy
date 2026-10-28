package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="privilegios") @Getter @Setter @NoArgsConstructor
public class Privilegio {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=40) private String codigo;
    public Privilegio(String codigo) { this.codigo=codigo; }
}
