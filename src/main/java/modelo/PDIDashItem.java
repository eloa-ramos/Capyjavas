package modelo;

public class PDIDashItem {

    private final int idUsuario;
    private final String colaborador;
    private final String objetivo;
    private final String prazo;
    private final String status;

    public PDIDashItem(int idUsuario, String colaborador, String objetivo, String prazo, String status) {
        this.idUsuario = idUsuario;
        this.colaborador = colaborador;
        this.objetivo = objetivo;
        this.prazo = prazo;
        this.status = status;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getColaborador() { return colaborador; }
    public String getObjetivo() { return objetivo; }
    public String getPrazo() { return prazo; }
    public String getStatus() { return status; }
}