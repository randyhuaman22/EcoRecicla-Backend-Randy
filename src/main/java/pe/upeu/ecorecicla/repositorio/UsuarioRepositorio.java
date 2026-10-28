package pe.upeu.ecorecicla.repositorio;
import pe.upeu.ecorecicla.dominio.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UsuarioRepositorio extends JpaRepository<Usuario,Long> {
    @EntityGraph(attributePaths={"roles","roles.privilegios"}) Optional<Usuario> findByUsernameIgnoreCase(String username);
}
