import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // 1. Define a Stage estaticamente para que outras classes possam acessá-la (opcional, mas útil)
        StageManager.setStage(stage);

        try {
            // 2. Carrega o FXML da tela de Login
            // Assumimos que o LoginGUI.fxml está no caminho "/gui/LoginGUI.fxml" dentro da sua pasta 'resources'
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginGUI.fxml"));
            Parent root = loader.load();

            // 3. Configura e exibe a Stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("YouTan - Login de Acesso");
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar o LoginGUI.fxml. Verifique o caminho e se o arquivo existe.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Classe auxiliar para controlar o Stage (Mantida para controle de Stage)
    public static class StageManager {
        private static Stage stage;

        public static void setStage(Stage s) {
            stage = s;
        }

        public static Stage getStage() {
            return stage;
        }
    }
}