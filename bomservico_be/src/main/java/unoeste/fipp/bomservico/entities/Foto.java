package unoeste.fipp.bomservico.entities;

import com.fasterxml.jackson.annotation.JsonIgnore; // <— novo
import jakarta.persistence.*;

@Entity
@Table(name = "foto")
public class Foto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fot_id")
    private Long id;

    @Column(name = "fot_file", length = 200)
    private String nomeArq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anu_id")
    @JsonIgnore // <— evita loop: Foto -> Anuncio -> fotoList -> Foto...
    private Anuncio anuncio;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeArq() { return nomeArq; }
    public void setNomeArq(String nomeArq) { this.nomeArq = nomeArq; }
    public Anuncio getAnuncio() { return anuncio; }
    public void setAnuncio(Anuncio anuncio) { this.anuncio = anuncio; }
}
