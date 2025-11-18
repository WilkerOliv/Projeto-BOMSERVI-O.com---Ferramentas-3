package unoeste.fipp.bomservico.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unoeste.fipp.bomservico.entities.Anuncio;
import unoeste.fipp.bomservico.repositories.AnuncioRepository;
import java.util.List;
@Service
public class AnuncioService {
    @Autowired private AnuncioRepository repo;
    public List<Anuncio> listarTodos(){ return repo.findAll(); }
    public Anuncio buscarPorId(Long id){ return repo.findById(id).orElse(null); }
    public List<Anuncio> getByUser(String login){ return repo.findByUsuario_Login(login); }
    public Anuncio salvar(Anuncio a){ return repo.save(a); }
    public void deletar(Long id){ repo.deleteById(id); }
}