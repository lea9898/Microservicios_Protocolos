package cl.duocuc.usuarios.dto;

public record RegistroRequest(
        String nombre,
        String correo,
        String contrasena,
        String ciudad,
        Long rolId
) {
}
