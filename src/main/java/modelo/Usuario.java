package modelo;

import java.time.LocalDate;

public class Usuario {

    // --- CAMPOS ADICIONADOS ---
    private int id;
    private String email;
    private String senha;

    // --- CAMPOS EXISTENTES ---
    private String nome;
    private String cpf;
    private String cargo;
    private String experiencia;
    private String observacoes;
    private LocalDate dataNascimento;
    private String tipoAcesso;


    // --- GETTERS E SETTERS (MÉTODOS DE ACESSO) ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTipoAcesso() { return tipoAcesso; }
    public void setTipoAcesso(String tipoAcesso) { this.tipoAcesso = tipoAcesso; }
}