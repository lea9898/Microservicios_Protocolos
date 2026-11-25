package cl.duocuc.protocolos;
public record ProtocoloRequest(String numero, String cliente, String instalacion, String tag,
    String tipo, String tecnico, String fecha, Double temperatura, Double humedad,
    Double lectura1, Double lectura10, String comentario) {}
