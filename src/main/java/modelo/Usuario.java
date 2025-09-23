package modelo;

import java.time.LocalDate;
import java.util.List;

public class Usuario {
    private String nome;
    private String cpf;
    private String cargo;
    private String experiencia;
    private String observacoes;
    private String tipoAcesso;
    private LocalDate dataNascimento;
    private int idUsuario;

    private Usuario gestorDeArea;
    private Usuario gestorGeral;

    private List<Usuario> subordinadosDeArea;
    private List<Usuario> subordinadosGeral;


    // Construtor
    public Usuario(){}
    public Usuario(String nome, String cpf, String cargo) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.dataNascimento = dataNascimento;
    }

    // Getters e Setters
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getTipoAcesso() {
        return tipoAcesso;
    }

    public void setTipoAcesso(String tipoAcesso) {
        this.tipoAcesso = tipoAcesso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario getGestorDeArea() {
        return gestorDeArea;
    }

    public void setGestorDeArea(Usuario gestorDeArea) {
        this.gestorDeArea = gestorDeArea;
    }

    public Usuario getGestorGeral() {
        return gestorGeral;
    }

    public void setGestorGeral(Usuario gestorGeral) {
        this.gestorGeral = gestorGeral;
    }

    public List<Usuario> getSubordinadosDeArea() {
        return subordinadosDeArea;
    }

    public void setSubordinadosDeArea(List<Usuario> subordinadosDeArea) {
        this.subordinadosDeArea = subordinadosDeArea;
    }

    public List<Usuario> getSubordinadosGeral() {
        return subordinadosGeral;
    }

    public void setSubordinadosGeral(List<Usuario> subordinadosGeral) {
        this.subordinadosGeral = subordinadosGeral;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", cargo='" + cargo + '\'' +
                ", experiencia='" + experiencia + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", tipoAcesso='" + tipoAcesso + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", gestorDeArea=" + (gestorDeArea != null ? gestorDeArea.getNome() : "Nenhum") +
                ", gestorGeral=" + (gestorGeral != null ? gestorGeral.getNome() : "Nenhum") +
                ", subordinadosDeArea=" + (subordinadosDeArea != null ? subordinadosDeArea.size() : 0) +
                ", subordinadosGeral=" + (subordinadosGeral != null ? subordinadosGeral.size() : 0) +
                '}';
    }
}