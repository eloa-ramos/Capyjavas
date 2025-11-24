package gui;

import dao.MetasDAO;
import dao.PDIDAO;
import dao.SkillsDAO;
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modelo.Metas;
import modelo.PDI;
import modelo.Skills;
import modelo.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// NOVOS IMPORTS
import dao.AnexosDAO;
import modelo.Anexos;
import javafx.stage.FileChooser;
import java.io.File;
// FIM NOVOS IMPORTS

public class CadastroPdiController {

    // --- Componentes FXML ---
    @FXML
    private ComboBox<Usuario> cmbColaborador;

    // --- NOVOS COMPONENTES FXML ---
    @FXML
    private TextField txtObjetivo;
    @FXML
    private TextField txtMetaPontuacao;
    @FXML
    private TextField txtPontuacaoObtida;

    // --- NOVOS CAMPOS FXML PARA ANEXO ---
    @FXML
    private Button btnAnexar;
    @FXML
    private Label lblNomeArquivo;
    // --- FIM NOVOS CAMPOS FXML PARA ANEXO ---

    @FXML
    private DatePicker dateInicio;

    @FXML
    private DatePicker dateFim;

    @FXML
    private TextArea txtObservacoes;

    @FXML
    private Button btnCadastrar;

    // --- Variáveis de Controle ---
    private Usuario usuarioLogado;
    private File arquivoSelecionado; // NOVO: Guarda a referência do arquivo

    // --- DAOs ---
    private UsuarioDAO usuarioDAO;
    private PDIDAO pdiDAO;
    private SkillsDAO skillsDAO;
    private MetasDAO metasDAO;
    private AnexosDAO anexosDAO; // NOVO DAO

    // --- Lista Observável (apenas para usuário) ---
    private ObservableList<Usuario> listaUsuariosEncontrados = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Inicializa todos os DAOs
        this.usuarioDAO = new UsuarioDAO();
        this.pdiDAO = new PDIDAO();
        this.skillsDAO = new SkillsDAO();
        this.metasDAO = new MetasDAO();
        this.anexosDAO = new AnexosDAO(); // <--- INICIALIZAÇÃO

        // Configura o ComboBox de Colaborador (lógica existente)
        setupCmbColaborador();

