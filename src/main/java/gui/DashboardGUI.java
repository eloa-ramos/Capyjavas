package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.Usuario;

public class DashboardGUI extends Application {

    private Usuario usuarioLogado;

    public DashboardGUI() {
        // Construtor padrão para JavaFX
    }

    public DashboardGUI(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/DashboardGUI.fxml"));
        Parent root = loader.load();

        DashboardGUIController controller = loader.getController();
        controller.setUsuario(usuarioLogado);

        Scene scene = new Scene(root, 1200, 800);
        //css global e do dashboard
        scene.getStylesheets().add(getClass().getResource("/gui/css/style.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/css/Dashboard.css").toExternalForm());

        stage.setTitle("Dashboard - " + (usuarioLogado != null ? usuarioLogado.getTipoAcesso() : "Visitante"));
        stage.setMaximized(true); // Tela inicial maximizada
        stage.setScene(scene);
        stage.show();
    }
}
