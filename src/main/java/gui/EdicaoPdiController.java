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
import java.awt.Desktop;
import dao.AnexosDAO;
import modelo.Anexos;
import javafx.stage.FileChooser;
import java.util.List;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import dao.MetasDAO;
import javafx.scene.control.ButtonType;
import dao.SkillsDAO;
import modelo.Metas;
import modelo.Skills;
import java.math.BigDecimal;

public class EdicaoPdiController {

    // CAMPOS DE DADOS DO PDI
    @FXML private TextField txtColaborador;
    @FXML private TextField txtArea;
    @FXML private DatePicker dpDataInicio;
    @FXML private DatePicker dpPrazo;
    @FXML private TextField txtObjetivo;
    @FXML private TextArea txtObservacoes;

    // NOVOS CAMPOS DE METAS/PONTUAÇÃO
    @FXML private TextField txtMetaPontuacao;
    @FXML private TextField txtPontuacaoObtida;

    // CAMPOS DE BOTÕES/ANEXOS
    @FXML private Button btnSalvar;
    @FXML private Button btnConcluir;
    @FXML private Button btnAnexar;
    @FXML private Label lblNomeArquivo;
    @FXML private VBox vboxAnexosExistentes;

    private PDI pdiParaEditar;
    private Metas metaParaEditar; // Armazena a meta existente (assumindo 1 por PDI)
    private Skills skillExistente; // Armazena a skill existente
    private DashboardGUIController dashboardController;
    private PDIDashItem dashItem;
    private File arquivoSelecionado;

    private final AnexosDAO anexosDAO = new AnexosDAO();
    private final MetasDAO metasDAO = new MetasDAO();
    private final SkillsDAO skillsDAO = new SkillsDAO();

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

        // 1. CARREGAR DADOS BÁSICOS
        txtColaborador.setText(item.getColaborador());
        txtArea.setText(item.getArea());
        dpDataInicio.setValue(pdi.getDataInicio());
        dpPrazo.setValue(pdi.getDataFim());
        txtObservacoes.setText(pdi.getObservacoes());

        // 2. CARREGAR DADOS DE META/PONTUAÇÃO (Assumindo 1 meta por PDI)
        this.metaParaEditar = metasDAO.buscarMetaPorPdi(pdi.getIdPdi());

        if (metaParaEditar != null) {
            this.skillExistente = skillsDAO.buscarPorId(metaParaEditar.getIdSkill());

            // Preenche os campos de meta/skill
            txtObjetivo.setText(skillExistente.getNomeSkill());
            txtMetaPontuacao.setText(metaParaEditar.getMetaPontuacao().toPlainString());
            txtPontuacaoObtida.setText(metaParaEditar.getPontuacaoObtida().toPlainString());
        } else {
            // Caso não haja meta cadastrada (o que é raro, mas possível)
            txtObjetivo.setText(item.getObjetivo()); // Usa o objetivo do dashitem (que pode ser N/A)
            txtMetaPontuacao.setText("100");
            txtPontuacaoObtida.setText("0");
        }


        // 3. CARREGAR ANEXOS
        this.arquivoSelecionado = null;
        if (lblNomeArquivo != null) {
            lblNomeArquivo.setText("Nenhum arquivo selecionado.");
        }

        if (vboxAnexosExistentes != null && pdi != null) {
            List<Anexos> anexos = anexosDAO.listarPorPdi(pdi.getIdPdi());
            exibirAnexos(anexos);
        }

