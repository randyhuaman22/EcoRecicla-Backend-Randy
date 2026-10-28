package pe.upeu.ecorecicla.repositorio;
import pe.upeu.ecorecicla.dominio.RutaPunto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface RutaPuntoRepositorio extends JpaRepository<RutaPunto,Long> {
    List<RutaPunto> findByRutaIdOrderBySecuenciaAsc(Long rutaId);
    Optional<RutaPunto> findByRutaIdAndPuntoId(Long rutaId,Long puntoId);
    long countByRutaIdAndMotivoOmissionIsNullAndIdNotIn(Long rutaId,List<Long> ids);
}
