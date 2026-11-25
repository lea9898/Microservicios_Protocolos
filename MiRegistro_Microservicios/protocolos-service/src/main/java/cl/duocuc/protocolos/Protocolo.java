package cl.duocuc.protocolos;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @Entity @Table(name="protocolos")
public class Protocolo {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String numero;
    private String cliente, instalacion, tag, tipo, tecnico, fecha;
    private Double temperatura, humedad, lectura1, lectura10;
    private String resultado, estado;
    @Column(length=500) private String comentario;
}