        // 4. LÓGICA DE VISIBILIDADE DO BOTÃO CONCLUIR
        atualizarVisibilidadeBtnConcluir();
    }

    // Método auxiliar para atualização da visibilidade do botão de conclusão
    private void atualizarVisibilidadeBtnConcluir() {
        if (btnConcluir != null && dashboardController.getUsuarioLogado() != null && dashItem != null) {
            String status = dashItem.getStatus();
            boolean isConcluido = status != null && "Concluído".equalsIgnoreCase(status.trim());

            String tipoAcesso = dashboardController.getUsuarioLogado().getTipoAcesso().toUpperCase().replace(" ", "");
            boolean podeEditar = "GESTORGERAL".equals(tipoAcesso) || "RH".equals(tipoAcesso);

            btnConcluir.setVisible(!isConcluido && podeEditar);
            btnConcluir.setManaged(!isConcluido && podeEditar);
        } else if (btnConcluir != null) {
            btnConcluir.setVisible(false);
            btnConcluir.setManaged(false);
        }
    }


    // --- MÉTODO MODIFICADO: EXIBIÇÃO COM HYPERLINK E BOTÃO (CÓDIGO OMITIDO POR SER IGUAL) ---
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

        // Validação de campos de Meta/Pontuação
        String nomeObjetivo = txtObjetivo.getText().trim();
        String metaPontuacaoStr = txtMetaPontuacao.getText().trim();
        String pontuacaoObtidaStr = txtPontuacaoObtida.getText().trim();

        if (nomeObjetivo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Atenção", "O campo Objetivo (Skill) é obrigatório.");
            return;
        }

        BigDecimal metaPontuacao;
        BigDecimal pontuacaoObtida;
        try {
            metaPontuacao = new BigDecimal(metaPontuacaoStr.isEmpty() ? "100" : metaPontuacaoStr);
            pontuacaoObtida = new BigDecimal(pontuacaoObtidaStr.isEmpty() ? "0" : pontuacaoObtidaStr);

            if (metaPontuacao.compareTo(BigDecimal.ZERO) < 0 || pontuacaoObtida.compareTo(BigDecimal.ZERO) < 0) {
                showAlert(Alert.AlertType.WARNING, "Atenção", "Os valores de pontuação não podem ser negativos.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Valores de pontuação inválidos. Use apenas números (ex: 100 ou 0).");
            return;
        }

        // 1. Atualizar PDI (Datas e Observações)
        pdiParaEditar.setDataInicio(dpDataInicio.getValue());
        pdiParaEditar.setDataFim(dpPrazo.getValue());
        pdiParaEditar.setObservacoes(txtObservacoes.getText().trim());

        try {
            new PDIDAO().atualizarPDI(pdiParaEditar);

            int idSkill;
            if (metaParaEditar != null && skillExistente != null && skillExistente.getNomeSkill().equalsIgnoreCase(nomeObjetivo)) {
                // O nome do objetivo não mudou, usa o ID existente.
                idSkill = skillExistente.getIdSkill();
            } else {
                // O nome do objetivo mudou ou não havia meta: busca/cria a skill
                idSkill = skillsDAO.buscarOuCriarSkill(nomeObjetivo, "A Definir");
            }

            // 2. Atualizar ou Criar Meta
            if (metaParaEditar != null) {
                // Atualiza a meta existente
                metaParaEditar.setIdSkill(idSkill);
                metaParaEditar.setMetaPontuacao(metaPontuacao);
                metaParaEditar.setPontuacaoObtida(pontuacaoObtida);
                metasDAO.atualiza(metaParaEditar);
            } else {
                // Cria uma nova meta (se for a primeira vez que está sendo salva)
                Metas novaMeta = new Metas();
                novaMeta.setIdPdi(pdiParaEditar.getIdPdi());
                novaMeta.setIdSkill(idSkill);
                novaMeta.setMetaPontuacao(metaPontuacao);
                novaMeta.setPontuacaoObtida(pontuacaoObtida);
                metasDAO.adiciona(novaMeta);
                this.metaParaEditar = metasDAO.buscarMetaPorPdi(pdiParaEditar.getIdPdi()); // Recarrega para usar o objeto no futuro
            }


            // 3. Salvar Anexo (se houver)
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
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI, Meta e Anexo adicionados/atualizados com sucesso!");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "PDI e Meta atualizados com sucesso!");
            }

            // 4. Recarrega o dashboard principal
            dashboardController.recarregarPdis(null);

            // 5. Recarrega a própria tela de edição para atualizar a lista de anexos e o botão "Concluir"
            PDI pdiAtualizado = new PDIDAO().buscarPdiPorId(pdiParaEditar.getIdPdi());
            carregarPdi(pdiAtualizado, dashItem, dashboardController);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao salvar as alterações: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // MÉTODO: Lida com o botão "Concluir PDI"
    @FXML
    private void handleConcluirPdi() {
        if (pdiParaEditar == null) {
            showAlert(Alert.AlertType.ERROR, "Erro", "PDI não carregado.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Tem certeza que deseja marcar este PDI como CONCLUÍDO (100% de atingimento em todas as metas)? Esta ação não pode ser desfeita facilmente.",
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirmação de Conclusão");
        confirmation.setHeaderText("Confirmação de Conclusão do PDI");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    // 1. Marca as metas como 100% concluídas
                    metasDAO.marcarComoConcluido(pdiParaEditar.getIdPdi());

                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", "✅ PDI marcado como CONCLUÍDO com sucesso! O dashboard será atualizado.");

                    // 2. Atualiza o dashboard principal
                    if (dashboardController != null) {
                        dashboardController.recarregarPdis(null);
                    }

                    // 3. Fecha a janela
                    Stage stage = (Stage) btnConcluir.getScene().getWindow();
                    stage.close();

                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erro", "❌ Falha ao marcar o PDI como concluído: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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