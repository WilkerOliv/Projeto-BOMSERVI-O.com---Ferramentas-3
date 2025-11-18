package unoeste.fipp.bomservico.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unoeste.fipp.bomservico.entities.Anuncio;
import unoeste.fipp.bomservico.services.AnuncioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/anuncios")
@CrossOrigin(origins = "*")
public class PublicAnuncioController {

    @Autowired
    private AnuncioService service;

    @GetMapping
    public List<Anuncio> listar() {
        List<Anuncio> lista = service.listarTodos();

        lista.forEach(a -> {
            if (a.getFotoList() == null)
                a.setFotoList(new ArrayList<>());
        });

        return lista;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Anuncio a = service.buscarPorId(id);

        if (a == null)
            return ResponseEntity.notFound().build();

        if (a.getFotoList() == null)
            a.setFotoList(new ArrayList<>());

        return ResponseEntity.ok(a);
    }

    @GetMapping("/by-user/{login}")
    public List<Anuncio> listarPorUsuario(@PathVariable String login) {
        List<Anuncio> lista = service.getByUser(login);

        lista.forEach(a -> {
            if (a.getFotoList() == null)
                a.setFotoList(new ArrayList<>());
        });

        return lista;
    }

    @GetMapping("/search")
    public List<Anuncio> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long cat
    ) {
        List<Anuncio> base = service.listarTodos();

        if (q != null && !q.isBlank()) {
            String termo = q.toLowerCase();
            base = base.stream().filter(a ->
                    (a.getTitulo() != null && a.getTitulo().toLowerCase().contains(termo)) ||
                            (a.getDescricao() != null && a.getDescricao().toLowerCase().contains(termo))
            ).toList();
        }

        if (cat != null) {
            base = base.stream().filter(a ->
                    a.getCategorias() != null &&
                            a.getCategorias().stream().anyMatch(c -> Objects.equals(c.getId(), cat))
            ).toList();
        }

        base.forEach(a -> {
            if (a.getFotoList() == null)
                a.setFotoList(new ArrayList<>());
        });

        return base;
    }
}
