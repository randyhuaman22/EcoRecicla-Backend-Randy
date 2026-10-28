package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="pesajes") @Getter @Setter @NoArgsConstructor
public class Pesaje {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ruta_id", nullable=false) private Ruta ruta;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="punto_id", nullable=false) private PuntoAcopio punto;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="autor_id", nullable=false) private Usuario autor;
    @Column(name="fecha_hora", nullable=false) private Instant fechaHora;
    @Column(nullable=false, length=60) private String material;
    @Column(name="peso_bruto", nullable=false, precision=12, scale=3) private BigDecimal pesoBruto;
    @Column(nullable=false, precision=12, scale=3) private BigDecimal tara;
    @Column(name="peso_neto", nullable=false, precision=12, scale=3) private BigDecimal pesoNeto;
    @Column(length=500) private String observacion;
    @Column(nullable=false, length=12) private String estado="REGISTRADO";
    @Column(name="motivo_anulacion", length=500) private String motivoAnulacion;
    @Column(name="creado_en", nullable=false) private Instant creadoEn=Instant.now();
    @Column(name="actualizado_en") private Instant actualizadoEn;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="modificado_por") private Usuario modificadoPor;
}
