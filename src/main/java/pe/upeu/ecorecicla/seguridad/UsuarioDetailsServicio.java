package pe.upeu.ecorecicla.seguridad;

import pe.upeu.ecorecicla.dominio.Usuario;
import pe.upeu.ecorecicla.repositorio.UsuarioRepositorio;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UsuarioDetailsServicio implements UserDetailsService {
    private final UsuarioRepositorio usuarios;
    public UsuarioDetailsServicio(UsuarioRepositorio usuarios){this.usuarios=usuarios;}
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u=usuarios.findByUsernameIgnoreCase(username).orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado"));
        List<SimpleGrantedAuthority> authorities=u.getRoles().stream().flatMap(r->r.getPrivilegios().stream()).map(p->new SimpleGrantedAuthority(p.getCodigo())).distinct().toList();
        return User.withUsername(u.getUsername()).password(u.getPasswordHash()).disabled(!u.isActivo()).authorities(authorities).build();
    }
}
