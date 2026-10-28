package pe.upeu.ecorecicla.repositorio;
import pe.upeu.ecorecicla.dominio.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
public interface RutaRepositorio extends JpaRepository<Ruta,Long> { Optional<Ruta> findByFechaAndOperarioId(LocalDate fecha,Long operarioId); }
