package unoeste.fipp.bomservico.entities;

import com.fasterxml.jackson.annotation.JsonIgnore; // <— novo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "interesse")
public class Interesse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "int_id")
    private Long id;

    @Column(name = "int_nome", length = 100)
    private String nome;

    @Column(name = "int_fone", length = 20)
    private String fone;

    @Column(name = "int_email", length = 100)
    private String email;

    @Column(name = "int_mensagem", columnDefinition = "text")
    private String mensagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anu_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Anuncio anuncio;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Anuncio getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(Anuncio anuncio) {
        this.anuncio = anuncio;
    }
}
