




import javafx.application.Application;
import gui.DashboardGUI;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DashboardGUI dashboardApp = new DashboardGUI();
        dashboardApp.start(stage);
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
