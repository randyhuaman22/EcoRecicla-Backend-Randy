package pe.upeu.ecorecicla.repositorio;
import pe.upeu.ecorecicla.dominio.Privilegio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PrivilegioRepositorio extends JpaRepository<Privilegio,Long> { Optional<Privilegio> findByCodigo(String codigo); }
