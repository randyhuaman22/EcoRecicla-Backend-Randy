package pe.upeu.ecorecicla.config;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import pe.upeu.ecorecicla.dominio.*;
import pe.upeu.ecorecicla.repositorio.*;

@Configuration
public class DatosSemilla {
  private static final ZoneId ZONA = ZoneId.of("America/Lima");

  @Bean
  CommandLineRunner cargarDatos(
      UsuarioRepositorio usuarios,
      RolRepositorio roles,
      PrivilegioRepositorio privilegios,
      PuntoAcopioRepositorio puntos,
      RutaRepositorio rutas,
      RutaPuntoRepositorio rutaPuntos,
      PesajeRepositorio pesajes,
      PasswordEncoder encoder,
      PlatformTransactionManager transactionManager) {
    return args ->
        new TransactionTemplate(transactionManager)
            .executeWithoutResult(
                status ->
                    sembrar(
                        usuarios, roles, privilegios, puntos, rutas, rutaPuntos, pesajes, encoder));
  }

  @Transactional
  public void sembrar(
      UsuarioRepositorio usuarios,
      RolRepositorio roles,
      PrivilegioRepositorio privilegios,
      PuntoAcopioRepositorio puntos,
      RutaRepositorio rutas,
      RutaPuntoRepositorio rutaPuntos,
      PesajeRepositorio pesajes,
      PasswordEncoder encoder) {
    List<String> codigos =
        List.of(
            "PUNTO_LEER",
            "PESAJE_LEER",
            "PESAJE_CREAR",
            "PESAJE_EDITAR",
            "PESAJE_EDITAR_TODOS",
            "PESAJE_ANULAR",
            "RUTA_CERRAR",
            "PUNTO_GESTIONAR");
    Map<String, Privilegio> ps = new HashMap<>();
    for (String c : codigos)
      ps.put(c, privilegios.findByCodigo(c).orElseGet(() -> privilegios.save(new Privilegio(c))));
    Map<String, Set<String>> matriz =
        Map.of(
            "OPERARIO", Set.of("PUNTO_LEER", "PESAJE_LEER", "PESAJE_CREAR", "PESAJE_EDITAR"),
            "SUPERVISOR",
                Set.of(
                    "PUNTO_LEER",
                    "PESAJE_LEER",
                    "PESAJE_CREAR",
                    "PESAJE_EDITAR",
                    "PESAJE_EDITAR_TODOS",
                    "PESAJE_ANULAR",
                    "RUTA_CERRAR"),
            "ADMINISTRADOR", Set.of("PUNTO_LEER", "PESAJE_LEER", "PUNTO_GESTIONAR"));
    Map<String, Rol> rs = new HashMap<>();
    for (var e : matriz.entrySet()) {
      Rol rol = roles.findByCodigo(e.getKey()).orElseGet(() -> roles.save(new Rol(e.getKey())));
      e.getValue().forEach(c -> rol.getPrivilegios().add(ps.get(c)));
      rs.put(e.getKey(), roles.save(rol));
    }
    Usuario op = usuario(usuarios, encoder, "operario1", "Operario de Ruta 1", rs.get("OPERARIO"));
    Usuario supervisor =
        usuario(
            usuarios, encoder, "supervisor1", "Supervisión de Operaciones", rs.get("SUPERVISOR"));
    usuario(usuarios, encoder, "admin", "Administración EcoRecicla", rs.get("ADMINISTRADOR"));
    Usuario op2 = usuario(usuarios, encoder, "operario2", "Operario de Ruta 2", rs.get("OPERARIO"));
    if (puntos.count() == 0) {
      String[] zonas = {"Centro", "Norte", "Sur"};
      String[] mats = {"PAPEL", "PLASTICO", "VIDRIO", "METAL"};
      for (int i = 0; i < 12; i++) {
        PuntoAcopio p = new PuntoAcopio();
        p.setRazonSocial("Empresa de Acopio %02d S.A.C.".formatted(i + 1));
        p.setDireccion("Avenida Reciclaje %d".formatted(100 + i));
        p.setZona(zonas[i / 4]);
        p.setMaterialHabitual(mats[i % 4]);
        p.setCreadoPor(op);
        puntos.save(p);
      }
    }
    List<PuntoAcopio> todos =
        puntos.findAllById(java.util.stream.LongStream.rangeClosed(1, 12).boxed().toList());
    LocalDate hoy = LocalDate.now(ZONA), ayer = hoy.minusDays(1);
    Ruta abierta =
        rutas.findByFechaAndOperarioId(hoy, op.getId()).orElseGet(() -> nuevaRuta(hoy, op, rutas));
    Ruta cerrada =
        rutas
            .findByFechaAndOperarioId(ayer, op.getId())
            .orElseGet(() -> nuevaRuta(ayer, op, rutas));
    if (rutaPuntos.findByRutaIdOrderBySecuenciaAsc(abierta.getId()).isEmpty())
      for (int i = 0; i < 6; i++) rutaPuntos.save(enlazar(abierta, todos.get(i), i + 1));
    if (rutaPuntos.findByRutaIdOrderBySecuenciaAsc(cerrada.getId()).isEmpty())
      for (int i = 0; i < 6; i++) rutaPuntos.save(enlazar(cerrada, todos.get(i + 6), i + 1));
    if (pesajes.count() == 0) {
      List<RutaPunto> planHoy = rutaPuntos.findByRutaIdOrderBySecuenciaAsc(abierta.getId());
      List<RutaPunto> planAyer = rutaPuntos.findByRutaIdOrderBySecuenciaAsc(cerrada.getId());
      for (int i = 0; i < 8; i++)
        guardarPesaje(
            pesajes,
            i < 5 ? abierta : cerrada,
            i < 5 ? planHoy.get(i % 3).getPunto() : planAyer.get(i % 6).getPunto(),
            i == 4 ? op2 : op,
            Instant.now().minusSeconds(i * 3600L),
            i == 2);
      for (int i = 0; i < 8; i++)
        guardarPesaje(
            pesajes,
            cerrada,
            planAyer.get(i % 6).getPunto(),
            op,
            ayer.atTime(9, 0).atZone(ZONA).toInstant().plusSeconds(i * 1800L),
            false);
    }
    if (!"CERRADA".equals(cerrada.getEstado())) {
      cerrada.setEstado("CERRADA");
      cerrada.setCerradaPor(supervisor);
      cerrada.setCerradaEn(ayer.atTime(17, 0).atZone(ZONA).toInstant());
      rutas.save(cerrada);
    }
  }

