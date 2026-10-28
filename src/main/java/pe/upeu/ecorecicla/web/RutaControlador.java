package pe.upeu.ecorecicla.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.upeu.ecorecicla.servicio.RutaServicio;
import pe.upeu.ecorecicla.web.dto.Dtos;

@RestController
@RequestMapping("/api/v1")
public class RutaControlador {
  private final RutaServicio servicio;

  public RutaControlador(RutaServicio s) {
    servicio = s;
  }

  @PostMapping("/rutas/{id}/cierre")
  @PreAuthorize("hasAuthority('RUTA_CERRAR')")
  public Dtos.RutaResponse cerrar(@PathVariable long id, Authentication a) {
    return servicio.cerrar(id, a.getName());
  }

  @PostMapping("/rutas/{rutaId}/puntos/{puntoId}/omision")
  @PreAuthorize("hasAuthority('RUTA_CERRAR')")
  public Dtos.RutaResponse omitir(
      @PathVariable long rutaId,
      @PathVariable long puntoId,
      @Valid @RequestBody Dtos.OmisionRequest q,
      Authentication a) {
    return servicio.omitir(rutaId, puntoId, q.motivo(), a.getName());
  }

  @PostMapping("/puntos")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('PUNTO_GESTIONAR')")
  public Dtos.PuntoResponse punto(@Valid @RequestBody Dtos.PuntoCrearRequest q, Authentication a) {
    return servicio.crearPunto(q, a.getName());
  }

  @PostMapping("/rutas")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('PUNTO_GESTIONAR')")
  public Dtos.RutaResponse ruta(@Valid @RequestBody Dtos.RutaCrearRequest q) {
    return servicio.crearRuta(q);
  }
}
