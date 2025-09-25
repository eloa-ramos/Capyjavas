package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import modelo.PDIDashItem;
import java.util.List;

public class DashboardGUI extends Application {

    private final DashboardGUIController controller = new DashboardGUIController();
    private final VBox pdiList = new VBox(5); // Layout para a lista de itens do PDI

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f7f7f7;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Dashboard RH");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button sairButton = new Button("Sair");
        sairButton.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;");

        sairButton.setOnAction(e -> stage.close());

        header.getChildren().addAll(title, spacer, sairButton);

        TextField searchField = new TextField();
        searchField.setPromptText("Buscar Colaborador...");
        searchField.setPrefWidth(300);

        Button cadastrarButton = new Button("Cadastrar");
        cadastrarButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");

        HBox searchArea = new HBox(10, cadastrarButton, searchField);
        searchArea.setAlignment(Pos.CENTER_LEFT);

        VBox dashboardContent = createDashboardContent();

        root.getChildren().addAll(header, searchArea, dashboardContent);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Dashboard de PDIs");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createDashboardContent() {
        VBox content = new VBox(10);
        content.getChildren().add(createHeaderRow());

        carregarEExibirPDIs();
        content.getChildren().add(pdiList);

        return content;
    }

    private HBox createHeaderRow() {
        HBox row = new HBox(10);
        row.setPadding(new Insets(10, 10, 10, 10));
        row.setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold;");

        Label colab = new Label("Colaborador");
        Label obj = new Label("Objetivo");
        Label prazo = new Label("Prazo");
        Label status = new Label("Status");

        row.getChildren().addAll(colab, obj, prazo, status);
        HBox.setHgrow(colab, Priority.ALWAYS);
        HBox.setHgrow(obj, Priority.ALWAYS);
        HBox.setHgrow(prazo, Priority.ALWAYS);

        colab.setPrefWidth(150);
        obj.setPrefWidth(200);
        prazo.setPrefWidth(100);
        status.setPrefWidth(100);

        return row;
    }

    private void carregarEExibirPDIs() {
        pdiList.getChildren().clear();
        List<PDIDashItem> dados = controller.carregarDadosPDI();

        for (PDIDashItem item : dados) {
            pdiList.getChildren().add(createPDIDataRow(item));
        }
    }

    private HBox createPDIDataRow(PDIDashItem item) {
        HBox row = new HBox(10);
        row.setPadding(new Insets(10, 10, 10, 10));
        row.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");

        Label colab = new Label(item.getColaborador());
        Label obj = new Label(item.getObjetivo());
        Label prazo = new Label(item.getPrazo());

        Label status = new Label(item.getStatus());
        String statusColor = getStatusColor(item.getStatus());
        status.setStyle("-fx-background-color: " + statusColor + "; -fx-padding: 3 8; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5; -fx-background-radius: 5;");

        row.getChildren().addAll(colab, obj, prazo, status);
        colab.setPrefWidth(150);
        obj.setPrefWidth(200);
        prazo.setPrefWidth(100);
        status.setPrefWidth(100);

        return row;
    }
    private String getStatusColor(String status) {
        return switch (status) {
            case "Concluído" -> "#4caf50";
            case "Atrasado" -> "#f44336";
            default -> "#ffc107";
        };
    }
    public static void main(String[] args) {
        launch(args);
    }
}