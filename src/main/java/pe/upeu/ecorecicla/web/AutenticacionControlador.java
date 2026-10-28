package pe.upeu.ecorecicla.web;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.upeu.ecorecicla.servicio.AutenticacionServicio;
import pe.upeu.ecorecicla.web.dto.Dtos;

@RestController
@RequestMapping("/api/v1")
public class AutenticacionControlador {
  private final AutenticacionServicio servicio;

  public AutenticacionControlador(AutenticacionServicio s) {
    servicio = s;
  }

  @PostMapping("/auth/login")
  public Dtos.LoginResponse login(@Valid @RequestBody Dtos.LoginRequest q) {
    return servicio.login(q);
  }

  @GetMapping("/usuarios/actual")
  public Dtos.UsuarioResponse actual(Authentication a) {
    return servicio.actual(a.getName());
  }
}
