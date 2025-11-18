package unoeste.fipp.bomservico.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import unoeste.fipp.bomservico.entities.Interesse;
import unoeste.fipp.bomservico.repositories.InteresseRepository;

import java.util.List;

@RestController
@RequestMapping("/api/interesses")
@CrossOrigin(origins = {"http://localhost:5000","http://127.0.0.1:5500","http://localhost:5501"}, allowCredentials = "true")
public class InteresseController {

    @Autowired
    private InteresseRepository repo;

    // Criar interesse (mensagem do visitante)
    @PostMapping
    public Interesse create(@RequestBody Interesse i) {
        return repo.save(i);
    }

    // Listar interesses de um anúncio específico
    @GetMapping("/by-anuncio/{id}")
    public List<Interesse> getByAnuncio(@PathVariable Long id) {
        return repo.findByAnuncioId(id);
    }

    // ★★★ ESSA ROTA AQUI É A QUE ESTAVA FALTANDO ★★★
    @GetMapping("/by-prestador/{login}")
    public List<Interesse> getByPrestador(@PathVariable String login) {
        return repo.findByAnuncioUsuarioLogin(login);
    }

    // Excluir uma mensagem
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
