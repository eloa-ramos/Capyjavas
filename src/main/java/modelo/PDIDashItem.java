package modelo;

public class PDIDashItem {

    private final int idUsuario;
    private final String colaborador;
    private final String objetivo;
    private final String prazo;
    private final String status;
    private final String area;

    // Novo construtor com o campo área
    public PDIDashItem(int idUsuario, String colaborador, String objetivo, String prazo, String status, String area) {
        this.idUsuario = idUsuario;
        this.colaborador = colaborador;
        this.objetivo = objetivo;
        this.prazo = prazo;
        this.status = status;
        this.area = area;
    }

    // Construtor antigo (mantém compatibilidade)
    public PDIDashItem(int idUsuario, String colaborador, String objetivo, String prazo, String status) {
        this(idUsuario, colaborador, objetivo, prazo, status, null);
    }

    // Getters
    public String getArea() { return area; }
    public int getIdUsuario() { return idUsuario; }
    public String getColaborador() { return colaborador; }
    public String getObjetivo() { return objetivo; }
    public String getPrazo() { return prazo; }
    public String getStatus() { return status; }
}
