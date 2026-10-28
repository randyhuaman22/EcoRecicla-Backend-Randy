package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name="roles") @Getter @Setter @NoArgsConstructor
public class Rol {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=30) private String codigo;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="roles_privilegios", joinColumns=@JoinColumn(name="rol_id"), inverseJoinColumns=@JoinColumn(name="privilegio_id"))
    private Set<Privilegio> privilegios=new LinkedHashSet<>();
    public Rol(String codigo) { this.codigo=codigo; }
}
