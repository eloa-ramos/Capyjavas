package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RootGUIController {

    // Método que será chamado quando qualquer botão "Entrar" for clicado
    @FXML
    void handleEntrar(ActionEvent event) {

        // Obter o Stage (janela) atual do evento do botão
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        // Carregar a tela de Login
        try {
            // Assume que o FXML da tela de Login se chama LoginGUI.fxml
            // O caminho deve ser ajustado se o FXML estiver em outra pasta (ex: /resources/gui/LoginGUI.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginGUI.fxml"));
            Parent root = loader.load();

            // Substituir a cena atual pela cena de Login
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login do Sistema"); // Título da nova janela
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERRO: Falha ao carregar a tela de Login. Verifique o caminho do FXML.");
        }
    }
}