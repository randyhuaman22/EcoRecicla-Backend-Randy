package pe.upeu.ecorecicla.servicio;
import pe.upeu.ecorecicla.dominio.*;
import pe.upeu.ecorecicla.exception.*;
import pe.upeu.ecorecicla.repositorio.*;
import pe.upeu.ecorecicla.web.dto.Dtos;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;

@Service public class PesajeServicio {
 private static final ZoneId ZONA=ZoneId.of("America/Lima");
 private final PesajeRepositorio pesajes;private final RutaRepositorio rutas;private final PuntoAcopioRepositorio puntos;private final RutaPuntoRepositorio rutaPuntos;private final AutenticacionServicio auth;
 public PesajeServicio(PesajeRepositorio p,RutaRepositorio r,PuntoAcopioRepositorio pa,RutaPuntoRepositorio rp,AutenticacionServicio a){pesajes=p;rutas=r;puntos=pa;rutaPuntos=rp;auth=a;}
 @Transactional public Dtos.PesajeResponse crear(long rutaId,long puntoId,Dtos.PesajeRequest req,String username){Usuario autor=auth.usuario(username);Ruta ruta=rutas.findByIdForUpdate(rutaId).orElseThrow(()->new NoEncontradoException("La ruta no existe."));abierta(ruta);RutaPunto rp=rutaPuntos.findByRutaIdAndPuntoId(rutaId,puntoId).orElseThrow(()->new ConflictoNegocioException("El punto no está planificado en esta ruta."));if(!rp.getRuta().getOperario().getId().equals(autor.getId())&&!tiene("PESAJE_EDITAR_TODOS"))throw new org.springframework.security.access.AccessDeniedException("Ruta no asignada al usuario.");return Mapeos.pesaje(pesajes.save(nuevo(ruta,rp.getPunto(),autor,req)));}
 @Transactional public Dtos.PesajeResponse corregir(long id,Dtos.PesajeRequest req,Authentication principal){Pesaje p=pesajes.findById(id).orElseThrow(()->new NoEncontradoException("El pesaje no existe."));Ruta r=rutas.findByIdForUpdate(p.getRuta().getId()).orElseThrow(()->new NoEncontradoException("La ruta no existe."));abierta(r);if("ANULADO".equals(p.getEstado()))throw new ConflictoNegocioException("No se puede corregir un pesaje anulado.");Usuario actor=auth.usuario(principal.getName());boolean supervisor=principal.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("PESAJE_EDITAR_TODOS"));if(!supervisor&&(!p.getAutor().getId().equals(actor.getId())||!p.getFechaHora().atZone(ZONA).toLocalDate().equals(LocalDate.now(ZONA))))throw new org.springframework.security.access.AccessDeniedException("El operario solo puede corregir sus propios pesajes del día.");actualizar(p,req,actor);return Mapeos.pesaje(pesajes.save(p));}
 @Transactional public Dtos.PesajeResponse anular(long id,String motivo,String username){Pesaje p=pesajes.findById(id).orElseThrow(()->new NoEncontradoException("El pesaje no existe."));if("ANULADO".equals(p.getEstado()))throw new ConflictoNegocioException("El pesaje ya está anulado.");p.setEstado("ANULADO");p.setMotivoAnulacion(motivo);p.setActualizadoEn(Instant.now());p.setModificadoPor(auth.usuario(username));return Mapeos.pesaje(pesajes.save(p));}
 private Pesaje nuevo(Ruta r,PuntoAcopio p,Usuario autor,Dtos.PesajeRequest q){Pesaje x=new Pesaje();x.setRuta(r);x.setPunto(p);x.setAutor(autor);x.setFechaHora(Instant.now());actualizar(x,q,autor);x.setCreadoEn(Instant.now());return x;}
 private void actualizar(Pesaje p,Dtos.PesajeRequest q,Usuario actor){BigDecimal bruto=q.pesoBruto().setScale(3,RoundingMode.HALF_UP),tara=q.tara().setScale(3,RoundingMode.HALF_UP),neto=bruto.subtract(tara);if(neto.signum()<=0)throw new ValidacionNegocioException("pesoBruto","El peso neto (peso bruto menos tara) debe ser mayor que cero.");p.setPesoBruto(bruto);p.setTara(tara);p.setPesoNeto(neto);p.setMaterial(q.material());p.setObservacion(q.observacion());p.setEstado("REGISTRADO");p.setMotivoAnulacion(null);p.setModificadoPor(actor);p.setActualizadoEn(Instant.now());}
 private void abierta(Ruta r){if("CERRADA".equals(r.getEstado()))throw new ConflictoNegocioException("No se puede registrar ni corregir pesajes porque la ruta está cerrada.");}
 private boolean tiene(String p){var a=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();return a!=null&&a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals(p));}
}
