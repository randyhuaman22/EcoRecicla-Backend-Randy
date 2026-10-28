package pe.upeu.ecorecicla.web;
import jakarta.validation.Valid;
import pe.upeu.ecorecicla.servicio.PesajeServicio;
import pe.upeu.ecorecicla.web.dto.Dtos;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1")
public class PesajeControlador {
 private final PesajeServicio servicio;public PesajeControlador(PesajeServicio s){servicio=s;}
 @PostMapping("/rutas/{rutaId}/puntos/{puntoId}/pesajes") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('PESAJE_CREAR')") public Dtos.PesajeResponse crear(@PathVariable long rutaId,@PathVariable long puntoId,@Valid @RequestBody Dtos.PesajeRequest q,Authentication a){return servicio.crear(rutaId,puntoId,q,a.getName());}
 @PutMapping("/pesajes/{id}") @PreAuthorize("hasAuthority('PESAJE_EDITAR')") public Dtos.PesajeResponse corregir(@PathVariable long id,@Valid @RequestBody Dtos.PesajeRequest q,Authentication a){return servicio.corregir(id,q,a);}
 @PostMapping("/pesajes/{id}/anulacion") @PreAuthorize("hasAuthority('PESAJE_ANULAR')") public Dtos.PesajeResponse anular(@PathVariable long id,@Valid @RequestBody Dtos.AnularRequest q,Authentication a){return servicio.anular(id,q.motivo(),a.getName());}
}
