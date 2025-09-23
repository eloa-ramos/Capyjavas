package modelo;

import java.time.LocalDate;

public class PDI {
    private int idPdi;
    private int idColaborador; // chave estrangeira para Usuario
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String observacoes;

    // Construtor vazio
    public PDI() {}

    // Construtor cheio
    public PDI(int idPdi, int idColaborador, LocalDate dataInicio, LocalDate dataFim, String observacoes) {
        this.idPdi = idPdi;
        this.idColaborador = idColaborador;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.observacoes = observacoes;
    }

    // Getters e Setters
    public int getIdPdi() {
        return idPdi;
    }

    public void setIdPdi(int idPdi) {
        this.idPdi = idPdi;
    }

    public int getIdColaborador() {
        return idColaborador;
    }

    public void setIdColaborador(int idColaborador) {
        this.idColaborador = idColaborador;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public String toString() {
        return "PDI{" +
                "idPdi=" + idPdi +
                ", idColaborador=" + idColaborador +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}
