package pe.upeu.ecorecicla.repositorio;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.upeu.ecorecicla.dominio.RutaPunto;

public interface RutaPuntoRepositorio extends JpaRepository<RutaPunto, Long> {
  List<RutaPunto> findByRutaIdOrderBySecuenciaAsc(Long rutaId);

  Optional<RutaPunto> findByRutaIdAndPuntoId(Long rutaId, Long puntoId);

  long countByRutaIdAndMotivoOmissionIsNullAndIdNotIn(Long rutaId, List<Long> ids);
}
