package unoeste.fipp.bomservico.entities;

import com.fasterxml.jackson.annotation.JsonIgnore; // <— novo
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cat_id")
    private Long id;

    @Column(name = "cat_nome", length = 50)
    private String nome;

    @ManyToMany(mappedBy = "categorias", fetch = FetchType.LAZY)
    @JsonIgnore // <— evita loop: ao serializar Categoria, NÃO incluir a lista de Anuncio
    private List<Anuncio> anuncios;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Anuncio> getAnuncios() { return anuncios; }
    public void setAnuncios(List<Anuncio> anuncios) { this.anuncios = anuncios; }
}
