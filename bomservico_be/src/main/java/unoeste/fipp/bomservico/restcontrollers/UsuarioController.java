package unoeste.fipp.bomservico.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import unoeste.fipp.bomservico.services.UsuarioService;
import unoeste.fipp.bomservico.entities.Usuario;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // ============================================================
    // LISTAR TODOS (somente ADMIN)
    // ============================================================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Usuario> all() {
        return service.getAll();
    }

    // ============================================================
    // BUSCAR UM USUÁRIO (admin ou prestador)
    // ============================================================
    @PreAuthorize("hasRole('PRESTADOR') or hasRole('ADMIN')")
    @GetMapping("/{login}")
    public Usuario get(@PathVariable String login) {
        Usuario u = service.getByLogin(login);
        if (u != null) u.setSenha(null); // nunca retornar senha
        return u;
    }

    // ============================================================
    // ATUALIZAR USUÁRIO (perfil) — atualização PARCIAL
    // ============================================================
    @PreAuthorize("hasRole('PRESTADOR') or hasRole('ADMIN')")
    @PutMapping("/{login}")
    public Usuario update(@PathVariable String login, @RequestBody Map<String,Object> body) {

        Usuario u = service.getByLogin(login);
        if (u == null) return null;

        // Atualização parcial — só altera o campo se existir no JSON
        if (body.containsKey("nome"))      u.setNome( (String) body.get("nome") );
        if (body.containsKey("email"))     u.setEmail( (String) body.get("email") );
        if (body.containsKey("telefone"))  u.setTelefone( (String) body.get("telefone") );
        if (body.containsKey("cpf"))       u.setCpf( (String) body.get("cpf") );
        if (body.containsKey("endereco"))  u.setEndereco( (String) body.get("endereco") );


        // SENHA (opcional)
        if (body.containsKey("senha")) {
            String s = (String) body.get("senha");
            if (s != null && !s.trim().isEmpty()) {
                u.setSenha(s.trim());
            }
        }

        // Nunca permitir mudar nível ou login
        u.setLogin(login);

        Usuario saved = service.save(u);
        saved.setSenha(null);
        return saved;
    }

    // ============================================================
    // DELETAR (somente ADMIN)
    // ============================================================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{login}")
    public void delete(@PathVariable String login) {
        service.delete(login);
    }
}
