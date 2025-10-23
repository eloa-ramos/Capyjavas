package modelo;

public class Area {
    private int idArea;
    private String nomeArea;

    public Area(int idArea, String nomeArea) {
        this.idArea = idArea;
        this.nomeArea = nomeArea;
    }

    // Usado pelo ComboBox para exibir o nome
    @Override
    public String toString() {
        return nomeArea;
    }

    // Getters e Setters
    public int getIdArea() {
        return idArea;
    }

    public void setIdArea(int idArea) {
        this.idArea = idArea;
    }

    public String getNomeArea() {
        return nomeArea;
    }

    public void setNomeArea(String nomeArea) {
        this.nomeArea = nomeArea;
    }
}