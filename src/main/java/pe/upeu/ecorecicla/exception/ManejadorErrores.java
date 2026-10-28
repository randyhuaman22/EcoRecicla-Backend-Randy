package pe.upeu.ecorecicla.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import pe.upeu.ecorecicla.web.dto.Dtos;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class ManejadorErrores {
  private Dtos.ErrorResponse error(HttpServletRequest r,int s,String nombre,String mensaje,Map<String,String> campos){return new Dtos.ErrorResponse(Instant.now(),s,nombre,mensaje,r.getRequestURI(),campos);}
  @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Dtos.ErrorResponse> validacion(MethodArgumentNotValidException e,HttpServletRequest r){Map<String,String> m=new LinkedHashMap<>();for(FieldError f:e.getBindingResult().getFieldErrors())m.putIfAbsent(f.getField(),Objects.requireNonNullElse(f.getDefaultMessage(),"Valor inválido."));return ResponseEntity.badRequest().body(error(r,400,"Bad Request","Los datos enviados no superan la validación.",m));}
  @ExceptionHandler(ValidacionNegocioException.class) ResponseEntity<Dtos.ErrorResponse> reglaCampo(ValidacionNegocioException e,HttpServletRequest r){return ResponseEntity.badRequest().body(error(r,400,"Bad Request",e.getMessage(),Map.of(e.getCampo(),e.getMessage())));}
  @ExceptionHandler({HttpMessageNotReadableException.class,IllegalArgumentException.class}) ResponseEntity<Dtos.ErrorResponse> solicitud(Exception e,HttpServletRequest r){return ResponseEntity.badRequest().body(error(r,400,"Bad Request",e instanceof IllegalArgumentException?e.getMessage():"El cuerpo o los parámetros de la solicitud tienen un formato inválido.",Map.of()));}
  @ExceptionHandler(AuthenticationException.class) ResponseEntity<Dtos.ErrorResponse> autenticacion(Exception e,HttpServletRequest r){return ResponseEntity.status(401).body(error(r,401,"Unauthorized","Usuario o contraseña incorrectos.",Map.of()));}
  @ExceptionHandler(AccessDeniedException.class) ResponseEntity<Dtos.ErrorResponse> denegado(Exception e,HttpServletRequest r){return ResponseEntity.status(403).body(error(r,403,"Forbidden","El usuario autenticado no tiene privilegio o alcance para esta operación.",Map.of()));}
  @ExceptionHandler(NoEncontradoException.class) ResponseEntity<Dtos.ErrorResponse> noEncontrado(NoEncontradoException e,HttpServletRequest r){return ResponseEntity.status(404).body(error(r,404,"Not Found",e.getMessage(),Map.of()));}
  @ExceptionHandler(ConflictoNegocioException.class) ResponseEntity<Dtos.ErrorResponse> conflicto(ConflictoNegocioException e,HttpServletRequest r){return ResponseEntity.status(409).body(error(r,409,"Conflict",e.getMessage(),Map.of()));}
  @ExceptionHandler(Exception.class) ResponseEntity<Dtos.ErrorResponse> inesperado(Exception e,HttpServletRequest r){return ResponseEntity.status(500).body(error(r,500,"Internal Server Error","Ocurrió un error inesperado al procesar la solicitud.",Map.of()));}
}
