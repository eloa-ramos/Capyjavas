package modelo;

import java.time.LocalDate;

public class Usuario {

    // --- CAMPOS QUE TÊM COLUNA NO SQL ---
    private int id;
    private String email;
    private String senha;

    // --- CHAVE ESTRANGEIRA DA ÁREA (NOVO) ---
    private int idArea;

    // --- CAMPOS EXISTENTES ---
    private String nome;
    private String cpf;
    private String cargo;
    private String experiencia;
    private String observacoes;
    private LocalDate dataNascimento;
    private String tipoAcesso;

    // Nota: id_gestor_de_area e id_gestor_geral omitidos para simplicidade do modelo.


    // --- GETTERS E SETTERS (MÉTODOS DE ACESSO) ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    // --- NOVO GETTER E SETTER PARA idArea ---
    public int getIdArea() { return idArea; }
    public void setIdArea(int idArea) { this.idArea = idArea; }
    // ------------------------------------------

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

    /**
     * NOVO: Método toString() para exibir o nome do usuário no ComboBox.
     */
    @Override
    public String toString() {
        return getNome(); // Retorna o nome do usuário
    }
}