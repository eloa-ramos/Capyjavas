package gui;

import dao.PDIDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.PDI;
import modelo.Usuario;

import java.time.LocalDate;

public class CadastroPdiController {

    @FXML
    private TextField txtColaborador;

    @FXML
    private DatePicker dateInicio;

    @FXML
    private DatePicker dateFim;

    @FXML
    private TextArea txtObservacoes;

    @FXML
    private Button btnCadastrar;

    private Usuario usuarioLogado;

    public void setUsuario(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    @FXML
    private void cadastrarPdi() {
        try {
            String colaborador = txtColaborador.getText().trim();
            LocalDate inicio = dateInicio.getValue();
            LocalDate fim = dateFim.getValue();
            String observacoes = txtObservacoes.getText().trim();

            if (colaborador.isEmpty() || inicio == null || fim == null) {
                showAlert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios.");
                return;
            }

            PDI pdi = new PDI();
            pdi.setIdColaborador(Integer.parseInt(colaborador)); // ideal: buscar ID pelo nome
            pdi.setDataInicio(inicio);
            pdi.setDataFim(fim);
            pdi.setObservacoes(observacoes);

            PDIDAO dao = new PDIDAO();
            dao.cadastrarPDI(pdi);

            showAlert(Alert.AlertType.INFORMATION, "PDI cadastrado com sucesso!");
            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro ao cadastrar PDI: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCadastrar.getScene().getWindow();
        stage.close();
    }
}
