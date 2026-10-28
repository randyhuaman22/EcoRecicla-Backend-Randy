package pe.upeu.ecorecicla.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.upeu.ecorecicla.dominio.Privilegio;

public interface PrivilegioRepositorio extends JpaRepository<Privilegio, Long> {
  Optional<Privilegio> findByCodigo(String codigo);
}
