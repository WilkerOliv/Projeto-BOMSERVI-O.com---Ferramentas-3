package unoeste.fipp.bomservico.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unoeste.fipp.bomservico.config.JwtUtils;
import unoeste.fipp.bomservico.entities.Usuario;
import unoeste.fipp.bomservico.repositories.UsuarioRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5000", "http://127.0.0.1:5500", "http://localhost:5501"}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private JwtUtils jwtUtils;

    // --------- util para pegar o primeiro valor não-nulo/blank entre várias chaves possíveis
    private String pick(Map<String, ?> body, String... keys) {
        for (String k : keys) {
            Object v = body.get(k);
            if (v instanceof String s && !s.trim().isEmpty()) return s.trim();
        }
        return null;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Map<String, Object> body) {
        // aceita varios aliases de campos
        String login = pick(body, "login", "usu_login", "username", "user");
        String senha = pick(body, "senha", "usu_senha", "password", "pass");

        if (login == null || senha == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Login e senha são obrigatórios."));
        }

        if (usuarioRepo.existsById(login)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Já existe um usuário com este login."));
        }

        // monta o Usuario manualmente para não depender de nomes de propriedades no JSON
        Usuario novo = new Usuario();
        novo.setLogin(login);
        novo.setSenha(senha);

        // nivel padrão = 0 (prestador). Aceita override se vier nivel válido.
        Integer nivel = null;
        Object rawNivel = body.get("nivel");
        if (rawNivel instanceof Number n) nivel = n.intValue();
        else if (rawNivel instanceof String s) {
            try { nivel = Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) {}
        }
        novo.setNivel(nivel != null ? nivel : 0);

        // campos opcionais
        String nome = pick(body, "nome", "usu_nome", "name", "fullName");
        String email = pick(body, "email", "usu_email");
        String telefone = pick(body, "telefone", "usu_telefone", "phone");
        String cpf = pick(body, "cpf", "usu_cpf");
        String endereco = pick(body, "endereco", "usu_endereco", "address");
        String dtNascStr = pick(body, "dtNasc", "usu_dtnascimento", "dataNascimento");

        novo.setNome(nome);
        novo.setEmail(email);
        novo.setTelefone(telefone);
        novo.setCpf(cpf);
        novo.setEndereco(endereco);

        if (dtNascStr != null) {
            try { novo.setDtNasc(LocalDate.parse(dtNascStr)); } catch (DateTimeParseException ignored) {}
        }

        // salva em texto puro (compatível com a tabela atual)
        usuarioRepo.save(novo);

        // nunca retornar a senha
        novo.setSenha(null);

        return ResponseEntity.ok(Map.of("ok", true, "user", novo));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String senha = body.get("senha");

        if (login == null || senha == null || login.isBlank() || senha.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Informe login e senha."));
        }

        var opt = usuarioRepo.findById(login);
        if (opt.isEmpty() || !senha.equals(opt.get().getSenha())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciais inválidas."));
        }

        var u = opt.get();
        String token = jwtUtils.generateToken(u.getLogin(), u.getNivel());
        u.setSenha(null);

        return ResponseEntity.ok(Map.of("token", token, "user", u));
    }
}
