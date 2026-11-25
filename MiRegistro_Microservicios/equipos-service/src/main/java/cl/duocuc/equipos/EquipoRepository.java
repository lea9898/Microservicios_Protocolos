package cl.duocuc.equipos;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EquipoRepository extends JpaRepository<Equipo,Long> { boolean existsByCodigo(String codigo); }