package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity @Table(name="rutas_puntos", uniqueConstraints={@UniqueConstraint(columnNames={"ruta_id","punto_id"}),@UniqueConstraint(columnNames={"ruta_id","secuencia"})}) @Getter @Setter @NoArgsConstructor
public class RutaPunto {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ruta_id", nullable=false) private Ruta ruta;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="punto_id", nullable=false) private PuntoAcopio punto;
    @Column(nullable=false) private int secuencia;
    @Column(name="motivo_omision", length=500) private String motivoOmission;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="omitido_por") private Usuario omitidoPor;
    @Column(name="omitido_en") private Instant omitidoEn;
}
