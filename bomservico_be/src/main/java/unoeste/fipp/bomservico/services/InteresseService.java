package unoeste.fipp.bomservico.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unoeste.fipp.bomservico.entities.Interesse;
import unoeste.fipp.bomservico.repositories.InteresseRepository;

import java.util.List;

@Service
public class InteresseService {

    @Autowired
    private InteresseRepository repo;

    public List<Interesse> getAll() {
        return repo.findAll();
    }

    public Interesse getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Interesse> getByAnuncio(Long anuncioId) {
        return repo.findByAnuncioId(anuncioId);
    }

    public List<Interesse> getByPrestador(String login) {
        return repo.findByAnuncioUsuarioLogin(login);
    }

    public Interesse save(Interesse i) {
        return repo.save(i);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
