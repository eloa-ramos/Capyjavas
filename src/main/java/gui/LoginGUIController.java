package gui;

import dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import modelo.Usuario;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class LoginGUIController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Button btnEntrar;
    @FXML private ImageView logoImage;

    private Usuario usuarioLogado;

    @FXML
    private void initialize() {
        // Carregar logo
        try {
            Image img = new Image(getClass().getResourceAsStream("/gui/images/logo_youtan_transparente.png"));
            logoImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Logo não encontrada: " + e.getMessage());
        }

    }

    @FXML
    private void handleBtnEntrar() {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Preencha email e senha.");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        usuarioLogado = dao.autenticar(email, senha);

        if (usuarioLogado != null) {

            // Abrir Dashboard
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/DashboardGUI.fxml"));
                Parent root = loader.load();

                DashboardGUIController controller = loader.getController();
                controller.setUsuario(usuarioLogado);

                Stage stage = (Stage) btnEntrar.getScene().getWindow();
                stage.close();

                Stage dashStage = new Stage();
                dashStage.setTitle("Dashboard YouTan");
                dashStage.setScene(new Scene(root));
                dashStage.setMaximized(true);
                dashStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erro ao abrir Dashboard: " + e.getMessage());
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "❌ Email ou senha incorretos.");
            txtSenha.clear();
        }
    }

    @FXML
    private void abrirCadastro() {
        if (usuarioLogado != null && "RH".equalsIgnoreCase(usuarioLogado.getTipoAcesso())) {
            CadastroPdiWindow cadastro = new CadastroPdiWindow(usuarioLogado);
            cadastro.show();
        }
    }

    private void showAlert(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo, mensagem);
        alert.showAndWait();
    }
}
