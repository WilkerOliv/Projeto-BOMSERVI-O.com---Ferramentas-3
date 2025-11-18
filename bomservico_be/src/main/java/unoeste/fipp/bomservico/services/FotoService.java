package unoeste.fipp.bomservico.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unoeste.fipp.bomservico.entities.Foto;
import unoeste.fipp.bomservico.repositories.FotoRepository;
import java.util.List;
@Service
public class FotoService {
    @Autowired private FotoRepository repo;
    public List<Foto> getAll(){ return repo.findAll(); }
    public Foto getById(Long id){ return repo.findById(id).orElse(null); }
    public List<Foto> getByAnuncio(Long anuncioId){ return repo.findByAnuncio_Id(anuncioId); }
    public Foto save(Foto f){ return repo.save(f); }
    public void delete(Long id){ repo.deleteById(id); }
}