package cl.duocuc.usuarios.dto;

public record ActualizarUsuarioRequest(
        String nombre,
        String correo,
        String ciudad,
        Long rolId
) {
}
