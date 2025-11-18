package unoeste.fipp.bomservico.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unoeste.fipp.bomservico.entities.Usuario;
import unoeste.fipp.bomservico.repositories.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    public List<Usuario> getAll() {
        return repo.findAll();
    }

    public Usuario getByLogin(String login) {
        return repo.findById(login).orElse(null);
    }

    public Usuario save(Usuario u) {
        if (u == null || u.getLogin() == null) {
            throw new IllegalArgumentException("Usuário ou login inválido");
        }
        return repo.save(u);
    }

    public void delete(String login) {
        repo.deleteById(login);
    }
}
