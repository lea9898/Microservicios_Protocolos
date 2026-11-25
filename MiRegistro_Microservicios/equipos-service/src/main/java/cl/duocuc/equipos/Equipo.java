package cl.duocuc.equipos;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter @Entity @Table(name="equipos") public class Equipo {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String codigo;
 private String nombre, marca, modelo, serie, estado, instalacion;
}