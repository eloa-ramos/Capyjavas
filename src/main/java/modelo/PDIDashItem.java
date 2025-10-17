package modelo;

public class PDIDashItem {

    private final int idPdi; // <-- ADICIONADO
    private final int idUsuario;
    private final String colaborador;
    private final String objetivo;
    private final String prazo;
    private final String status;
    private final String area;

    /**
     * Construtor principal com todas as propriedades.
     */
    public PDIDashItem(int idPdi, int idUsuario, String colaborador, String objetivo, String prazo, String status, String area) {
        this.idPdi = idPdi; // <-- ADICIONADO
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
        this(0, idUsuario, colaborador, objetivo, prazo, status, null); // Adicionado 0 para idPdi
    }

    // Getters
    public int getIdPdi() { return idPdi; } // <-- ADICIONADO
    public int getIdUsuario() { return idUsuario; }
    public String getColaborador() { return colaborador; }
    public String getObjetivo() { return objetivo; }
    public String getPrazo() { return prazo; }
    public String getStatus() { return status; }
    public String getArea() { return area; }
}