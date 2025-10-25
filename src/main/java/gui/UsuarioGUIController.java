package gui;

import dao.UsuarioDAO;
import dao.AreaDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Usuario;
import modelo.Area;

import java.util.List;
import javafx.stage.Stage; // Importação corrigida

public class UsuarioGUIController {

    @FXML private ComboBox<Area> areaCombo;

    @FXML private TextField nomeUsuario;
    @FXML private TextField cpfUsuario;
    @FXML private DatePicker dataNascimentoUsuario;
    @FXML private TextField cargoUsuario;
    @FXML private TextField experienciaUsuario;
    @FXML private TextField observacoesUsuario;
    @FXML private TextField emailUsuario;
    @FXML private PasswordField senhaUsuario;
    @FXML private ComboBox<String> tipoUsuarioCombo;
    @FXML private Button cadastrarUsuarioBtn;
    @FXML private Button sairBtn;

    private AreaDAO areaDAO;

    @FXML
    public void initialize() {
        tipoUsuarioCombo.setPromptText("Selecione o tipo de acesso");

        this.areaDAO = new AreaDAO();
        areaCombo.setEditable(true);

        try {
            List<Area> areas = areaDAO.listarTodas();
            areaCombo.getItems().addAll(areas);
            areaCombo.setPromptText("Selecione ou digite uma Área");
        } catch (Exception e) {
            System.err.println("Falha ao carregar áreas: " + e.getMessage());
            areaCombo.setPromptText("Erro ao carregar áreas");
            areaCombo.setDisable(true);
        }
    }

    @FXML
    void clickCadastrar(ActionEvent event) {
        try {
            // --- Campos de Texto ---
            String nome = nomeUsuario.getText().trim();
            String cpf = cpfUsuario.getText().trim();
            String cargo = cargoUsuario.getText().trim();
            String experiencia = experienciaUsuario.getText().trim();
            String observacoes = observacoesUsuario.getText().trim();
            String tipoAcesso = tipoUsuarioCombo.getValue();
            String email = emailUsuario.getText().trim();
            String senha = senhaUsuario.getText();
            java.time.LocalDate dataNascimento = dataNascimentoUsuario.getValue();

            // --- ================================== ---
            // --- LÓGICA DE CORREÇÃO PARA O ERRO     ---
            // --- ================================== ---

            // 1. Não usamos mais areaCombo.getValue(), pois ele causa o erro.
            //    Pegamos APENAS o texto que o usuário digitou ou selecionou.
            String nomeAreaFinal = areaCombo.getEditor().getText().trim();

            // --- FIM DA CORREÇÃO ---

            // Validação
            if (nome.isEmpty() || cpf.isEmpty() || cargo.isEmpty() || tipoAcesso == null || email.isEmpty() || senha.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Preencha todos os campos obrigatórios!");
                return;
            }
            // 2. Validamos o texto que pegamos
            if (nomeAreaFinal.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "⚠️ A Área é obrigatória (selecione ou digite uma nova)!");
                return;
            }
            if (dataNascimento == null) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Selecione a Data de Nascimento!");
                return;
            }

            // 3. O DAO cuida de buscar ou criar a área com base no nome (String)
            int idDaArea = this.areaDAO.buscarOuCriarArea(nomeAreaFinal);

            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setCpf(cpf);
            usuario.setCargo(cargo);
            usuario.setIdArea(idDaArea);
            usuario.setExperiencia(experiencia);
            usuario.setObservacoes(observacoes);
            usuario.setTipoAcesso(tipoAcesso);
            usuario.setDataNascimento(dataNascimento);
            usuario.setEmail(email);
            usuario.setSenha(senha);

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDAO.adiciona(usuario);

            showAlert(Alert.AlertType.INFORMATION, "✅ Usuário cadastrado com sucesso!");
            clearForm();

            // Opcional: recarregar a lista de áreas para incluir a nova
            areaCombo.getItems().setAll(areaDAO.listarTodas());


        } catch (Exception e) {
            // Se o erro for a ClassCastException, esta mensagem genérica o pegará
            showAlert(Alert.AlertType.ERROR, "Erro ao cadastrar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        nomeUsuario.clear();
        cpfUsuario.clear();
        cargoUsuario.clear();
        experienciaUsuario.clear();
        observacoesUsuario.clear();
        dataNascimentoUsuario.setValue(null);
        tipoUsuarioCombo.setValue(null);
        areaCombo.setValue(null);
        areaCombo.getEditor().clear(); // Limpa o texto digitado
        emailUsuario.clear();
        senhaUsuario.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erro" : "Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void clickSair(ActionEvent event) {
        // Fecha apenas a janela atual
        Stage stage = (Stage) sairBtn.getScene().getWindow();
        stage.close();
    }
}