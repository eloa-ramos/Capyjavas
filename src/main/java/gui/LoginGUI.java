package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import modelo.Usuario;

public class LoginGUI extends Application {

    private TextField txtEmail;
    private PasswordField txtSenha;
    private Button btnEntrar;
    private Button btnCadastrar; // Apenas para RH
    private Label lblTitle;

    @Override
    public void start(Stage stage) {
        stage.setTitle("YouTan - Login");

        // --- Título ---
        lblTitle = new Label("YouTan");
        lblTitle.setFont(Font.font("Roboto", 32));
        lblTitle.setTextFill(Color.web("#333"));

        // --- Campos de login ---
        txtEmail = new TextField();
        txtEmail.setPromptText("Email");
        txtEmail.setPrefWidth(300);
        txtEmail.setStyle("-fx-background-radius: 6px; -fx-border-radius: 6px; -fx-border-color: #ccc; -fx-padding: 8px;");

        txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha");
        txtSenha.setPrefWidth(300);
        txtSenha.setStyle("-fx-background-radius: 6px; -fx-border-radius: 6px; -fx-border-color: #ccc; -fx-padding: 8px;");

        // --- Botão Entrar ---
        btnEntrar = new Button("Entrar");
        btnEntrar.setPrefWidth(300);
        btnEntrar.setFont(Font.font(14));
        btnEntrar.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 10 0 10 0;"
        );
        btnEntrar.setEffect(new DropShadow(2, Color.GRAY));

        btnEntrar.setOnMouseEntered(e -> btnEntrar.setStyle(
                "-fx-background-color: #45a049; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10 0 10 0;"
        ));
        btnEntrar.setOnMouseExited(e -> btnEntrar.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 8px; -fx-padding: 10 0 10 0;"
        ));

        // --- Botão Cadastrar (visível apenas para RH) ---
        btnCadastrar = new Button("Cadastrar Usuário");
        btnCadastrar.setPrefWidth(300);
        btnCadastrar.setFont(Font.font(13));
        btnCadastrar.setStyle(
                "-fx-background-color: #2196F3; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 8 0 8 0;"
        );
        btnCadastrar.setEffect(new DropShadow(1, Color.GRAY));
        btnCadastrar.setVisible(false); // Inicialmente escondido

        // --- Layout ---
        VBox vbox = new VBox(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(lblTitle, txtEmail, txtSenha, btnEntrar, btnCadastrar);

        BorderPane root = new BorderPane();
        root.setCenter(vbox);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Scene scene = new Scene(root, 400, 400);
        //css global e de login
        scene.getStylesheets().add(getClass().getResource("/gui/css/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/css/Login.css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        // --- Eventos ---
        btnEntrar.setOnAction(e -> handleLogin(stage));
    }

    private void handleLogin(Stage stage) {
        try {
            String email = txtEmail.getText().trim();
            String senha = txtSenha.getText().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "⚠️ Por favor, preencha o email e a senha.");
                alert.showAndWait();
                return;
            }

            Usuario usuarioLogado = new dao.UsuarioDAO().autenticar(email, senha);

            if (usuarioLogado != null) {
                String tipoAcesso = usuarioLogado.getTipoAcesso();

                // --- Mostrar botão cadastrar apenas para RH ---
                if ("RH".equalsIgnoreCase(tipoAcesso)) {
                    btnCadastrar.setVisible(true);
                    btnCadastrar.setOnAction(ev -> {
                        // Aqui você chamaria a tela de cadastro
                        System.out.println("Abrir tela de cadastro de usuário");
                    });
                }

                // Fecha a janela de login
                stage.close();

                // --- Abrir Dashboard ---
                DashboardGUI dashboard = new DashboardGUI(usuarioLogado);
                dashboard.start(new Stage());

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Login falhou.\nEmail ou senha incorretos.");
                alert.showAndWait();
                txtSenha.clear();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro durante o login: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
