package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.Usuario;

public class UsuarioGUIController {

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
        // Preenche o ComboBox com os tipos de acesso disponíveis
        tipoUsuarioCombo.setPromptText("Selecione o tipo de acesso");
    }

    @FXML
    void clickCadastrar(ActionEvent event) {
        try {
            String nome = nomeUsuario.getText().trim();
            String cpf = cpfUsuario.getText().trim();
            String cargo = cargoUsuario.getText().trim();
            String experiencia = experienciaUsuario.getText().trim();
            String observacoes = observacoesUsuario.getText().trim();
            String tipoAcesso = tipoUsuarioCombo.getValue();
            String email = emailUsuario.getText().trim();
            String senha = senhaUsuario.getText(); // Não use trim() em senhas cruas
            java.time.LocalDate dataNascimento = dataNascimentoUsuario.getValue(); // Pega a data

            // Validação básica
            if (nome.isEmpty() || cpf.isEmpty() || cargo.isEmpty() || tipoAcesso == null || email.isEmpty() || senha.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Preencha todos os campos obrigatórios!");
                return;
            }

            // ***** NOVA VALIDAÇÃO PARA DATA *****
            if (dataNascimento == null) {
                showAlert(Alert.AlertType.WARNING, "⚠️ Selecione a Data de Nascimento!");
                return; // Impede o cadastro se a data for nula
            }
            // ************************************


            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setCpf(cpf);
            usuario.setCargo(cargo);
            usuario.setExperiencia(experiencia);
            usuario.setObservacoes(observacoes);
            usuario.setTipoAcesso(tipoAcesso);
            usuario.setDataNascimento(dataNascimento); // Usa a variável local já validada
            usuario.setEmail(email);
            usuario.setSenha(senha); // Define a senha (sem trim)

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDAO.adiciona(usuario);

            showAlert(Alert.AlertType.INFORMATION, "✅ Usuário cadastrado com sucesso!");

            // Limpa os campos
            clearForm(); // Chama o método para limpar

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao cadastrar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para limpar o formulário (Crie se não existir ou ajuste)
    private void clearForm() {
        nomeUsuario.clear();
        cpfUsuario.clear();
        cargoUsuario.clear();
        experienciaUsuario.clear();
        observacoesUsuario.clear();
        dataNascimentoUsuario.setValue(null);
        tipoUsuarioCombo.setValue(null);
        emailUsuario.clear();
        senhaUsuario.clear();
    }

    // Método auxiliar para mostrar alertas (Crie se não existir ou ajuste)
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