  private static Usuario usuario(
      UsuarioRepositorio repo, PasswordEncoder enc, String username, String nombre, Rol rol) {
    Usuario u =
        repo.findByUsernameIgnoreCase(username)
            .orElseGet(
                () -> {
                  Usuario n = new Usuario();
                  n.setUsername(username);
                  n.setNombre(nombre);
                  n.setPasswordHash(enc.encode("Eco2026*"));
                  n.setActivo(true);
                  return repo.save(n);
                });
    u.getRoles().add(rol);
    return repo.save(u);
  }

  private static Ruta nuevaRuta(LocalDate d, Usuario u, RutaRepositorio r) {
    Ruta x = new Ruta();
    x.setFecha(d);
    x.setEstado("ABIERTA");
    x.setOperario(u);
    return r.save(x);
  }

  private static RutaPunto enlazar(Ruta r, PuntoAcopio p, int seq) {
    RutaPunto rp = new RutaPunto();
    rp.setRuta(r);
    rp.setPunto(p);
    rp.setSecuencia(seq);
    return rp;
  }

  private static void guardarPesaje(
      PesajeRepositorio repo, Ruta r, PuntoAcopio p, Usuario autor, Instant t, boolean anulada) {
    Pesaje x = new Pesaje();
    x.setRuta(r);
    x.setPunto(p);
    x.setAutor(autor);
    x.setFechaHora(t);
    x.setMaterial(p.getMaterialHabitual());
    x.setPesoBruto(new BigDecimal("25.000"));
    x.setTara(new BigDecimal("2.000"));
    x.setPesoNeto(new BigDecimal("23.000"));
    x.setEstado(anulada ? "ANULADO" : "REGISTRADO");
    x.setMotivoAnulacion(anulada ? "Registro de demostración anulado" : null);
    repo.save(x);
  }
}
