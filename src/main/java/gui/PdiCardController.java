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

    public void setPdiItem(PDIDashItem item) {
        this.item = item;
        preencherDados();
        handleEditAction.setVisible(false);
        handleEditAction.setManaged(false);
    }

    public void setupCard(PDIDashItem item, Usuario usuarioLogado, DashboardGUIController controller) {
        this.item = item;
        this.dashboardController = controller;
        preencherDados();

        // Lógica de visibilidade do botão de edição (Gestor Geral ou RH podem editar)
        String tipoAcesso = usuarioLogado.getTipoAcesso().toUpperCase().replace(" ", "");
        boolean podeEditar = "GESTORGERAL".equals(tipoAcesso) || "RH".equals(tipoAcesso);

        if (handleEditAction != null) {
            handleEditAction.setVisible(podeEditar);
            handleEditAction.setManaged(podeEditar);
        }
    }

    private void preencherDados() {
        lblObjetivo.setText(item.getObjetivo());
        lblColaborador.setText(item.getColaborador());
        lblArea.setText(item.getArea() != null ? item.getArea() : "N/A");
        lblPrazo.setText(item.getPrazo());

        String status = item.getStatus();
        lblStatus.setText(status);

        // Garante que apenas a classe 'status-pill' e a classe específica de status permaneçam
        lblStatus.getStyleClass().removeIf(s -> s.startsWith("status-"));
        lblStatus.getStyleClass().add(getStatusCssClass(status));
    }

    private String getStatusCssClass(String status) {
        if (status == null) return "status-default";

        String lowerStatus = status.toLowerCase();
        if (lowerStatus.contains("concluído")) {
            return "status-concluido";
        } else if (lowerStatus.contains("andamento")) {
            return "status-andamento";
        } else if (lowerStatus.contains("atrasado") || lowerStatus.contains("bloqueado")) {
            return "status-atrasado";
        }
        return "status-default";
    }

    @FXML
    private void handleEditAction() {
        if (dashboardController != null && item != null) {
            dashboardController.abrirJanelaDeEdicao(item);
        }
    }
}