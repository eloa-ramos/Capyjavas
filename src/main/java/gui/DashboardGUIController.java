package gui;

import dao.DashboardDAO;
import modelo.PDIDashItem;
import java.util.List;

public class DashboardGUIController {

    private final DashboardDAO dao;

    public DashboardGUIController() {
        // Inicializa o DAO (a conexão só será feita ao chamar o método de busca)
        this.dao = new DashboardDAO();
    }

    public List<PDIDashItem> carregarDadosPDI() {
        try {
            return dao.buscarPDIs();
        } catch (RuntimeException e) {
            // Em caso de falha de conexão (que será lançada pelo DAO),
            // exibe o erro e retorna uma lista vazia.
            System.err.println("ERRO: Falha ao carregar dados do Dashboard: " + e.getMessage());
            // Você pode adicionar um alerta JavaFX aqui no futuro
            return List.of(
                    new PDIDashItem(0, "ERRO", "Verifique o DB", "00/00/0000", "Atrasado")
            );
        }
    }
}