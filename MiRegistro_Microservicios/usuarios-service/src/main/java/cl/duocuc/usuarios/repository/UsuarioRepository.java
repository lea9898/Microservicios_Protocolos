package cl.duocuc.usuarios.repository;

import cl.duocuc.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByOrderByNombreAsc();

    List<Usuario> findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc(String nombre, String correo);

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndIdNot(String correo, Long id);
}
