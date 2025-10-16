package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import modelo.Usuario;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeGUIController implements javafx.fxml.Initializable {

    @FXML private BorderPane rootPane; // Contêiner principal para trocar de tela
    @FXML private Label lblBemVindo;   // Exibir boas-vindas ao usuário

    // --- Controles de Navegação (Menu Superior) ---
    @FXML private Button btnHome;
    @FXML private Button btnPDIs;

    // --- Dados da Home ---
    @FXML private Label lblPdiAtivos;
    @FXML private Label lblColaboradores;
    @FXML private Label lblPorcentagemConcluidos;

    private Usuario usuarioLogado;
    private final HomeGUIControllerHelper helper = new HomeGUIControllerHelper(); // Para buscar os KPIs

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializações visuais, se houver.
        // O foco na navegação será feito via handleNavigation().
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        if (usuario != null) {
            lblBemVindo.setText("Olá, " + usuario.getNome().split(" ")[0] + "!");
            carregarKPIs();
        }
    }

    private void carregarKPIs() {
        if (usuarioLogado == null) return;

        // Simulação de busca (Substituir pela chamada real ao Helper)
        int ativos = 15; // helper.getPDIAtivos(usuarioLogado);
        int colaboradores = 30; // helper.getTotalColaboradores();
        double concluido = 45.5; // helper.getPorcentagemConcluidos();

        lblPdiAtivos.setText(String.valueOf(ativos));
        lblColaboradores.setText(String.valueOf(colaboradores));
        lblPorcentagemConcluidos.setText(String.format("%.1f%%", concluido));
    }

    // --- Lógica de Navegação ---

    /**
     * Navega para a tela de PDIs (o Dashboard atual).
     */
    @FXML
    private void handleNavigateToPDIs() {
        // Fecha a Stage atual
        Stage currentStage = (Stage) rootPane.getScene().getWindow();
        currentStage.close();

        // Reabre a Stage para o Dashboard (usando o fluxo do Controller)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/DashboardGUI.fxml"));
            Parent root = loader.load();

            DashboardGUIController controller = loader.getController();
            controller.setUsuario(usuarioLogado);

            Stage dashStage = new Stage();
            dashStage.setTitle("YouTan - PDIs");
            dashStage.setScene(new javafx.scene.Scene(root));
            dashStage.setMaximized(true);
            dashStage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar DashboardGUI.fxml.");
            e.printStackTrace();
        }
    }

    /**
     * Permanece ou recarrega a tela Home (Não faz nada, pois já estamos nela).
     */
    @FXML
    private void handleNavigateToHome() {
        // Já estamos na Home, nada a fazer além de garantir que os dados estejam atualizados.
        carregarKPIs();
    }
}