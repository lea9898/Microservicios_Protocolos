package cl.duocuc.protocolos;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/protocolos") @RequiredArgsConstructor
public class ProtocoloController {
    private final ProtocoloRepository repo;
    @GetMapping public List<Protocolo> listar() { return repo.findAll(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Protocolo crear(@RequestBody ProtocoloRequest datos) {
        if (!ValidacionProtocolo.esValido(datos)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Datos del protocolo no válidos");
        if (repo.existsByNumero(datos.numero())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Número de protocolo repetido");
        Protocolo p = new Protocolo();
        p.setNumero(datos.numero()); p.setCliente(datos.cliente()); p.setInstalacion(datos.instalacion());
        p.setTag(datos.tag()); p.setTipo(datos.tipo()); p.setTecnico(datos.tecnico()); p.setFecha(datos.fecha());
        p.setTemperatura(datos.temperatura()); p.setHumedad(datos.humedad()); p.setLectura1(datos.lectura1());
        p.setLectura10(datos.lectura10()); p.setComentario(datos.comentario() == null ? "" : datos.comentario());
        p.setResultado(ValidacionProtocolo.resultado(datos)); p.setEstado("Registrado en terreno");
        return repo.save(p);
    }
}
