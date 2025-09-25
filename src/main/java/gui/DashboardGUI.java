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
    private final VBox pdiList = new VBox(5);

    @Override
    public void start(Stage stage) {
        // Layout Principal: VBox
        VBox root = new VBox(25); // Aumenta o espaçamento
        root.setPadding(new Insets(30)); // Aumenta o padding
        root.setStyle("-fx-background-color: #f7f7f7;");

        // --- Header Principal (Título e Sair) ---
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Dashboard RH");
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30)); // Fonte maior e mais bold
        title.setStyle("-fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button sairButton = new Button("Sair");
        sairButton.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-padding: 8 15; -fx-font-weight: bold; -fx-cursor: hand;");
        sairButton.setOnAction(e -> stage.close());

        header.getChildren().addAll(title, spacer, sairButton);

        // --- Área de Ações (Busca e Cadastrar) ---
        HBox actionArea = new HBox(20); // Mais espaço entre os elementos
        actionArea.setAlignment(Pos.CENTER_LEFT);

        Button cadastrarButton = new Button("Novo Cadastro");
        cadastrarButton.setStyle("-fx-background-color: #2C2C2C; -fx-text-fill: white; -fx-padding: 8 15; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        // FUNÇÃO DE NAVEGAÇÃO CORRETA: Abre UsuarioGUI
        cadastrarButton.setOnAction(e -> {
            try {
                UsuarioGUI cadastroApp = new UsuarioGUI();
                cadastroApp.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Erro ao abrir tela de Cadastro: " + ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Buscar Colaborador por Nome, Objetivo ou CPF...");
        searchField.setPrefHeight(35); // Altura padronizada
        searchField.setStyle("-fx-padding: 8; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        HBox.setHgrow(searchField, Priority.ALWAYS); // O campo de busca ocupa o espaço restante

        actionArea.getChildren().addAll(cadastrarButton, searchField);

        // Renderiza a Lista (Cabeçalho + Itens)
        VBox dashboardContent = createDashboardContent();
        dashboardContent.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);"); // Adiciona sombra e cantos arredondados

        root.getChildren().addAll(header, actionArea, dashboardContent);

        Scene scene = new Scene(root, 1000, 700); // Aumenta o tamanho da janela
        stage.setTitle("Dashboard de PDIs");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createDashboardContent() {
        VBox content = new VBox(0); // Remove o spacing VBox para parecer uma tabela
        content.getChildren().add(createHeaderRow());

        carregarEExibirPDIs();
        content.getChildren().add(pdiList);

        return content;
    }

    private HBox createHeaderRow() {
        HBox row = new HBox(10);
        row.setPadding(new Insets(12, 20, 12, 20)); // Padding maior
        row.setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold; -fx-border-radius: 8 8 0 0;"); // Cantos arredondados apenas em cima

        Label colab = new Label("COLABORADOR");
        Label obj = new Label("OBJETIVO");
        Label prazo = new Label("PRAZO");
        Label status = new Label("STATUS");

        colab.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        obj.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        prazo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        status.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        row.getChildren().addAll(colab, obj, prazo, status);

        // Define as larguras relativas
        colab.setPrefWidth(200);
        obj.setPrefWidth(350);
        prazo.setPrefWidth(120);
        status.setPrefWidth(150);
        HBox.setHgrow(obj, Priority.ALWAYS); // Objetivo pode crescer mais

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
        row.setPadding(new Insets(12, 20, 12, 20));
        row.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;"); // Linha divisória mais sutil

        // Dados
        Label colab = new Label(item.getColaborador());
        Label obj = new Label(item.getObjetivo());
        Label prazo = new Label(item.getPrazo());

        // Status com estilo de Badge
        Label status = new Label(item.getStatus());
        String statusColor = getStatusColor(item.getStatus());
        status.setStyle("-fx-background-color: " + statusColor + "; -fx-padding: 4 10; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 12; -fx-background-radius: 12;");

        // Configurações de fonte para dados
        colab.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        obj.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        prazo.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        row.getChildren().addAll(colab, obj, prazo, status);

        // Define as larguras
        colab.setPrefWidth(200);
        obj.setPrefWidth(350);
        prazo.setPrefWidth(120);
        status.setPrefWidth(150);

        return row;
    }

    private String getStatusColor(String status) {
        return switch (status) {
            case "Concluído" -> "#4caf50"; // Verde
            case "Atrasado" -> "#f44336"; // Vermelho
            case "Em Andamento" -> "#ff9800"; // Laranja (melhor que amarelo)
            default -> "#9e9e9e"; // Cinza
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}