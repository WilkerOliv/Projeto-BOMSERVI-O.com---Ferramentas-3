package unoeste.fipp.bomservico.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import unoeste.fipp.bomservico.entities.Interesse;

import java.util.List;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {

    // Buscar mensagens destinadas ao prestador (DONO DO ANÚNCIO)
    List<Interesse> findByAnuncioUsuarioLogin(String login);

    // Buscar mensagens recebidas de um anúncio específico
    List<Interesse> findByAnuncioId(Long anuncioId);
}
