




import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Iniciando aplicação CRUD...");

        // Carrega o arquivo FXML da pasta resources/gui
        Parent root = FXMLLoader.load(getClass().getResource("/gui/UsuarioGUI.fxml"));

        stage.setTitle("Cadastro de Clientes");
        stage.setScene(new Scene(root));
        stage.show();

        StageManager.setStage(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Classe auxiliar para controlar o Stage
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
