package pe.upeu.ecorecicla.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.upeu.ecorecicla.dominio.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
  @EntityGraph(attributePaths = {"roles", "roles.privilegios"})
  Optional<Usuario> findByUsernameIgnoreCase(String username);
}
