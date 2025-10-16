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

    // CAMPOS EXISTENTES
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Button btnEntrar;
    @FXML private ImageView logoImage;

    // NOVOS CAMPOS PARA O TOGGLE
    @FXML private TextField txtTextoSenha; // Campo para mostrar a senha
    @FXML private Button btnToggleSenha;
    @FXML private ImageView iconToggle;

    private boolean senhaVisivel = false; // Estado do toggle
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

    // NOVO MÉTODO: Alterna a visibilidade da senha
    @FXML
    private void toggleVisibilidadeSenha() {
        senhaVisivel = !senhaVisivel;

        String pathOlhoAberto = "/gui/images/icon_eye_open.png";
        String pathOlhoFechado = "/gui/images/icon_eye_closed.png";

        if (senhaVisivel) {
            // Mudar para o modo VISÍVEL (TextField)
            txtTextoSenha.setText(txtSenha.getText());
            txtTextoSenha.setManaged(true);
            txtTextoSenha.setVisible(true);
            txtSenha.setManaged(false);
            txtSenha.setVisible(false);

            // Mudar ícone para OLHO ABERTO
            try {
                iconToggle.setImage(new Image(getClass().getResourceAsStream(pathOlhoAberto)));
            } catch (Exception e) { System.err.println("Ícone olho aberto não encontrado."); }

        } else {
            // Mudar para o modo OCULTO (PasswordField)
            txtSenha.setText(txtTextoSenha.getText());
            txtSenha.setManaged(true);
            txtSenha.setVisible(true);
            txtTextoSenha.setManaged(false);
            txtTextoSenha.setVisible(false);

            // Mudar ícone para OLHO FECHADO
            try {
                iconToggle.setImage(new Image(getClass().getResourceAsStream(pathOlhoFechado)));
            } catch (Exception e) { System.err.println("Ícone olho fechado não encontrado."); }
        }
    }


    @FXML
    private void handleBtnEntrar() {
        // Lógica de autenticação:
        // Pega o texto do campo ativo no momento (txtSenha ou txtTextoSenha)
        String email = txtEmail.getText().trim();
        String senha;

        if (senhaVisivel) {
            senha = txtTextoSenha.getText().trim();
        } else {
            senha = txtSenha.getText().trim();
        }

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
            // Limpa o campo ativo:
            if (senhaVisivel) {
                txtTextoSenha.clear();
            } else {
                txtSenha.clear();
            }
        }
    }

    // ... restante do código (abrirCadastro e showAlert)
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