package unoeste.fipp.bomservico.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import unoeste.fipp.bomservico.entities.Foto;
import java.util.List;
public interface FotoRepository extends JpaRepository<Foto, Long> {
    List<Foto> findByAnuncio_Id(Long anuncioId);
}