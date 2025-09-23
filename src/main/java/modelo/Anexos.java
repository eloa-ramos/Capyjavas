package modelo;

public class Anexos {

    private int idAnexo;
    private int idPdi;
    private String nomeArquivo;
    private String caminhoArquivo;
    private String tipoArquivo; // 'pdf', 'png', 'jpeg'
    private String observacoes;

    // Relacionamento opcional
    private PDI pdi;

    // Construtor
    public Anexos(int idPdi, String nomeArquivo, String caminhoArquivo, String tipoArquivo, String observacoes) {
        this.idPdi = idPdi;
        this.nomeArquivo = nomeArquivo;
        this.caminhoArquivo = caminhoArquivo;
        this.tipoArquivo = tipoArquivo;
        this.observacoes = observacoes;
    }

    // Construtor vazio
    public Anexos() {}

    // Getters e Setters
    public int getIdAnexo() {
        return idAnexo;
    }

    public void setIdAnexo(int idAnexo) {
        this.idAnexo = idAnexo;
    }

    public int getIdPdi() {
        return idPdi;
    }

    public void setIdPdi(int idPdi) {
        this.idPdi = idPdi;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public PDI getPdi() {
        return pdi;
    }

    public void setPdi(PDI pdi) {
        this.pdi = pdi;
    }

    @Override
    public String toString() {
        return "Anexos{" +
                "idAnexo=" + idAnexo +
                ", idPdi=" + idPdi +
                ", nomeArquivo='" + nomeArquivo + '\'' +
                ", caminhoArquivo='" + caminhoArquivo + '\'' +
                ", tipoArquivo='" + tipoArquivo + '\'' +
                ", observacoes='" + observacoes + '\'' +
                ", pdi=" + (pdi != null ? pdi.getIdPdi() : "Nenhum") +
                '}';
    }
}
