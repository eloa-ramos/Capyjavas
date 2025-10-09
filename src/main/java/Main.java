import gui.StageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import gui.LoginGUI;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        StageManager.setStage(stage);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LoginGUI.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/css/style.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/gui/css/Login.css").toExternalForm());

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("YouTan - Login");
            stage.show();

        } catch (Exception e) {
            System.err.println("ERRO: Não foi possível iniciar a tela de login.");
            e.printStackTrace();
        }
    }
}
