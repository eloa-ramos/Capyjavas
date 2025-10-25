package gui;

import dao.MetasDAO; //
import dao.PDIDAO;
import dao.SkillsDAO; //
import dao.UsuarioDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import modelo.Metas; //
import modelo.PDI;
import modelo.Skills; //
import modelo.Usuario;

import java.math.BigDecimal; // IMPORTADO
import java.time.LocalDate;
import java.util.List;

public class CadastroPdiController {

    // --- Componentes FXML ---
    @FXML
    private ComboBox<Usuario> cmbColaborador;

    // --- NOVOS COMPONENTES FXML ---
    @FXML
    private TextField txtObjetivo; // Veio do FXML
    @FXML
    private TextField txtMetaPontuacao; // Veio do FXML
    @FXML
    private TextField txtPontuacaoObtida; // Veio do FXML

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

    // --- DAOs ---
    private UsuarioDAO usuarioDAO;
    private PDIDAO pdiDAO;
    private SkillsDAO skillsDAO; //
    private MetasDAO metasDAO; //

    // --- Lista Observável (apenas para usuário) ---
    private ObservableList<Usuario> listaUsuariosEncontrados = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Inicializa todos os DAOs
        this.usuarioDAO = new UsuarioDAO();
        this.pdiDAO = new PDIDAO();
        this.skillsDAO = new SkillsDAO(); //
        this.metasDAO = new MetasDAO(); //

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
            // "A Definir" é o tipo padrão para skills criadas assim
            int idSkill = skillsDAO.buscarOuCriarSkill(nomeObjetivo, "A Definir");


            // --- 4. Salvar o PDI ---
            PDI pdi = new PDI();
            pdi.setIdColaborador(colaborador.getId());
            pdi.setDataInicio(inicio);
            pdi.setDataFim(fim);
            pdi.setObservacoes(observacoes);

            // Chama o DAO (da resposta anterior) e pega o ID retornado
            int novoPdiId = pdiDAO.cadastrarPDI(pdi);

            // --- 5. Salvar a Meta (O VÍNCULO) ---
            Metas meta = new Metas(novoPdiId, idSkill, metaPontuacao, pontuacaoObtida); //

            metasDAO.adiciona(meta); //

            // --- 6. Sucesso ---
            showAlert(Alert.AlertType.INFORMATION, "PDI e Meta cadastrados com sucesso!");
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