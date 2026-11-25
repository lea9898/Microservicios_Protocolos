package cl.duocuc.usuarios.config;

import cl.duocuc.usuarios.model.Usuario;
import cl.duocuc.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    usuario("Administrador Induztek", "admin@induztek.cl", "Santiago", 1L),
                    usuario("Marco Godoy", "marco.godoy@induztek.cl", "Lampa", 3L),
                    usuario("Diego Muñoz", "diego@duocuc.cl", "Valparaíso", 2L),
                    usuario("Valentina Soto", "valentina@duocuc.cl", "Concepción", 3L),
                    usuario("Matías Pérez", "matias@duocuc.cl", "Temuco", 3L),
                    usuario("Fernanda Díaz", "fernanda@duocuc.cl", "Santiago", 3L)
            ));
        }
    }

    private Usuario usuario(String nombre, String correo, String ciudad, Long rolId) {
        return Usuario.builder()
                .nombre(nombre)
                .correo(correo)
                .contrasena(rolId == 1L ? "Admin123!" : "Usuario123!")
                .ciudad(ciudad)
                .rolId(rolId)
                .build();
    }
}
