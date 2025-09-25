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
            // Validação básica
            if (nome.isEmpty() || cpf.isEmpty() || cargo.isEmpty() || tipoAcesso == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "⚠️ Preencha todos os campos obrigatórios!");
                alert.showAndWait();
                return;
            }

            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setCpf(cpf);
            usuario.setCargo(cargo);
            usuario.setExperiencia(experiencia);
            usuario.setObservacoes(observacoes);
            usuario.setTipoAcesso(tipoAcesso);
            usuario.setDataNascimento(dataNascimentoUsuario.getValue());

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDAO.adiciona(usuario);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Usuário cadastrado com sucesso!");
            alert.showAndWait();

            // Limpa os campos
            nomeUsuario.clear();
            cpfUsuario.clear();
            cargoUsuario.clear();
            experienciaUsuario.clear();
            observacoesUsuario.clear();
            dataNascimentoUsuario.setValue(null);
            tipoUsuarioCombo.setValue(null);

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao cadastrar: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
