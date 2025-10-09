package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelo.Usuario;
import dao.PDIDAO;
import modelo.PDI;

import java.time.LocalDate;

public class CadastroPdiGUIController {

    private Usuario usuarioLogado;

    // --- Campos do FXML ---
    @FXML
    private TextField txtColaboradorId;

    @FXML
    private DatePicker dpDataInicio;

    @FXML
    private DatePicker dpDataFim;

    @FXML
    private TextArea taObservacoes;

    @FXML
    private Button btnSalvar;

    @FXML
    private Button btnCancelar;

    // --- Método para receber usuário logado ---
    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    @FXML
    private void initialize() {
        // Configurações iniciais se necessário
    }

    @FXML
    private void handleSalvar() {
        try {
            int idColaborador = Integer.parseInt(txtColaboradorId.getText().trim());
            LocalDate dataInicio = dpDataInicio.getValue();
            LocalDate dataFim = dpDataFim.getValue();
            String observacoes = taObservacoes.getText().trim();

            if (dataInicio == null || dataFim == null) {
                showAlert(Alert.AlertType.WARNING, "Preencha as datas de início e fim do PDI.");
                return;
            }

            PDI pdi = new PDI();
            pdi.setIdColaborador(idColaborador);
            pdi.setDataInicio(dataInicio);
            pdi.setDataFim(dataFim);
            pdi.setObservacoes(observacoes);

            PDIDAO dao = new PDIDAO();
            dao.cadastrarPDI(pdi);

            showAlert(Alert.AlertType.INFORMATION, "PDI cadastrado com sucesso!");
            fecharJanela();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "ID do colaborador inválido.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao cadastrar PDI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo, mensagem);
        alert.showAndWait();
    }
}