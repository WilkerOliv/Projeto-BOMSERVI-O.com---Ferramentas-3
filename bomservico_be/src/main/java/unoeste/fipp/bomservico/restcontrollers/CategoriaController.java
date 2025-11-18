package unoeste.fipp.bomservico.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import unoeste.fipp.bomservico.services.CategoriaService;
import unoeste.fipp.bomservico.entities.Categoria;
import java.util.List;
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService service;

    // GET → público
    @GetMapping
    public List<Categoria> all() {
        return service.getAll();
    }

    // A partir daqui, apenas ADMIN ↓↓↓

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Categoria create(@RequestBody Categoria c){
        return service.save(c);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Categoria update(@PathVariable Long id, @RequestBody Categoria c){
        c.setId(id);
        return service.save(c);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
