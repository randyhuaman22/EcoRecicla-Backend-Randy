package pe.upeu.ecorecicla.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import java.time.Instant;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import pe.upeu.ecorecicla.web.dto.Dtos.ErrorResponse;

@Configuration
public class SeguridadErrores {
  @Bean
  AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper mapper) {
    return (req, res, e) ->
        write(mapper, req, res, 401, "Unauthorized", "Se requiere un token válido.");
  }

  @Bean
  AccessDeniedHandler accessDeniedHandler(ObjectMapper mapper) {
    return (req, res, e) ->
        write(
            mapper,
            req,
            res,
            403,
            "Forbidden",
            "El usuario autenticado no tiene privilegio para esta operación.");
  }

  private static void write(
      ObjectMapper m,
      HttpServletRequest q,
      HttpServletResponse r,
      int status,
      String error,
      String message)
      throws java.io.IOException {
    r.setStatus(status);
    r.setContentType(MediaType.APPLICATION_JSON_VALUE);
    m.writeValue(
        r.getOutputStream(),
        new ErrorResponse(Instant.now(), status, error, message, q.getRequestURI(), Map.of()));
  }
}
