package unoeste.fipp.bomservico.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "anuncio")
public class Anuncio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usu_login")
    private Usuario usuario;

    @Column(name = "anu_titulo", length = 60)
    private String titulo;

    @Column(name = "anu_descr", columnDefinition = "text")
    private String descricao;

    // dias de trabalho -> usa a coluna antiga obrigatória
    @Column(name = "anu_diastrabalho", length = 30, nullable = false)
    private String diasTrabalho;

    // horário início -> usa a coluna antiga obrigatória, guardando como texto "HH:mm"
    @Column(name = "anu_horarioinicio", length = 5, nullable = false)
    private String horarioInicio;

    // horário fim -> usa a coluna antiga obrigatória, guardando como texto "HH:mm"
    @Column(name = "anu_horariofim", length = 5, nullable = false)
    private String horarioFim;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "anu_cat",
            joinColumns = @JoinColumn(name = "anu_id"),
            inverseJoinColumns = @JoinColumn(name = "cat_id"))
    private List<Categoria> categorias;

    @OneToMany(mappedBy = "anuncio", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Foto> fotoList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDiasTrabalho() {
        return diasTrabalho;
    }

    public void setDiasTrabalho(String diasTrabalho) {
        this.diasTrabalho = diasTrabalho;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public String getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(String horarioFim) {
        this.horarioFim = horarioFim;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public List<Foto> getFotoList() {
        return fotoList;
    }

    public void setFotoList(List<Foto> fotoList) {
        this.fotoList = fotoList;
    }
}
