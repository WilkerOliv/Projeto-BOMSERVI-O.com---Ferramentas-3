package unoeste.fipp.bomservico.entities;

import com.fasterxml.jackson.annotation.JsonIgnore; // <— novo
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(name = "usu_login", length = 16)
    private String login;

    @Column(name = "usu_senha", length = 255, nullable = false)
    @JsonIgnore // <— nunca exponha senha no JSON!
    private String senha;

    @Column(name = "usu_nivel")
    private Integer nivel;

    @Column(name = "usu_nome", length = 50)
    private String nome;

    @Column(name = "usu_cpf", length = 20)
    private String cpf;

    @Column(name = "usu_dtnascimento")
    private LocalDate dtNasc;

    @Column(name = "usu_email", length = 50)
    private String email;

    @Column(name = "usu_telefone", length = 20)
    private String telefone;

    @Column(name = "usu_endereco", length = 100)
    private String endereco;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDtNasc() {
        return dtNasc;
    }

    public void setDtNasc(LocalDate dtNasc) {
        this.dtNasc = dtNasc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
