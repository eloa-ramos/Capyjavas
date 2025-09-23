package modelo;

import java.math.BigDecimal;

public class Metas {

    private int idMeta;
    private int idPdi;
    private int idSkill;
    private BigDecimal metaPontuacao;
    private BigDecimal pontuacaoObtida;

    // Relacionamentos opcionais
    private PDI pdi;
    private Skills skill;

    // Construtor
    public Metas(int idPdi, int idSkill, BigDecimal metaPontuacao, BigDecimal pontuacaoObtida) {
        this.idPdi = idPdi;
        this.idSkill = idSkill;
        this.metaPontuacao = metaPontuacao;
        this.pontuacaoObtida = pontuacaoObtida;
    }

    // Construtor vazio
    public Metas() {}

    // Getters e Setters
    public int getIdMeta() {
        return idMeta;
    }

    public void setIdMeta(int idMeta) {
        this.idMeta = idMeta;
    }

    public int getIdPdi() {
        return idPdi;
    }

    public void setIdPdi(int idPdi) {
        this.idPdi = idPdi;
    }

    public int getIdSkill() {
        return idSkill;
    }

    public void setIdSkill(int idSkill) {
        this.idSkill = idSkill;
    }

    public BigDecimal getMetaPontuacao() {
        return metaPontuacao;
    }

    public void setMetaPontuacao(BigDecimal metaPontuacao) {
        this.metaPontuacao = metaPontuacao;
    }

    public BigDecimal getPontuacaoObtida() {
        return pontuacaoObtida;
    }

    public void setPontuacaoObtida(BigDecimal pontuacaoObtida) {
        this.pontuacaoObtida = pontuacaoObtida;
    }

    public PDI getPdi() {
        return pdi;
    }

    public void setPdi(PDI pdi) {
        this.pdi = pdi;
    }

    public Skills getSkill() {
        return skill;
    }

    public void setSkill(Skills skill) {
        this.skill = skill;
    }

    // Percentual atingido calculado
    public BigDecimal getPercentualAtingido() {
        if (metaPontuacao != null && metaPontuacao.compareTo(BigDecimal.ZERO) != 0 && pontuacaoObtida != null) {
            return pontuacaoObtida.divide(metaPontuacao, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Metas{" +
                "idMeta=" + idMeta +
                ", idPdi=" + idPdi +
                ", idSkill=" + idSkill +
                ", metaPontuacao=" + metaPontuacao +
                ", pontuacaoObtida=" + pontuacaoObtida +
                ", percentualAtingido=" + getPercentualAtingido() +
                ", pdi=" + (pdi != null ? pdi.getIdPdi() : "Nenhum") +
                ", skill=" + (skill != null ? skill.getNomeSkill() : "Nenhum") +
                '}';
    }
}

