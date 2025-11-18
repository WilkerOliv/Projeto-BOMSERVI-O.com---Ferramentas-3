package unoeste.fipp.bomservico.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unoeste.fipp.bomservico.entities.Categoria;
import unoeste.fipp.bomservico.repositories.CategoriaRepository;
import java.util.List;
@Service
public class CategoriaService {
    @Autowired private CategoriaRepository repo;
    public List<Categoria> getAll(){ return repo.findAll(); }
    public Categoria getById(Long id){ return repo.findById(id).orElse(null); }
    public Categoria save(Categoria c){ return repo.save(c); }
    public void delete(Long id){ repo.deleteById(id); }
}