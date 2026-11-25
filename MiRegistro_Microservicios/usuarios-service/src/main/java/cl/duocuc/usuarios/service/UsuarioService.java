package cl.duocuc.usuarios.service;

import cl.duocuc.usuarios.client.RolClient;
import cl.duocuc.usuarios.dto.ActualizarUsuarioRequest;
import cl.duocuc.usuarios.dto.CambiarContrasenaRequest;
import cl.duocuc.usuarios.dto.LoginRequest;
import cl.duocuc.usuarios.dto.RegistroRequest;
import cl.duocuc.usuarios.dto.RolDto;
import cl.duocuc.usuarios.dto.UsuarioResponse;
import cl.duocuc.usuarios.model.Usuario;
import cl.duocuc.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Long ROL_ESTUDIANTE = 3L;

    private final UsuarioRepository usuarioRepository;
    private final RolClient rolClient;
    private final AlmacenamientoService almacenamientoService;

    public List<UsuarioResponse> listar(String buscar) {
        List<Usuario> usuarios = (buscar == null || buscar.isBlank())
                ? usuarioRepository.findAllByOrderByNombreAsc()
                : usuarioRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc(buscar, buscar);

        Map<Long, String> roles = rolClient.listar().stream()
                .collect(Collectors.toMap(RolDto::id, RolDto::nombre));

        return usuarios.stream()
                .map(usuario -> aRespuesta(usuario, roles.getOrDefault(usuario.getRolId(), "")))
                .toList();
    }

    public UsuarioResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    public UsuarioResponse registrar(RegistroRequest datos) {
        if (vacio(datos.nombre()) || vacio(datos.correo()) || vacio(datos.ciudad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre, correo y ciudad son obligatorios");
        }
        if (datos.contrasena() == null || datos.contrasena().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener mínimo 6 caracteres");
        }
        if (usuarioRepository.existsByCorreo(datos.correo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado");
        }

        Long rolId = ROL_ESTUDIANTE; // Registro publico: nunca asignar rol de administrador
        validarRol(rolId);

        Usuario nuevo = Usuario.builder()
                .nombre(datos.nombre())
                .correo(datos.correo())
                .contrasena(datos.contrasena())
                .ciudad(datos.ciudad())
                .rolId(rolId)
                .build();
        return aRespuesta(usuarioRepository.save(nuevo));
    }

    public UsuarioResponse iniciarSesion(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByCorreo(datos.correo())
                .filter(u -> u.getContrasena().equals(datos.contrasena()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
        return aRespuesta(usuario);
    }

    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest datos) {
        Usuario usuario = buscar(id);

        if (vacio(datos.nombre()) || vacio(datos.correo()) || vacio(datos.ciudad())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre, correo y ciudad son obligatorios");
        }
        if (usuarioRepository.existsByCorreoAndIdNot(datos.correo(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya está registrado");
        }
        if (datos.rolId() != null) {
            validarRol(datos.rolId());
            usuario.setRolId(datos.rolId());
        }

        usuario.setNombre(datos.nombre());
        usuario.setCorreo(datos.correo());
        usuario.setCiudad(datos.ciudad());
        return aRespuesta(usuarioRepository.save(usuario));
    }

    public void cambiarContrasena(Long id, CambiarContrasenaRequest datos) {
        Usuario usuario = buscar(id);

        if (!usuario.getContrasena().equals(datos.actual())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña actual es incorrecta");
        }
        if (datos.nueva() == null || datos.nueva().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener mínimo 6 caracteres");
        }

        usuario.setContrasena(datos.nueva());
        usuarioRepository.save(usuario);
    }

    public UsuarioResponse subirAvatar(Long id, MultipartFile archivo) {
        Usuario usuario = buscar(id);
        String anterior = usuario.getAvatar();

        usuario.setAvatar(almacenamientoService.guardar(archivo));
        Usuario guardado = usuarioRepository.save(usuario);

        almacenamientoService.eliminar(anterior);
        return aRespuesta(guardado);
    }

    public byte[] obtenerAvatar(Long id) {
        Usuario usuario = buscar(id);
        if (usuario.getAvatar() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no tiene foto");
        }
        return almacenamientoService.leer(usuario.getAvatar());
    }

    public void eliminar(Long id) {
        Usuario usuario = buscar(id);
        usuarioRepository.delete(usuario);
        almacenamientoService.eliminar(usuario.getAvatar());
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void validarRol(Long rolId) {
        if (rolClient.obtener(rolId) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol indicado no existe");
        }
    }

    private UsuarioResponse aRespuesta(Usuario usuario) {
        RolDto rol = rolClient.obtener(usuario.getRolId());
        return aRespuesta(usuario, rol != null ? rol.nombre() : "");
    }

    private UsuarioResponse aRespuesta(Usuario usuario, String rolNombre) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getCiudad(),
                usuario.getRolId(),
                rolNombre,
                urlAvatar(usuario)
        );
    }

    private String urlAvatar(Usuario usuario) {
        if (usuario.getAvatar() == null) {
            return null;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/usuarios/" + usuario.getId() + "/avatar")
                .queryParam("v", usuario.getAvatar())
                .toUriString();
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
