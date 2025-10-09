package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.Usuario;

public class CadastroPdiWindow {

    private final Usuario usuarioLogado;

    public CadastroPdiWindow(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/CadastroPdiGUI.fxml"));
            Parent root = loader.load();

            CadastroPdiController controller = loader.getController();
            controller.setUsuario(usuarioLogado);

            Stage stage = new Stage();
            stage.setTitle("Cadastrar PDI");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
