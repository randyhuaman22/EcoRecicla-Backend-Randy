package pe.upeu.ecorecicla.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.upeu.ecorecicla.dominio.Rol;

public interface RolRepositorio extends JpaRepository<Rol, Long> {
  Optional<Rol> findByCodigo(String codigo);
}
