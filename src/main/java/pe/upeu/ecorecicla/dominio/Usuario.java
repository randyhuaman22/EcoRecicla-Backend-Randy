package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name="usuarios") @Getter @Setter @NoArgsConstructor
public class Usuario {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=50) private String username;
    @Column(nullable=false, length=120) private String nombre;
    @Column(name="password_hash", nullable=false, length=100) private String passwordHash;
    @Column(nullable=false) private boolean activo=true;
    @Column(name="creado_en", nullable=false) private Instant creadoEn=Instant.now();
    @Column(name="actualizado_en") private Instant actualizadoEn;
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="usuarios_roles", joinColumns=@JoinColumn(name="usuario_id"), inverseJoinColumns=@JoinColumn(name="rol_id"))
    private Set<Rol> roles=new LinkedHashSet<>();
}
