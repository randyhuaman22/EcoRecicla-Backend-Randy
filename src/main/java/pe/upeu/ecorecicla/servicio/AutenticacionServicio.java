package pe.upeu.ecorecicla.servicio;

import java.util.List;
import org.springframework.security.authentication.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pe.upeu.ecorecicla.dominio.Usuario;
import pe.upeu.ecorecicla.exception.NoEncontradoException;
import pe.upeu.ecorecicla.repositorio.UsuarioRepositorio;
import pe.upeu.ecorecicla.seguridad.JwtServicio;
import pe.upeu.ecorecicla.web.dto.Dtos;

@Service
public class AutenticacionServicio {
  private final AuthenticationManager manager;
  private final JwtServicio jwt;
  private final UsuarioRepositorio usuarios;

  public AutenticacionServicio(AuthenticationManager m, JwtServicio j, UsuarioRepositorio u) {
    manager = m;
    jwt = j;
    usuarios = u;
  }

  public Dtos.LoginResponse login(Dtos.LoginRequest r) {
    var auth =
        manager.authenticate(new UsernamePasswordAuthenticationToken(r.usuario(), r.contrasena()));
    Usuario u =
        usuarios
            .findByUsernameIgnoreCase(auth.getName())
            .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado."));
    List<SimpleGrantedAuthority> authorities =
        Mapeos.privilegios(u).stream().map(SimpleGrantedAuthority::new).toList();
    return new Dtos.LoginResponse(
        jwt.emitir(u.getId(), u.getUsername(), authorities),
        "Bearer",
        jwt.vigenciaSegundos(),
        u.getNombre(),
        u.getUsername(),
        Mapeos.rol(u),
        Mapeos.privilegios(u));
  }

  public Dtos.UsuarioResponse actual(String username) {
    Usuario u =
        usuarios
            .findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado."));
    return new Dtos.UsuarioResponse(
        u.getId(), u.getUsername(), u.getNombre(), Mapeos.rol(u), Mapeos.privilegios(u));
  }

  public Usuario usuario(String username) {
    return usuarios
        .findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado."));
  }
}
