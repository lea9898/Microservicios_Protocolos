package cl.duocuc.roles.config;

import cl.duocuc.roles.model.Rol;
import cl.duocuc.roles.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final RolRepository rolRepository;

    @Override
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            rolRepository.saveAll(List.of(
                    Rol.builder().nombre("Administrador").build(),
                    Rol.builder().nombre("Supervisor").build(),
                    Rol.builder().nombre("Usuario").build()
            ));
        }
    }
}
