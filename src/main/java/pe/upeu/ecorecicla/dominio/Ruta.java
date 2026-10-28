package pe.upeu.ecorecicla.dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="rutas", uniqueConstraints=@UniqueConstraint(columnNames={"fecha","operario_id"})) @Getter @Setter @NoArgsConstructor
public class Ruta {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private LocalDate fecha;
    @Column(nullable=false, length=12) private String estado="ABIERTA";
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="operario_id", nullable=false) private Usuario operario;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cerrada_por") private Usuario cerradaPor;
    @Column(name="cerrada_en") private Instant cerradaEn;
    @Column(name="creado_en", nullable=false) private Instant creadoEn=Instant.now();
    @Column(name="actualizado_en") private Instant actualizadoEn;
}
