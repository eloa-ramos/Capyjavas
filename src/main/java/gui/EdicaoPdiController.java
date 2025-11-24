package gui;

import dao.PDIDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import modelo.PDI;
import modelo.PDIDashItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.awt.Desktop; // Necessário para abrir o arquivo
import dao.AnexosDAO;
import modelo.Anexos;
import javafx.stage.FileChooser;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;

public class EdicaoPdiController {

    @FXML private TextField txtColaborador;
    @FXML private TextField txtArea;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpPrazo;
    @FXML private TextField txtObjetivo;
    @FXML private TextArea txtObservacoes;
    @FXML private Button btnSalvar;

    @FXML private Button btnAnexar;
    @FXML private Label lblNomeArquivo;
    @FXML private VBox vboxAnexosExistentes;

    private PDI pdiParaEditar;
    private DashboardGUIController dashboardController;
    private PDIDashItem dashItem;
    private File arquivoSelecionado;

    private final AnexosDAO anexosDAO = new AnexosDAO();

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

        this.arquivoSelecionado = null;
        if (lblNomeArquivo != null) {
            lblNomeArquivo.setText("Nenhum arquivo selecionado.");
        }

        if (vboxAnexosExistentes != null && pdi != null) {
            List<Anexos> anexos = anexosDAO.listarPorPdi(pdi.getIdPdi());
            exibirAnexos(anexos);
        }
    }

    // --- MÉTODO MODIFICADO: EXIBIÇÃO COM HYPERLINK E BOTÃO ---
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

            // 1. Ícone e Tipo
            Label lblTipo = new Label(getTipoIcone(anexo.getTipoArquivo()));
            lblTipo.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

            // 2. Hyperlink (Abre o arquivo diretamente)
            Hyperlink linkNome = new Hyperlink(anexo.getNomeArquivo());
            linkNome.setTooltip(new Tooltip("Clique para Abrir (ID: " + anexo.getIdAnexo() + ")"));

            linkNome.setOnAction(e -> {
                File file = new File(anexo.getCaminhoArquivo());
                if (Desktop.isDesktopSupported() && file.exists()) {
                    try {
                        // Lógica de Abrir
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                        showAlert(Alert.AlertType.ERROR, "Erro de Acesso", "Não foi possível abrir o arquivo. Verifique as permissões.");
                    } catch (UnsupportedOperationException ex) {
                        showAlert(Alert.AlertType.ERROR, "Erro de Sistema", "A abertura de arquivos diretos não é suportada por esta plataforma.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Arquivo Indisponível",
                            "O arquivo físico não foi encontrado ou o sistema não suporta a abertura direta.");
                }
            });

            // 3. Botão de Download (Salva cópia para o PC)
            Button btnDownload = new Button("Download");
            btnDownload.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 3 8; -fx-font-size: 11px; -fx-cursor: hand;");

            btnDownload.setOnAction(e -> {
                File sourceFile = new File(anexo.getCaminhoArquivo());

                if (!sourceFile.exists()) {
                    showAlert(Alert.AlertType.ERROR, "Erro de Download", "O arquivo físico não foi encontrado no caminho original:\n" + anexo.getCaminhoArquivo());
                    return;
                }

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Salvar Cópia do Anexo");
                fileChooser.setInitialFileName(anexo.getNomeArquivo());

                File destFile = fileChooser.showSaveDialog((Stage) btnDownload.getScene().getWindow());

                if (destFile != null) {
                    try {
                        // Lógica de Salvar Como (Copia o arquivo)
                        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        showAlert(Alert.AlertType.INFORMATION, "Download Concluído",
                                "O arquivo foi salvo com sucesso em:\n" + destFile.getAbsolutePath());
                    } catch (IOException ex) {
                        showAlert(Alert.AlertType.ERROR, "Erro ao Salvar Arquivo",
                                "Falha ao copiar o arquivo para o destino. Detalhes: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });

            anexoBox.getChildren().addAll(lblTipo, linkNome, btnDownload);
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
            new PDIDAO().atualizarPDI(pdiParaEditar);

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
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI e Anexo adicionados/atualizados com sucesso!");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI atualizado com sucesso!");
            }

            dashboardController.recarregarPdis(null);

            PDI pdiAtualizado = new PDIDAO().buscarPdiPorId(pdiParaEditar.getIdPdi());
            carregarPdi(pdiAtualizado, dashItem, dashboardController);

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