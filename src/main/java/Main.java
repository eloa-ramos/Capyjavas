import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // 1. Define a Stage estaticamente para que outras classes possam acessá-la (opcional)
        StageManager.setStage(stage);

        try {
            // 2. MUDANÇA PRINCIPAL: Carrega o FXML da tela de Seleção de Perfil
            // Assumimos que o Root.fxml está no caminho "/gui/Root.fxml"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Root.fxml"));
            Parent root = loader.load();

            // 3. Configura e exibe a Stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("YouTan - Seleção de Acesso"); // Novo título
            stage.show();

        } catch (IOException e) {
            // Se o Root.fxml não for encontrado, você verá este erro.
            System.err.println("ERRO: Não foi possível carregar o Root.fxml. Verifique o caminho e se o arquivo existe.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Classe auxiliar para controlar o Stage (Mantida)
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