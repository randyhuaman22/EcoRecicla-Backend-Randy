package pe.upeu.ecorecicla.servicio;

import java.util.*;
import java.util.stream.Collectors;
import pe.upeu.ecorecicla.dominio.*;
import pe.upeu.ecorecicla.web.dto.Dtos;

public final class Mapeos {
  private Mapeos() {}

  public static Set<String> privilegios(Usuario u) {
    return u.getRoles().stream()
        .flatMap(r -> r.getPrivilegios().stream())
        .map(Privilegio::getCodigo)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  public static String rol(Usuario u) {
    return u.getRoles().stream().map(Rol::getCodigo).sorted().findFirst().orElse("");
  }

  public static Dtos.PesajeResponse pesaje(Pesaje p) {
    return new Dtos.PesajeResponse(
        p.getId(),
        p.getRuta().getId(),
        p.getPunto().getId(),
        p.getFechaHora(),
        p.getMaterial(),
        p.getPesoBruto(),
        p.getTara(),
        p.getPesoNeto(),
        p.getObservacion(),
        p.getEstado(),
        p.getMotivoAnulacion(),
        p.getAutor().getUsername());
  }

  public static Dtos.PuntoResponse punto(PuntoAcopio p) {
    return new Dtos.PuntoResponse(
        p.getId(), p.getRazonSocial(), p.getDireccion(), p.getZona(), p.getMaterialHabitual());
  }
}
