package modelo;

public class PDIDashItem {

    private final int idUsuario;
    private final String colaborador;
    private final String objetivo; // Usado na busca de texto livre
    private final String prazo;
    private final String status; // Usado no filtro de status
    private final String area; // Usado no filtro de área e na busca

    /**
     * Construtor principal com todas as propriedades.
     */
    public PDIDashItem(int idUsuario, String colaborador, String objetivo, String prazo, String status, String area) {
        this.idUsuario = idUsuario;
        this.colaborador = colaborador;
        this.objetivo = objetivo;
        this.prazo = prazo;
        this.status = status;
        this.area = area;
    }

    /**
     * Construtor de compatibilidade, assumindo área nula.
     */
    public PDIDashItem(int idUsuario, String colaborador, String objetivo, String prazo, String status) {
        this(idUsuario, colaborador, objetivo, prazo, status, null);
    }

    // Getters
    public int getIdUsuario() { return idUsuario; }
    public String getColaborador() { return colaborador; }
    public String getObjetivo() { return objetivo; }
    public String getPrazo() { return prazo; }
    public String getStatus() { return status; }
    public String getArea() { return area; }
}