        // Não precisamos mais carregar skills
    }

    // Método de configuração do Colaborador (seu código original)
    private void setupCmbColaborador() {
        cmbColaborador.setItems(listaUsuariosEncontrados);
        cmbColaborador.setEditable(true);

        cmbColaborador.setConverter(new StringConverter<Usuario>() {
            @Override
            public String toString(Usuario usuario) {
                return (usuario == null) ? null : usuario.getNome();
            }

            @Override
            public Usuario fromString(String string) {
                return listaUsuariosEncontrados.stream()
                        .filter(u -> u.getNome().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        cmbColaborador.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                listaUsuariosEncontrados.clear();
                cmbColaborador.hide();
                return;
            }
            Usuario selectedUser = cmbColaborador.getSelectionModel().getSelectedItem();
            if (selectedUser != null && newText.equals(selectedUser.getNome())) {
                return;
            }
            buscarColaboradores(newText);
        });
    }

    private void buscarColaboradores(String textoDigitado) {
        try {
            List<Usuario> usuarios = usuarioDAO.buscarPorNome(textoDigitado);
            listaUsuariosEncontrados.setAll(usuarios);

            if (!usuarios.isEmpty()) {
                cmbColaborador.show();
            } else {
                cmbColaborador.hide();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setUsuario(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    // --- NOVO MÉTODO: Lidar com a seleção de arquivo (CORREÇÃO DE ERRO) ---
    @FXML
    private void handleAnexarDocumento() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Anexar Documento ao PDI");

        // Define filtros de extensão
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos e Imagens", "*.pdf", "*.docx", "*.doc", "*.png", "*.jpeg", "*.jpg"),
                new FileChooser.ExtensionFilter("Todos os Arquivos", "*.*")
        );

        // Obtém o Stage atual
        Stage stage = (Stage) btnAnexar.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.arquivoSelecionado = selectedFile;
            lblNomeArquivo.setText(selectedFile.getName());
            showAlert(Alert.AlertType.INFORMATION, "Arquivo selecionado: " + selectedFile.getName());
        } else {
            this.arquivoSelecionado = null;
            lblNomeArquivo.setText("Nenhum arquivo selecionado.");
        }
    }
    // --- FIM NOVO MÉTODO ---

    @FXML
    private void cadastrarPdi() {
        try {
            // --- 1. Obter dados do Formulário ---
            Usuario colaborador = cmbColaborador.getValue();
            LocalDate inicio = dateInicio.getValue();
            LocalDate fim = dateFim.getValue();
            String observacoes = txtObservacoes.getText().trim();

            // --- NOVOS DADOS ---
            String nomeObjetivo = txtObjetivo.getText().trim();
            String metaPontuacaoStr = txtMetaPontuacao.getText().trim();
            String pontuacaoObtidaStr = txtPontuacaoObtida.getText().trim();

            // --- 2. Validação ---
            if (colaborador == null) {
                showAlert(Alert.AlertType.WARNING, "Colaborador inválido. Por favor, digite o nome e SELECIONE um colaborador da lista.");
                return;
            }
            if (inicio == null || fim == null) {
                showAlert(Alert.AlertType.WARNING, "Preencha as datas de início e fim do PDI.");
                return;
            }
            if (fim.isBefore(inicio)) {
                showAlert(Alert.AlertType.WARNING, "A data de fim não pode ser anterior à data de início.");
                return;
            }
            if (nomeObjetivo.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Digite um nome para o Objetivo (Skill).");
                return;
            }

            // Validação de números
            BigDecimal metaPontuacao;
            BigDecimal pontuacaoObtida;
            try {
                metaPontuacao = new BigDecimal(metaPontuacaoStr.isEmpty() ? "100" : metaPontuacaoStr);
                pontuacaoObtida = new BigDecimal(pontuacaoObtidaStr.isEmpty() ? "0" : pontuacaoObtidaStr);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Valores de pontuação inválidos. Use apenas números (ex: 100 ou 0).");
                return;
            }


            // --- 3. Buscar ou Criar a Skill ---
            int idSkill = skillsDAO.buscarOuCriarSkill(nomeObjetivo, "A Definir");


            // --- 4. Salvar o PDI ---
            PDI pdi = new PDI();
            pdi.setIdColaborador(colaborador.getId());
            pdi.setDataInicio(inicio);
            pdi.setDataFim(fim);
            pdi.setObservacoes(observacoes);

            int novoPdiId = pdiDAO.cadastrarPDI(pdi);

            // --- 5. Salvar a Meta (O VÍNCULO) ---
            Metas meta = new Metas(novoPdiId, idSkill, metaPontuacao, pontuacaoObtida);
            metasDAO.adiciona(meta);

            // --- NOVO PASSO: SALVAR O ANEXO (SE HOUVER) ---
            if (arquivoSelecionado != null) {
                String nomeArquivo = arquivoSelecionado.getName();
                String caminho = arquivoSelecionado.getAbsolutePath();

                // Extrai a extensão para verificar o tipo, tratando o caso onde não há extensão
                int lastDot = nomeArquivo.lastIndexOf('.');
                String extensao = (lastDot > 0) ? nomeArquivo.substring(lastDot + 1).toLowerCase() : "";

                // Mapeamento simples para o ENUM('pdf', 'png', 'jpeg') no banco de dados:
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
                anexo.setIdPdi(novoPdiId);
                anexo.setNomeArquivo(nomeArquivo);
                anexo.setCaminhoArquivo(caminho);
                anexo.setTipoArquivo(tipoArquivo);
                anexo.setObservacoes("Anexado durante o cadastro do PDI.");

                anexosDAO.adiciona(anexo);
                showAlert(Alert.AlertType.INFORMATION, "PDI, Meta e Anexo cadastrados com sucesso!");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "PDI e Meta cadastrados com sucesso!");
            }
            // --- FIM NOVO PASSO ---

            clearForm();
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

    private void clearForm() {
        cmbColaborador.setValue(null);
        cmbColaborador.getEditor().clear();
        dateInicio.setValue(null);
        dateFim.setValue(null);
        txtObservacoes.clear();
        txtObjetivo.clear();
        txtMetaPontuacao.clear();
        txtPontuacaoObtida.clear();

        // Limpar campos de anexo
        arquivoSelecionado = null;
        if (lblNomeArquivo != null) {
            lblNomeArquivo.setText("Nenhum arquivo selecionado.");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCadastrar.getScene().getWindow();
        stage.close();
    }
}