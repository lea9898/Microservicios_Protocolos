package cl.duocuc.equipos;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
@RestController @RequestMapping("/api/equipos") @RequiredArgsConstructor
public class EquipoController {
 private final EquipoRepository repo;
 @GetMapping public List<Equipo> listar(){ return repo.findAll(); }
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Equipo crear(@RequestBody EquipoRequest d){
 if(!ValidacionEquipo.esValido(d)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Datos invalidos");
 if(repo.existsByCodigo(d.codigo().trim())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Codigo repetido");
 Equipo e=new Equipo(); e.setCodigo(d.codigo().trim()); e.setNombre(d.nombre()); e.setMarca(d.marca()); e.setModelo(d.modelo()); e.setSerie(d.serie()); e.setEstado(d.estado()); e.setInstalacion(d.instalacion()); return repo.save(e);
 }
}