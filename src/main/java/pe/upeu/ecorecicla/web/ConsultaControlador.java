package pe.upeu.ecorecicla.web;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.upeu.ecorecicla.servicio.ConsultaServicio;
import pe.upeu.ecorecicla.web.dto.Dtos;

@RestController
@RequestMapping("/api/v1")
public class ConsultaControlador {
  private final ConsultaServicio servicio;

  public ConsultaControlador(ConsultaServicio s) {
    servicio = s;
  }

  @GetMapping("/rutas/asignada")
  @PreAuthorize("hasAuthority('PUNTO_LEER')")
  public Dtos.RutaResponse ruta(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
      Authentication a) {
    return servicio.ruta(fecha, a.getName());
  }

  @GetMapping("/puntos")
  @PreAuthorize("hasAuthority('PUNTO_LEER')")
  public Dtos.Pagina<Dtos.PuntoResponse> puntos(
      @RequestParam(required = false) String zona,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanio) {
    return servicio.listarPuntos(zona, pagina, tamanio);
  }

  @GetMapping("/puntos/{puntoId}/pesajes")
  @PreAuthorize("hasAuthority('PESAJE_LEER')")
  public Dtos.Pagina<Dtos.PesajeResponse> pesajes(
      @PathVariable long puntoId,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(defaultValue = "20") int tamanio) {
    return servicio.listarPesajes(puntoId, pagina, tamanio);
  }
}
