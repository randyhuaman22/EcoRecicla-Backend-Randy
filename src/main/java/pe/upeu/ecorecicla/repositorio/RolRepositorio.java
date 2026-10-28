package pe.upeu.ecorecicla.repositorio;
import pe.upeu.ecorecicla.dominio.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RolRepositorio extends JpaRepository<Rol,Long> { Optional<Rol> findByCodigo(String codigo); }
