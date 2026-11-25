package cl.duocuc.equipos;
import java.util.Set;
public class ValidacionEquipo {
 private static boolean texto(String s) { return s != null && !s.isBlank() && s.length() <= 150; }
 public static boolean esValido(EquipoRequest e) { return e != null && texto(e.codigo()) && texto(e.nombre()) && texto(e.marca()) && texto(e.modelo()) && texto(e.serie()) && texto(e.instalacion()) && e.estado()!=null && Set.of("Operativo","Mantenimiento","Fuera de servicio").contains(e.estado()); }
}