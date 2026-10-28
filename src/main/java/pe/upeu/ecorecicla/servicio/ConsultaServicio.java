package pe.upeu.ecorecicla.servicio;
import pe.upeu.ecorecicla.dominio.*;
import pe.upeu.ecorecicla.exception.NoEncontradoException;
import pe.upeu.ecorecicla.repositorio.*;
import pe.upeu.ecorecicla.web.dto.Dtos;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
@Service @Transactional(readOnly=true) public class ConsultaServicio {
 private final PuntoAcopioRepositorio puntos;private final RutaRepositorio rutas;private final RutaPuntoRepositorio rutaPuntos;private final PesajeRepositorio pesajes;private final AutenticacionServicio auth;
 public ConsultaServicio(PuntoAcopioRepositorio p,RutaRepositorio r,RutaPuntoRepositorio rp,PesajeRepositorio pj,AutenticacionServicio a){puntos=p;rutas=r;rutaPuntos=rp;pesajes=pj;auth=a;}
 public Dtos.Pagina<Dtos.PuntoResponse> listarPuntos(String zona,int pagina,int tamanio){validarPagina(pagina,tamanio);Pageable pg=PageRequest.of(pagina,tamanio,Sort.by("razonSocial").ascending());Page<PuntoAcopio> page=(zona==null||zona.isBlank())?puntos.findByActivoTrue(pg):puntos.findByActivoTrueAndZonaIgnoreCase(zona.trim(),pg);return new Dtos.Pagina<>(page.getContent().stream().map(Mapeos::punto).toList(),page.getNumber(),page.getSize(),page.getTotalElements(),page.getTotalPages(),page.isLast());}
 public Dtos.Pagina<Dtos.PesajeResponse> listarPesajes(long punto,int pagina,int tamanio){validarPagina(pagina,tamanio);if(!puntos.existsById(punto))throw new NoEncontradoException("El punto de acopio no existe.");Page<Pesaje> page=pesajes.findByPuntoIdOrderByFechaHoraDesc(punto,PageRequest.of(pagina,tamanio));return new Dtos.Pagina<>(page.getContent().stream().map(Mapeos::pesaje).toList(),page.getNumber(),page.getSize(),page.getTotalElements(),page.getTotalPages(),page.isLast());}
 public Dtos.RutaResponse ruta(LocalDate fecha,String username){Usuario u=auth.usuario(username);Ruta r=rutas.findByFechaAndOperarioId(fecha,u.getId()).orElseThrow(()->new NoEncontradoException("No hay una ruta asignada para esa fecha."));return ruta(r);}
 public Dtos.RutaResponse ruta(Ruta r){List<Dtos.RutaPuntoResponse> data=rutaPuntos.findByRutaIdOrderBySecuenciaAsc(r.getId()).stream().map(rp->new Dtos.RutaPuntoResponse(rp.getPunto().getId(),rp.getPunto().getRazonSocial(),rp.getPunto().getDireccion(),rp.getPunto().getZona(),rp.getPunto().getMaterialHabitual(),rp.getSecuencia(),pesajes.existsByRutaIdAndPuntoIdAndEstado(r.getId(),rp.getPunto().getId(),"REGISTRADO"),rp.getMotivoOmission())).toList();return new Dtos.RutaResponse(r.getId(),r.getFecha(),r.getEstado(),data);}
 public static void validarPagina(int p,int s){if(p<0)throw new IllegalArgumentException("La página debe ser mayor o igual que cero.");if(s<1||s>100)throw new IllegalArgumentException("El tamaño debe estar entre 1 y 100.");}
}
