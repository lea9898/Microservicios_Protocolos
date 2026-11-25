package cl.duocuc.protocolos;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
public final class ValidacionProtocolo {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    private ValidacionProtocolo() {}
    public static boolean esValido(ProtocoloRequest p) {
        if (p == null || vacio(p.numero()) || vacio(p.cliente()) || vacio(p.instalacion()) ||
            vacio(p.tag()) || vacio(p.tipo()) || vacio(p.tecnico()) || p.comentario() != null && p.comentario().length() > 500) return false;
        if (p.temperatura() == null || p.humedad() == null || p.lectura1() == null || p.lectura10() == null) return false;
        if (!Double.isFinite(p.temperatura()) || !Double.isFinite(p.humedad()) || !Double.isFinite(p.lectura1()) || !Double.isFinite(p.lectura10())) return false;
        if (p.temperatura() < -20 || p.temperatura() > 70 || p.humedad() < 0 || p.humedad() > 100 || p.lectura1() <= 0 || p.lectura10() <= 0) return false;
        try { LocalDate.parse(p.fecha(), FORMATO); return true; } catch (Exception e) { return false; }
    }
    public static String resultado(ProtocoloRequest p) {
        double indice = p.lectura10() / p.lectura1();
        if (p.lectura1() < 100 || indice < 1) return "Rechazado";
        if (indice < 2) return "Observado";
        return "Aprobado";
    }
    private static boolean vacio(String v) { return v == null || v.isBlank(); }
}
