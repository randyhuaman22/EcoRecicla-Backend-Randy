package pe.upeu.ecorecicla.seguridad;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtServicio {
  private final SecretKey clave;

  public JwtServicio(@Value("${app.jwt.secret}") String secreto) {
    this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
  }

  public String emitir(
      long usuarioId, String username, List<? extends GrantedAuthority> authorities) {
    Instant ahora = Instant.now();
    List<String> privilegios =
        authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> !a.startsWith("ROLE_"))
            .toList();
    return Jwts.builder()
        .subject(username)
        .claim("uid", usuarioId)
        .claim("privilegios", privilegios)
        .issuedAt(Date.from(ahora))
        .expiration(Date.from(ahora.plus(60, ChronoUnit.MINUTES)))
        .signWith(clave)
        .compact();
  }

  public io.jsonwebtoken.Claims validar(String token) {
    return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
  }

  public long vigenciaSegundos() {
    return 3600;
  }
}
