package pe.upeu.ecorecicla.repositorio;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.upeu.ecorecicla.dominio.Ruta;

public interface RutaRepositorio extends JpaRepository<Ruta, Long> {
  Optional<Ruta> findByFechaAndOperarioId(LocalDate fecha, Long operarioId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from Ruta r where r.id=:id")
  Optional<Ruta> findByIdForUpdate(@Param("id") Long id);
}
