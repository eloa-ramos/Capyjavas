package gui;

import dao.PDIDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.PDI;
import modelo.PDIDashItem;

// --- NOVOS IMPORTS
import dao.AnexosDAO;
import modelo.Anexos;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; // NECESSÁRIO
// --- FIM NOVOS IMPORTS

public class EdicaoPdiController {

    @FXML private TextField txtColaborador;
    @FXML private TextField txtArea;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpPrazo;
    @FXML private TextField txtObjetivo;
    @FXML private TextArea txtObservacoes;
    @FXML private Button btnSalvar;

    // --- NOVOS CAMPOS FXML PARA ANEXO ---
    @FXML private Button btnAnexar;
    @FXML private Label lblNomeArquivo;
    @FXML private VBox vboxAnexosExistentes; // NOVO CAMPO PARA EXIBIR A LISTA
    // --- FIM NOVOS CAMPOS FXML PARA ANEXO ---

    private PDI pdiParaEditar;
    private DashboardGUIController dashboardController;
    private PDIDashItem dashItem;
    private File arquivoSelecionado;

    // DAO
    private final AnexosDAO anexosDAO = new AnexosDAO(); // Inicializa o DAO

    // --- Lidar com a seleção de arquivo ---
    @FXML
    private void handleAnexarDocumento() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Anexar Documento ao PDI");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos e Imagens", "*.pdf", "*.docx", "*.doc", "*.png", "*.jpeg", "*.jpg"),
                new FileChooser.ExtensionFilter("Todos os Arquivos", "*.*")
        );

        Stage stage = (Stage) btnAnexar.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.arquivoSelecionado = selectedFile;
            lblNomeArquivo.setText(selectedFile.getName());
            showAlert(Alert.AlertType.INFORMATION, "Atenção", "Arquivo selecionado: " + selectedFile.getName() + ".\nClique em SALVAR para anexá-lo.");
        } else {
            this.arquivoSelecionado = null;
            lblNomeArquivo.setText("Nenhum arquivo selecionado.");
        }
    }

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
        txtObjetivo.setText(item.getObjetivo());
        dpDataInicio.setValue(pdi.getDataInicio());
        dpPrazo.setValue(pdi.getDataFim());
        txtObservacoes.setText(pdi.getObservacoes());

        // Limpa o estado do anexo ao carregar e reseta o rótulo
        this.arquivoSelecionado = null;
        if (lblNomeArquivo != null) {
            lblNomeArquivo.setText("Nenhum arquivo selecionado.");
        }

        // --- NOVO: CARREGAR E EXIBIR ANEXOS EXISTENTES ---
        if (vboxAnexosExistentes != null && pdi != null) {
            List<Anexos> anexos = anexosDAO.listarPorPdi(pdi.getIdPdi());
            exibirAnexos(anexos);
        }
        // --- FIM NOVO: CARREGAR E EXIBIR ANEXOS EXISTENTES ---
    }

    // --- NOVO MÉTODO: Exibir a lista de anexos ---
    private void exibirAnexos(List<Anexos> anexos) {
        vboxAnexosExistentes.getChildren().clear();

        if (anexos.isEmpty()) {
            Label lblNenhum = new Label("Nenhum anexo encontrado para este PDI.");
            lblNenhum.setStyle("-fx-font-style: italic; -fx-text-fill: #999;");
            vboxAnexosExistentes.getChildren().add(lblNenhum);
            return;
        }

        for (Anexos anexo : anexos) {
            HBox anexoBox = new HBox(10);
            anexoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            anexoBox.setStyle("-fx-padding: 5 10; -fx-background-color: #f0f0f0; -fx-background-radius: 4;");

            Label lblTipo = new Label(getTipoIcone(anexo.getTipoArquivo()));
            lblTipo.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

            Hyperlink linkNome = new Hyperlink(anexo.getNomeArquivo());
            linkNome.setTooltip(new Tooltip(anexo.getCaminhoArquivo())); // Mostra o caminho no hover
            linkNome.setOnAction(e -> {
                // Simulação de abertura de arquivo:
                showAlert(Alert.AlertType.INFORMATION, "Abrir Arquivo",
                        "Tentando abrir arquivo em: " + anexo.getCaminhoArquivo() + "\n(A abertura real depende do JavaFX Desktop API)");
                // Para implementar a abertura real, descomente as linhas abaixo
                // try {
                //    java.awt.Desktop.getDesktop().open(new File(anexo.getCaminhoArquivo()));
                // } catch (java.io.IOException | UnsupportedOperationException ex) {
                //    System.err.println("Erro ao abrir arquivo: " + ex.getMessage());
                // }
            });

            anexoBox.getChildren().addAll(lblTipo, linkNome);
            vboxAnexosExistentes.getChildren().add(anexoBox);
        }
    }

    // Método auxiliar para ícones
    private String getTipoIcone(String tipo) {
        if (tipo.contains("pdf")) return "📄 PDF:";
        if (tipo.contains("png") || tipo.contains("jpeg")) return "🖼️ Imagem:";
        return "📁 Arquivo:";
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
            // 1. Salva as alterações do PDI
            new PDIDAO().atualizarPDI(pdiParaEditar);

            // --- SALVAR NOVO ANEXO (SE HOUVER) ---
            if (arquivoSelecionado != null) {
                String nomeArquivo = arquivoSelecionado.getName();
                String caminho = arquivoSelecionado.getAbsolutePath();

                int lastDot = nomeArquivo.lastIndexOf('.');
                String extensao = (lastDot > 0) ? nomeArquivo.substring(lastDot + 1).toLowerCase() : "";

                String tipoArquivo;
                if (extensao.contains("pdf") || extensao.contains("doc") || extensao.contains("xls") || extensao.contains("txt")) {
                    tipoArquivo = "pdf";
                } else if (extensao.contains("png")) {
                    tipoArquivo = "png";
                } else if (extensao.contains("jpe") || extensao.contains("jpg")) {
                    tipoArquivo = "jpeg";
                } else {
                    tipoArquivo = "pdf";
                }

                Anexos anexo = new Anexos();
                anexo.setIdPdi(pdiParaEditar.getIdPdi());
                anexo.setNomeArquivo(nomeArquivo);
                anexo.setCaminhoArquivo(caminho);
                anexo.setTipoArquivo(tipoArquivo);
                anexo.setObservacoes("Anexado durante a edição do PDI.");

                anexosDAO.adiciona(anexo);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI e Anexo atualizados com sucesso!");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI atualizado com sucesso!");
            }
            // --- FIM SALVAR NOVO ANEXO ---

            // Recarrega os dados no dashboard e na tela de edição
            dashboardController.recarregarPdis(null);
            // Re-chama o carregar PDI para atualizar a lista de anexos na tela
            carregarPdi(pdiParaEditar, dashItem, dashboardController);

            // fecharJanela(); // Pode ser omitido para permitir edições subsequentes/visualização do novo anexo

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