package pe.upeu.ecorecicla.repositorio;
import pe.upeu.ecorecicla.dominio.PuntoAcopio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PuntoAcopioRepositorio extends JpaRepository<PuntoAcopio,Long> {
    Page<PuntoAcopio> findByActivoTrue(Pageable pageable);
    Page<PuntoAcopio> findByActivoTrueAndZonaIgnoreCase(String zona, Pageable pageable);
}
