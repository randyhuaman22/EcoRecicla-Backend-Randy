package pe.upeu.ecorecicla.seguridad;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFiltro extends OncePerRequestFilter {
  private final JwtServicio jwt;

  public JwtFiltro(JwtServicio jwt) {
    this.jwt = jwt;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer "))
      try {
        Claims c = jwt.validar(h.substring(7));
        List<?> raw = c.get("privilegios", List.class);
        List<SimpleGrantedAuthority> authorities =
            raw.stream().map(Object::toString).map(SimpleGrantedAuthority::new).toList();
        var auth = new UsernamePasswordAuthenticationToken(c.getSubject(), null, authorities);
        auth.setDetails(c.get("uid"));
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception ignored) {
        SecurityContextHolder.clearContext();
      }
    chain.doFilter(req, res);
  }
}
