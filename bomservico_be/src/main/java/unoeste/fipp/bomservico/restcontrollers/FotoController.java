package unoeste.fipp.bomservico.restcontrollers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import unoeste.fipp.bomservico.repositories.AnuncioRepository;
import unoeste.fipp.bomservico.repositories.FotoRepository;
import unoeste.fipp.bomservico.entities.Foto;
import unoeste.fipp.bomservico.entities.Anuncio;
import org.springframework.security.access.prepost.PreAuthorize;

import java.nio.file.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    @Value("${app.uploadDir:uploads}")
    private String uploadDir;

    @Autowired private FotoRepository fotoRepo;
    @Autowired private AnuncioRepository anuncioRepo;

    @GetMapping("/by-anuncio/{anuncioId}")
    public List<Foto> byAnuncio(@PathVariable Long anuncioId){ return fotoRepo.findByAnuncio_Id(anuncioId); }

    @GetMapping("/raw/{filename}")
    public ResponseEntity<byte[]> raw(@PathVariable String filename) throws IOException {
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path resolved = base.resolve(filename).normalize();
        if (!resolved.startsWith(base) || !Files.exists(resolved) || Files.isDirectory(resolved)) {
            return ResponseEntity.badRequest().build();
        }
        byte[] data = Files.readAllBytes(resolved);
        String contentType = Files.probeContentType(resolved);
        if(contentType == null) contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }

    @PreAuthorize("hasRole('PRESTADOR') or hasRole('ADMIN')")
    @PostMapping("/upload/{anuncioId}")
    public ResponseEntity<?> upload(@PathVariable Long anuncioId, @RequestParam("file") MultipartFile file) throws IOException {
        Anuncio a = anuncioRepo.findById(anuncioId).orElse(null);
        if(a == null) return ResponseEntity.badRequest().body("Anúncio não encontrado");
        Path dir = Paths.get(uploadDir);
        if(!Files.exists(dir)) Files.createDirectories(dir);
        String filename = System.currentTimeMillis() + "_" + Paths.get(file.getOriginalFilename()).getFileName().toString();
        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        Foto f = new Foto(); f.setNomeArq(filename); f.setAnuncio(a);
        fotoRepo.save(f);
        return ResponseEntity.ok(f);
    }

    @PreAuthorize("hasRole('PRESTADOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteFoto(@PathVariable Long id) {
        fotoRepo.deleteById(id);
    }


}
