package unoeste.fipp.bomservico.restcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import unoeste.fipp.bomservico.entities.Anuncio;
import unoeste.fipp.bomservico.entities.Categoria;
import unoeste.fipp.bomservico.entities.Usuario;
import unoeste.fipp.bomservico.repositories.CategoriaRepository;
import unoeste.fipp.bomservico.repositories.InteresseRepository;
import unoeste.fipp.bomservico.repositories.UsuarioRepository;
import unoeste.fipp.bomservico.services.AnuncioService;

import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/anuncios")
@CrossOrigin(origins = "*")
public class AnuncianteRestController {

    @Autowired private AnuncioService service;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private CategoriaRepository categoriaRepo;
    @Autowired private InteresseRepository interesseRepo;

    private String currentLogin() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    private boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (ga.getAuthority().equals(role)) return true;
        }
        return false;
    }

    private boolean isAdmin() { return hasRole("ROLE_ADMIN"); }

    private ResponseEntity<?> bad(String msg){ return ResponseEntity.badRequest().body(Map.of("error", msg)); }
    private ResponseEntity<?> forb(String msg){ return ResponseEntity.status(403).body(Map.of("error", msg)); }

    private void fixFotoList(Anuncio a) {
        if (a.getFotoList() == null)
            a.setFotoList(new ArrayList<>());
    }

    private static final Pattern HORAS_RANGE = Pattern.compile("(\\d{1,2})(?:h|:)?\\s*-\\s*(\\d{1,2})(?:h|:)?");

    private LocalTime parseHoraSimples(String s) {
        if (s == null || s.isBlank()) return null;
        s = s.trim().toLowerCase().replace("h", ":");
        if (s.matches("^\\d{1,2}$")) return LocalTime.of(Integer.parseInt(s), 0);
        if (s.matches("^\\d{1,2}:\\d{1,2}$")) {
            String[] p = s.split(":");
            return LocalTime.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
        }
        return null;
    }

    private void preencherDeAtalhoHorarios(String horarios, Holder h) {
        if (horarios == null || horarios.isBlank()) return;
        String str = horarios.trim();
        Matcher m = HORAS_RANGE.matcher(str.replace(" ", ""));
        if (m.find()) {
            h.horarioInicio = parseHoraSimples(m.group(1));
            h.horarioFim    = parseHoraSimples(m.group(2));
        }

        int idx = -1;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) { idx = i; break; }
        }
        if (idx > 0) {
            h.diasTrabalho = str.substring(0, idx).trim().replaceAll("\\s+", " ");
        }
    }

    private static class Holder {
        String diasTrabalho;
        LocalTime horarioInicio;
        LocalTime horarioFim;
    }

    private String formatHora(LocalTime t) {
        if (t == null) return null;
        return String.format("%02d:%02d", t.getHour(), t.getMinute());
    }

    private String asText(Map<String,Object> body, String... keys) {
        for (String k : keys) {
            Object v = body.get(k);
            if (v instanceof String s && !s.trim().isEmpty()) return s.trim();
        }
        return null;
    }

    private List<Long> asLongList(Map<String,Object> body, String... keys) {
        for (String k : keys) {
            Object v = body.get(k);
            if (v instanceof List<?> lst) {
                List<Long> out = new ArrayList<>();
                lst.forEach(o -> {
                    if (o instanceof Number n) out.add(n.longValue());
                    else if (o instanceof String s)
                        try { out.add(Long.parseLong(s)); } catch (Exception e) {}
                });
                return out;
            }
        }
        return null;
    }

    // CREATE
    @PreAuthorize("hasRole('PRESTADOR') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String,Object> body) {
        String login = currentLogin();
        if (login == null) return forb("Não autenticado.");

        Usuario dono = usuarioRepo.findById(login).orElse(null);
        if (dono == null) return forb("Usuário não encontrado.");

        String titulo = asText(body, "titulo");
        String descricao = asText(body, "descricao");

        if (titulo == null) return bad("Campo obrigatório: titulo.");
        if (descricao == null) return bad("Campo obrigatório: descricao.");

        Holder h = new Holder();

        h.diasTrabalho = asText(body, "diasTrabalho", "dias");
        h.horarioInicio = parseHoraSimples(asText(body, "horarioInicio"));
        h.horarioFim    = parseHoraSimples(asText(body, "horarioFim"));

        preencherDeAtalhoHorarios(asText(body, "horarios"), h);

        if (h.diasTrabalho == null) return bad("Informe diasTrabalho.");
        if (h.horarioInicio == null) return bad("Informe horarioInicio.");
        if (h.horarioFim == null) return bad("Informe horarioFim.");

        Anuncio a = new Anuncio();
        a.setUsuario(dono);
        a.setTitulo(titulo);
        a.setDescricao(descricao);
        a.setDiasTrabalho(h.diasTrabalho);
        a.setHorarioInicio(formatHora(h.horarioInicio));
        a.setHorarioFim(formatHora(h.horarioFim));

        List<Long> idsCat = asLongList(body, "categorias");
        if (idsCat != null) {
            a.setCategorias(categoriaRepo.findAllById(idsCat));
        }

        Anuncio salvo = service.salvar(a);
        fixFotoList(salvo);
        return ResponseEntity.ok(salvo);
    }

    // UPDATE
    @PreAuthorize("hasRole('PRESTADOR') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String,Object> body) {
        Anuncio a = service.buscarPorId(id);
        if (a == null) return bad("Anúncio não encontrado.");

        String login = currentLogin();
        boolean ehDono = a.getUsuario().getLogin().equals(login);
        if (!ehDono && !isAdmin()) return forb("Sem permissão.");

        String titulo = asText(body, "titulo");
        String descricao = asText(body, "descricao");

        if (titulo != null) a.setTitulo(titulo);
        if (descricao != null) a.setDescricao(descricao);

        Holder h = new Holder();
        String dias = asText(body, "diasTrabalho", "dias");
        String hIni = asText(body, "horarioInicio");
        String hFim = asText(body, "horarioFim");
        String atalho = asText(body, "horarios");

        boolean mandouAlgum = dias != null || hIni != null || hFim != null || atalho != null;

        if (mandouAlgum) {
            if (dias != null) h.diasTrabalho = dias;
            if (hIni != null) h.horarioInicio = parseHoraSimples(hIni);
            if (hFim != null) h.horarioFim    = parseHoraSimples(hFim);
            preencherDeAtalhoHorarios(atalho, h);

            if (h.diasTrabalho != null) a.setDiasTrabalho(h.diasTrabalho);
            if (h.horarioInicio != null) a.setHorarioInicio(formatHora(h.horarioInicio));
            if (h.horarioFim != null) a.setHorarioFim(formatHora(h.horarioFim));
        }

        List<Long> idsCat = asLongList(body, "categorias");
        if (idsCat != null) {
            a.setCategorias(categoriaRepo.findAllById(idsCat));
        }

        Anuncio salvo = service.salvar(a);
        fixFotoList(salvo);

        return ResponseEntity.ok(salvo);
    }

    // DELETE
    @PreAuthorize("hasRole('PRESTADOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Anuncio existente = service.buscarPorId(id);
        if (existente == null) return bad("Anúncio não encontrado.");

        String login = currentLogin();
        if (login == null) return forb("Não autenticado.");
        boolean ehDono = existente.getUsuario() != null && login.equals(existente.getUsuario().getLogin());
        if (!ehDono && !isAdmin()) return forb("Sem permissão para excluir este anúncio.");

        // 1 — Apaga os INTERESSES associados ao anúncio
        var interesses = interesseRepo.findByAnuncioId(id);
        interesseRepo.deleteAll(interesses);

        // 2 — Apaga as FOTOS associadas (Foto tem cascade)
        // feito automaticamente

        // 3 — Agora apaga o anúncio
        service.deletar(id);

        return ResponseEntity.ok(Map.of("ok", true));
    }

}
