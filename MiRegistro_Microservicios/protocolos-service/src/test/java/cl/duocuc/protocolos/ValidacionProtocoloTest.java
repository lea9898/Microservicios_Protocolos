package cl.duocuc.protocolos;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/** 7 pruebas unitarias siguiendo el estilo @Test y assert de la plantilla. */
class ValidacionProtocoloTest {
    private ProtocoloRequest base() { return new ProtocoloRequest("PT-2026-0001","Cliente","Planta","TR-01","Transformador","Tecnico","23-11-2026",20.0,50.0,100.0,220.0,""); }
    private ProtocoloRequest cambiar(String campo,Object valor) {
        ProtocoloRequest p=base();
        return new ProtocoloRequest(campo.equals("numero")?(String)valor:p.numero(),campo.equals("cliente")?(String)valor:p.cliente(),p.instalacion(),campo.equals("tag")?(String)valor:p.tag(),p.tipo(),p.tecnico(),campo.equals("fecha")?(String)valor:p.fecha(),campo.equals("temperatura")?(Double)valor:p.temperatura(),campo.equals("humedad")?(Double)valor:p.humedad(),campo.equals("lectura1")?(Double)valor:p.lectura1(),campo.equals("lectura10")?(Double)valor:p.lectura10(),p.comentario());
    }
    @Test void aceptaProtocoloValido() { assertTrue(ValidacionProtocolo.esValido(base())); }
    @Test void rechazaNumeroVacio() { assertFalse(ValidacionProtocolo.esValido(cambiar("numero",""))); }
    @Test void rechazaFechaInvalida() { assertFalse(ValidacionProtocolo.esValido(cambiar("fecha","31-02-2026"))); }
    @Test void rechazaHumedadSuperiorACien() { assertFalse(ValidacionProtocolo.esValido(cambiar("humedad",101.0))); }
    @Test void rechazaTemperaturaAlta() { assertFalse(ValidacionProtocolo.esValido(cambiar("temperatura",71.0))); }
    @Test void rechazaLecturaCero() { assertFalse(ValidacionProtocolo.esValido(cambiar("lectura1",0.0))); }
    @Test void calculaAprobado() { assertEquals("Aprobado",ValidacionProtocolo.resultado(base())); }
}
