package gui;

import dao.PDIDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.PDI;
import modelo.PDIDashItem;

public class EdicaoPdiController {

    @FXML private TextField txtColaborador;
    @FXML private TextField txtArea;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpPrazo;
    @FXML private TextField txtObjetivo;
    @FXML private TextArea txtObservacoes;
    @FXML private Button btnSalvar;

    private PDI pdiParaEditar;
    private DashboardGUIController dashboardController;
    private PDIDashItem dashItem;

    public void carregarPdi(PDI pdi, PDIDashItem item, DashboardGUIController controller) {
        this.pdiParaEditar = pdi;
        this.dashItem = item;
        this.dashboardController = controller;

        if (pdiParaEditar == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar os dados do PDI.");
            return;
        }

        txtColaborador.setText(item.getColaborador());
        txtArea.setText(item.getArea());
        txtObjetivo.setText(item.getObjetivo()); // Assumindo que objetivo não muda
        dpDataInicio.setValue(pdi.getDataInicio());
        dpPrazo.setValue(pdi.getDataFim());
        txtObservacoes.setText(pdi.getObservacoes());
    }

    @FXML
    private void handleSalvar() {
        if (dpDataInicio.getValue() == null || dpPrazo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "As datas de início e fim são obrigatórias.");
            return;
        }

        pdiParaEditar.setDataInicio(dpDataInicio.getValue());
        pdiParaEditar.setDataFim(dpPrazo.getValue());
        pdiParaEditar.setObservacoes(txtObservacoes.getText().trim());

        try {
            new PDIDAO().atualizarPDI(pdiParaEditar);
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI atualizado com sucesso!");

            // Recarrega os dados no dashboard
            dashboardController.recarregarPdis(null);

            fecharJanela();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao salvar as alterações: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnSalvar.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}