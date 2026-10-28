package pe.upeu.ecorecicla.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Dtos {
  private Dtos() {}
  public record LoginRequest(@NotBlank String usuario, @NotBlank String contrasena) {}
  public record LoginResponse(String token, String tipo, long expiraEnSegundos, String nombre, String usuario, String rol, Set<String> privilegios) {}
  public record UsuarioResponse(long id, String usuario, String nombre, String rol, Set<String> privilegios) {}
  public record Pagina<T>(List<T> contenido,int pagina,int tamanio,long totalElementos,int totalPaginas,boolean ultima) {}
  public record PuntoResponse(long id,String razonSocial,String direccion,String zona,String materialHabitual) {}
  public record RutaPuntoResponse(long id,String razonSocial,String direccion,String zona,String materialHabitual,int secuencia,boolean atendido,String motivoOmission) {}
  public record RutaResponse(long id,LocalDate fecha,String estado,List<RutaPuntoResponse> puntos) {}
  public record PesajeRequest(@NotNull @DecimalMin(value="0.001", inclusive=true) BigDecimal pesoBruto,
      @NotNull @DecimalMin(value="0.000", inclusive=true) BigDecimal tara,
      @NotBlank @Size(max=60) String material,@Size(max=500) String observacion) {}
  public record AnularRequest(@NotBlank @Size(max=500) String motivo) {}
  public record OmisionRequest(@NotBlank @Size(max=500) String motivo) {}
  public record PesajeResponse(long id,long rutaId,long puntoId,Instant fechaHora,String material,BigDecimal pesoBruto,BigDecimal tara,BigDecimal pesoNeto,String observacion,String estado,String motivoAnulacion,String operario) {}
  public record PuntoCrearRequest(@NotBlank @Size(max=160) String razonSocial,@NotBlank @Size(max=240) String direccion,@NotBlank @Size(max=80) String zona,@NotBlank @Size(max=60) String materialHabitual) {}
  public record RutaCrearRequest(@NotNull LocalDate fecha,@NotNull Long operarioId,@NotEmpty List<@NotNull Long> puntosIds) {}
  public record ErrorResponse(Instant timestamp,int status,String error,String message,String path,Map<String,String> validationErrors) {}
}
