package cl.duocuc.protocolos;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProtocoloRepository extends JpaRepository<Protocolo,Long> {
    boolean existsByNumero(String numero);
}
