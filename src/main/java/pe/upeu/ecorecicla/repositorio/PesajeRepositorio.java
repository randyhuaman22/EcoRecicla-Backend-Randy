package pe.upeu.ecorecicla.repositorio;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.upeu.ecorecicla.dominio.Pesaje;

public interface PesajeRepositorio extends JpaRepository<Pesaje, Long> {
  Page<Pesaje> findByPuntoIdOrderByFechaHoraDesc(Long puntoId, Pageable pageable);

  boolean existsByRutaIdAndPuntoIdAndEstado(Long rutaId, Long puntoId, String estado);

  @Query("select distinct p.punto.id from Pesaje p where p.ruta.id=:ruta and p.estado='REGISTRADO'")
  List<Long> puntosPesados(@Param("ruta") Long rutaId);

  boolean existsByIdAndAutorId(Long id, Long autorId);

  List<Pesaje> findByRutaIdAndPuntoIdAndEstado(Long rutaId, Long puntoId, String estado);
}
