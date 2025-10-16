package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import modelo.PDIDashItem; // Usamos o DTO para carregar os dados

public class PdiCardController {

    @FXML private Label lblObjetivo;
    @FXML private Label lblStatus;
    @FXML private Label lblColaborador;
    @FXML private Label lblArea;
    @FXML private Label lblPrazo; // Mudei para 'lblPrazo' para refletir o PDIDashItem

    private PDIDashItem item; // O objeto DTO associado a este card

    // Método principal para popular o card com dados
    public void setPdiItem(PDIDashItem item) {
        this.item = item;

        // Preenchimento dos Labels
        lblObjetivo.setText(item.getObjetivo());
        lblColaborador.setText(item.getColaborador());
        lblArea.setText(item.getArea() != null ? item.getArea() : "N/A"); // Proteção contra área nula
        lblPrazo.setText(item.getPrazo());

        // Lógica para o Status (e adicionar a classe CSS)
        String status = item.getStatus();
        lblStatus.setText(status);
        lblStatus.getStyleClass().add(getStatusCssClass(status));
    }

    // Mapeia o status para a classe CSS (essencial para o seu novo design!)
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
        return "status-default"; // Classe padrão caso não se encaixe
    }

    @FXML
    private void handleEditAction() {
        // Ação de clique: Você deve usar o 'item.getIdUsuario()' ou o ID do PDI
        // para carregar a tela de edição correta.
        System.out.println("Abrir edição para PDI do Colaborador ID: " + item.getIdUsuario());
        // Aqui você chamaria um método no DashboardGUIController principal
        // para carregar a nova tela (ou usar o Stage/Scene atual).
    }
}