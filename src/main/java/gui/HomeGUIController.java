package gui;

import javafx.animation.Timeline; // Import para futura animação (opcional)
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox; // Import necessário para manipular o VBox de detalhes
import javafx.stage.Stage;
import modelo.Usuario;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class HomeGUIController implements javafx.fxml.Initializable {

    @FXML private BorderPane rootPane;
    @FXML private Label lblBemVindo;

    // --- Controles de Navegação (Menu Superior) ---
    @FXML private Button btnHome;
    @FXML private Button btnPDIs;

    // --- Dados da Home (KPIs) ---
    @FXML private Label lblTotalPDIs;
    @FXML private Label lblColaboradores;
    @FXML private Label lblPorcentagemConcluidos;

    @FXML private Button btnDetalhesPdi; // Botão de Toggle

    // NOVOS FXMLs para a funcionalidade de detalhes no card
    @FXML private VBox vboxPdiCard;        // O Card pai (opcional, mas bom para referência)
    @FXML private VBox vboxDetalhes;       // Contêiner dos detalhes (para toggle)
    @FXML private Label lblDetConcluidos;
    @FXML private Label lblDetAtrasados;
    @FXML private Label lblDetEmAndamento;
    // Adicione outras categorias de status se necessário aqui

    private Usuario usuarioLogado;
    private final HomeGUIControllerHelper helper = new HomeGUIControllerHelper();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializa o contêiner de detalhes como oculto
        if (vboxDetalhes != null) {
            vboxDetalhes.setVisible(false);
            vboxDetalhes.setManaged(false);
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioLogado = usuario;
        if (usuario != null) {
            // Preenche o nome do usuário no menu
            lblBemVindo.setText("Olá, " + usuario.getNome().split(" ")[0] + "!");
            // Carrega os dados reais e preenche os labels
            carregarKPIs();
        }
    }

    private void carregarKPIs() {
        if (usuarioLogado == null) return;

        // --- BUSCANDO DADOS REAIS VIA HELPER E DAO ---
        int totalPdis = helper.getTotalPDIs(usuarioLogado);
        int colaboradores = helper.getTotalColaboradores(usuarioLogado);
        double concluido = helper.getPorcentagemConcluidos(usuarioLogado);

        // --- ATUALIZAÇÃO DA UI PRINCIPAL ---
        lblTotalPDIs.setText(String.valueOf(totalPdis));

        // KPI de Colaboradores:
        if (colaboradores > 0) {
            lblColaboradores.setText(String.valueOf(colaboradores));
        } else {
            lblColaboradores.setText("--");
        }

        lblPorcentagemConcluidos.setText(String.format("%.1f%%", concluido));

        // NOVO: Preenche os labels da área de detalhes (vboxDetalhes)
        Map<String, Integer> detalhes = helper.getDetalhesStatus(usuarioLogado);
        preencherDetalhes(detalhes);
    }

    /**
     * Preenche os Labels de detalhe com a contagem real dos status.
     */
    private void preencherDetalhes(Map<String, Integer> detalhes) {
        // A chave no Map deve ser exatamente igual ao CASE no SQL do DAO: "Concluído", "Atrasado", "Em Andamento"
        int concluidos = detalhes.getOrDefault("Concluído", 0);
        int atrasados = detalhes.getOrDefault("Atrasado", 0);
        int emAndamento = detalhes.getOrDefault("Em Andamento", 0);

        // Verifica se os labels existem (boas práticas)
        if (lblDetConcluidos != null) {
            lblDetConcluidos.setText("Concluídos: " + concluidos);
        }
        if (lblDetAtrasados != null) {
            lblDetAtrasados.setText("Atrasados: " + atrasados);
        }
        if (lblDetEmAndamento != null) {
            lblDetEmAndamento.setText("Em Andamento: " + emAndamento);
        }
    }

    // --- Lógica de Navegação ---

    /**
     * Navega para a tela de PDIs (o Dashboard).
     */
    @FXML
    private void handleNavigateToPDIs() {
        // Fecha a Stage atual
        Stage currentStage = (Stage) rootPane.getScene().getWindow();
        currentStage.close();

        // Reabre a Stage para o Dashboard
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
     * Permanece ou recarrega a tela Home.
     */
    @FXML
    private void handleNavigateToHome() {
        // Já estamos na Home, apenas recarrega os dados (KPIs)
        carregarKPIs();
    }

    // ---------------------------------------------
    // AÇÃO DE EXPANDIR/OCULTAR DETALHES DO CARD
    // ---------------------------------------------

    /**
     * Alterna a visibilidade da área de detalhes do KPI, simulando uma expansão/contração.
     * Altera o texto do botão para "Ver Detalhes" ou "Ocultar Detalhes".
     */
    @FXML
    private void handleVerDetalhesPdi(ActionEvent event) {
        // Verifica se o VBox de detalhes e o Helper estão disponíveis
        if (vboxDetalhes == null || btnDetalhesPdi == null || helper.getDetalhesStatus(usuarioLogado).isEmpty()) {
            return;
        }

        boolean isVisible = vboxDetalhes.isVisible();

        // NOVO ESTADO: Ocultar
        if (isVisible) {
            // Desativa o gerenciamento de layout e a visibilidade
            vboxDetalhes.setVisible(false);
            vboxDetalhes.setManaged(false);
            btnDetalhesPdi.setText("Ver Detalhes");

            // NOVO ESTADO: Mostrar
        } else {
            // Ativa o gerenciamento de layout e a visibilidade
            vboxDetalhes.setManaged(true);
            vboxDetalhes.setVisible(true);
            btnDetalhesPdi.setText("Ocultar Detalhes");
        }
    }
}