package gui;

import dao.UsuarioDAO;
import dao.AreaDAO; // IMPORT NOVO
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Usuario;
import modelo.Area; // IMPORT NOVO

import java.util.List;

public class UsuarioGUIController {

    @FXML private ComboBox<Area> areaCombo; // Tipo alterado para 'Area'

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

    @FXML
    public void initialize() {
        tipoUsuarioCombo.setPromptText("Selecione o tipo de acesso");

        // NOVO: Inicializa a ComboBox de Áreas buscando do banco de dados
        AreaDAO areaDAO = new AreaDAO();
        try {
            List<Area> areas = areaDAO.listarTodas();
            areaCombo.getItems().addAll(areas);
            areaCombo.setPromptText("Selecione a Área");
        } catch (Exception e) {
            System.err.println("Falha ao carregar áreas: " + e.getMessage());
            areaCombo.setPromptText("Erro ao carregar áreas");
            areaCombo.setDisable(true);
        }
    }

    @FXML
    void clickCadastrar(ActionEvent event) {
        try {
            String nome = nomeUsuario.getText().trim();
            String cpf = cpfUsuario.getText().trim();
            String cargo = cargoUsuario.getText().trim();
            Area areaSelecionada = areaCombo.getValue(); // Obtém o objeto Area
            String experiencia = experienciaUsuario.getText().trim();
            String observacoes = observacoesUsuario.getText().trim();
            String tipoAcesso = tipoUsuarioCombo.getValue();
            String email = emailUsuario.getText().trim();
            String senha = senhaUsuario.getText();
            java.time.LocalDate dataNascimento = dataNascimentoUsuario.getValue();

            // Validação
            if (nome.isEmpty() || cpf.isEmpty() || cargo.isEmpty() || tipoAcesso == null || email.isEmpty() || senha.isEmpty() || areaSelecionada == null) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Preencha todos os campos obrigatórios (incluindo a Área)!");
                return;
            }

            if (dataNascimento == null) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Selecione a Data de Nascimento!");
                return;
            }

            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setCpf(cpf);
            usuario.setCargo(cargo);
            usuario.setIdArea(areaSelecionada.getIdArea()); // NOVO: Define a FK (id)
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

        } catch (Exception e) {
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
        Platform.exit();
        System.exit(0);
    }
}