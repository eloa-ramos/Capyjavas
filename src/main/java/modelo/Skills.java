package modelo;

public class Skills {
    private int idSkill;
    private String nomeSkill;
    private String tipoSkill; // pode ser Hard Skill ou Soft Skill

    // Construtor vazio
    public Skills() {}

    public String getNomeSkill() {
        return nomeSkill;
    }

    public int getIdSkill() {
        return idSkill;
    }

    public void setIdSkill(int idSkill) {
        this.idSkill = idSkill;
    }

    public void setNomeSkill(String nomeSkill) {
        this.nomeSkill = nomeSkill;
    }

    public String getTipoSkill() {
        return tipoSkill;
    }

    public void setTipoSkill(String tipoSkill) {
        this.tipoSkill = tipoSkill;
    }


@Override
public String toString() {
    return "Skills{" +
            "idSkill=" + idSkill +
            ", nomeSkill='" + nomeSkill + '\'' +
            ", tipoSkill='" + tipoSkill + '\'' +
            '}';
}
}