package unoeste.fipp.bomservico.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import unoeste.fipp.bomservico.entities.Anuncio;
import java.util.List;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    List<Anuncio> findByUsuario_Login(String login);
}
