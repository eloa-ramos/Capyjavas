package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import modelo.PDIDashItem;
import modelo.Usuario;

public class PdiCardController {

    @FXML private Label lblObjetivo;
    @FXML private Label lblStatus;
    @FXML private Label lblColaborador;
    @FXML private Label lblArea;
    @FXML private Label lblPrazo;
    @FXML private Button handleEditAction;

    private PDIDashItem item;
    private DashboardGUIController dashboardController;

    // Método antigo mantido por compatibilidade, mas o novo é preferível
    public void setPdiItem(PDIDashItem item) {
        this.item = item;
        preencherDados();
        handleEditAction.setVisible(false); // Oculta por padrão
    }

    // NOVO MÉTODO: Recebe as referências necessárias
    public void setupCard(PDIDashItem item, Usuario usuarioLogado, DashboardGUIController controller) {
        this.item = item;
        this.dashboardController = controller;
        preencherDados();

        // Lógica de acesso
        String tipoAcesso = usuarioLogado.getTipoAcesso().toUpperCase().replace(" ", "");
        boolean isGestorGeral = "GESTORGERAL".equals(tipoAcesso);
        handleEditAction.setVisible(isGestorGeral);
        handleEditAction.setManaged(isGestorGeral);
    }

    private void preencherDados() {
        lblObjetivo.setText(item.getObjetivo());
        lblColaborador.setText(item.getColaborador());
        lblArea.setText(item.getArea() != null ? item.getArea() : "N/A");
        lblPrazo.setText(item.getPrazo());

        String status = item.getStatus();
        lblStatus.setText(status);
        lblStatus.getStyleClass().clear();
        lblStatus.getStyleClass().add("status-pill");
        lblStatus.getStyleClass().add(getStatusCssClass(status));
    }

    private String getStatusCssClass(String status) {
        if (status == null) return "status-default";
        return switch (status.toLowerCase()) {
            case "concluído" -> "status-concluido";
            case "em andamento" -> "status-andamento";
            case "atrasado" -> "status-atrasado";
            default -> "status-default";
        };
    }

    @FXML
    private void handleEditAction() {
        if (dashboardController != null) {
            dashboardController.abrirJanelaDeEdicao(item);
        }
    }
}