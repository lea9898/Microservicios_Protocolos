package cl.duocuc.equipos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ValidacionEquipoTest {
 private EquipoRequest equipo(String codigo,String nombre,String estado){return new EquipoRequest(codigo,nombre,"Marca","Modelo","Serie",estado,"Planta");}
 @Test void aceptaEquipoValido(){assertTrue(ValidacionEquipo.esValido(equipo("TR-01","Transformador","Operativo")));}
 @Test void rechazaCodigoVacio(){assertFalse(ValidacionEquipo.esValido(equipo("","Transformador","Operativo")));}
 @Test void rechazaEstadoIncorrecto(){assertFalse(ValidacionEquipo.esValido(equipo("TR-01","Transformador","Otro")));}
}