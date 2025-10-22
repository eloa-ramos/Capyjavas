package gui;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import modelo.Usuario;

// IMPORTS PARA GRÁFICOS
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    @FXML private Button btnDetalhesPdi;

    // NOVOS FXMLs para a funcionalidade de detalhes no card
    @FXML private VBox vboxPdiCard;
    @FXML private VBox vboxDetalhes;
    @FXML private Label lblDetConcluidos;
    @FXML private Label lblDetAtrasados;
    @FXML private Label lblDetEmAndamento;

    // --- NOVOS ELEMENTOS PARA GRÁFICOS ---
    @FXML private PieChart chartStatus;
    @FXML private BarChart<String, Number> chartArea;
    @FXML private CategoryAxis xAxisArea;
    @FXML private NumberAxis yAxisArea;

    private Usuario usuarioLogado;
    private final HomeGUIControllerHelper helper = new HomeGUIControllerHelper();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (vboxDetalhes != null) {
            vboxDetalhes.setVisible(false);
            vboxDetalhes.setManaged(false);
        }
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

        int totalPdis = helper.getTotalPDIs(usuarioLogado);
        int colaboradores = helper.getTotalColaboradores(usuarioLogado);
        double concluido = helper.getPorcentagemConcluidos(usuarioLogado);

        lblTotalPDIs.setText(String.valueOf(totalPdis));

        if (colaboradores > 0) {
            lblColaboradores.setText(String.valueOf(colaboradores));
        } else {
            lblColaboradores.setText("--");
        }

        lblPorcentagemConcluidos.setText(String.format("%.1f%%", concluido));

        Map<String, Integer> detalhes = helper.getDetalhesStatus(usuarioLogado);
        preencherDetalhes(detalhes);

        // NOVO: Preenche os gráficos
        popularGraficos(detalhes);
    }

    private void preencherDetalhes(Map<String, Integer> detalhes) {
        int concluidos = detalhes.getOrDefault("Concluído", 0);
        int atrasados = detalhes.getOrDefault("Atrasado", 0);
        int emAndamento = detalhes.getOrDefault("Em Andamento", 0);

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

    // NOVO MÉTODO CENTRAL PARA GRÁFICOS
    private void popularGraficos(Map<String, Integer> detalhesStatus) {
        popularGraficoStatus(detalhesStatus);

        Map<String, Integer> contagemPorArea = helper.getContagemPdiPorArea(usuarioLogado);
        popularGraficoArea(contagemPorArea);
    }

    /**
     * Preenche o PieChart com a contagem de PDIs por Status.
     */
    private void popularGraficoStatus(Map<String, Integer> detalhesStatus) {
        if (chartStatus == null) return;

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        int total = detalhesStatus.values().stream().mapToInt(Integer::intValue).sum();

        if (total > 0) {
            for (Map.Entry<String, Integer> entry : detalhesStatus.entrySet()) {
                String label = String.format("%s (%.1f%%)",
                        entry.getKey(),
                        (double) entry.getValue() * 100 / total);
                pieChartData.add(new PieChart.Data(label, entry.getValue()));
            }
        } else {
            pieChartData.add(new PieChart.Data("Nenhum PDI Encontrado", 1));
        }

        chartStatus.setData(pieChartData);
        chartStatus.setTitle("Status dos PDIs");
        chartStatus.setLabelsVisible(true);
        chartStatus.setLegendVisible(true);
    }

    /**
     * Preenche o BarChart com a contagem de PDIs por Área.
     */
    private void popularGraficoArea(Map<String, Integer> contagemPorArea) {
        if (chartArea == null) return;

        chartArea.getData().clear();

        if (contagemPorArea.isEmpty()) {
            // Se não houver dados, o gráfico fica vazio ou exibe uma mensagem.
            // Aqui vamos apenas limpá-lo.
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total de PDIs");

        for (Map.Entry<String, Integer> entry : contagemPorArea.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chartArea.getData().add(series);
        chartArea.setTitle("Carga de Trabalho por Área");

        // Configurações visuais
        if (yAxisArea != null) {
            yAxisArea.setLowerBound(0);
        }
    }


    @FXML
    private void handleNavigateToPDIs() {
        Stage currentStage = (Stage) rootPane.getScene().getWindow();
        currentStage.close();

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

    @FXML
    private void handleNavigateToHome() {
        carregarKPIs();
    }

    @FXML
    private void handleVerDetalhesPdi(ActionEvent event) {
        if (vboxDetalhes == null || btnDetalhesPdi == null) {
            return;
        }

        boolean isVisible = vboxDetalhes.isVisible();

        if (isVisible) {
            vboxDetalhes.setVisible(false);
            vboxDetalhes.setManaged(false);
            btnDetalhesPdi.setText("Ver Detalhes");
        } else {
            vboxDetalhes.setManaged(true);
            vboxDetalhes.setVisible(true);
            btnDetalhesPdi.setText("Ocultar Detalhes");
        }
    }
}