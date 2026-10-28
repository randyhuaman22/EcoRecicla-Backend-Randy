package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity @Table(name="puntos_acopio") @Getter @Setter @NoArgsConstructor
public class PuntoAcopio {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="razon_social", nullable=false, length=160) private String razonSocial;
    @Column(nullable=false, length=240) private String direccion;
    @Column(nullable=false, length=80) private String zona;
    @Column(name="material_habitual", nullable=false, length=60) private String materialHabitual;
    @Column(nullable=false) private boolean activo=true;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="creado_por") private Usuario creadoPor;
    @Column(name="creado_en", nullable=false) private Instant creadoEn=Instant.now();
    @Column(name="actualizado_en") private Instant actualizadoEn;
}
