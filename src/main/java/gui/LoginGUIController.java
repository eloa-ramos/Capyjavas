package gui;

import dao.UsuarioDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage; // Importação necessária para fechar a tela de Login
import modelo.Usuario;

// Importação da classe DashboardGUI que você deseja abrir
import gui.DashboardGUI;

public class LoginGUIController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Button btnEntrar;

    @FXML
    void handleBtnEntrar(ActionEvent event) {
        try {
            String email = txtEmail.getText().trim();
            String senha = txtSenha.getText().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "⚠️ Por favor, preencha o email e a senha.");
                alert.showAndWait();
                return;
            }

            // 1. Cria uma instância do DAO
            UsuarioDAO usuarioDAO = new UsuarioDAO();

            // 2. Chama o método de autenticação
            Usuario usuarioLogado = usuarioDAO.autenticar(email, senha);

            // 3. Verifica o resultado
            if (usuarioLogado != null) {
                // SUCESSO NO LOGIN!

                // --- LÓGICA DE VERIFICAÇÃO DE ACESSO ---

                // ASSUME que Usuario.java tem o método getTipoAcesso()
                String tipoAcesso = usuarioLogado.getTipoAcesso();

                if ("RH".equalsIgnoreCase(tipoAcesso)) { // Verifica se é RH

                    // Alerta de Sucesso
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Login RH realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + ".");
                    alert.showAndWait();

                    // 4. REDIRECIONAMENTO PARA O DASHBOARD RH

                    // A. Fecha o Stage (janela) atual do Login
                    Stage currentStage = (Stage) btnEntrar.getScene().getWindow();
                    currentStage.close();

                    // B. Abre a nova tela do Dashboard RH
                    DashboardGUI dashboardApp = new DashboardGUI();
                    Stage dashboardStage = new Stage();
                    dashboardApp.start(dashboardStage);

                } else {
                    // Se não for RH, pode ser outro tipo (ex: Gestor Geral)

                    // Alerta para outros tipos (Você pode refinar isso depois)
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Login realizado com sucesso! Tipo: " + tipoAcesso + ". Redirecionando para tela padrão.");
                    alert.showAndWait();

                    // TODO: Implementar a lógica para abrir o Dashboard de Gestor Geral ou a tela padrão
                }


            } else {
                // FALHA NO LOGIN!
                Alert alert = new Alert(Alert.AlertType.ERROR, "Login falhou. Email ou senha incorretos.");
                alert.showAndWait();

                // Limpa o campo de senha para uma nova tentativa
                txtSenha.clear();
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erro durante o login: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}