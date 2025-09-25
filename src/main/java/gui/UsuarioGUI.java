package gui;
import dao.UsuarioDAO;
import modelo.Usuario;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.function.UnaryOperator;

public class UsuarioGUI extends Application {


    private TextField txtNome;
    private TextField txtCpf;
    private TextField txtCargo;
    private TextArea txtExperiencia;
    private TextArea txtObservacoes;
    private DatePicker datePicker;
    private ComboBox<String> comboTipoAcesso;
    private Button btnSalvar;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Cadastro de Usuários");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(36));

        // --- Título ---
        Label lblTitulo = new Label("Cadastro de Usuários");
        lblTitulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox topBox = new VBox(lblTitulo);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 20, 0));
        root.setTop(topBox);

        // --- Formulário ---
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(12);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);


        txtNome = new TextField();
        txtNome.setPromptText("Ex: João Silva");
        txtNome.setStyle("-fx-font-size: 13px; -fx-border-color: #aaa; -fx-border-radius: 4; -fx-padding: 4;");
        VBox vNome = createLabeledControl("Nome completo", txtNome);
        grid.add(vNome, 0, 0, 2, 1);

        // CPF (apenas números)
        txtCpf = new TextField();
        txtCpf.setPromptText("Digite apenas números");
        // filtro para aceitar apenas dígitos e limitar a 11 caracteres.
        UnaryOperator<TextFormatter.Change> cpfFilter = change -> {
            String newText = change.getControlNewText();
            if (!change.getText().matches("[0-9]*")) return null;
            if (newText.length() > 11) return null; // limite opcional
            return change;
        };
        txtCpf.setTextFormatter(new TextFormatter<>(cpfFilter));
        VBox vCpf = createLabeledControl("CPF", txtCpf);
        grid.add(vCpf, 0, 1);

        // Data de Nascimento
        datePicker = new DatePicker();
        datePicker.setPromptText("dd/MM/yyyy");
        VBox vData = createLabeledControl("Data de Nascimento", datePicker);
        grid.add(vData, 1, 1);

        // Cargo
        txtCargo = new TextField();
        txtCargo.setPromptText("Escolha o Cargo");
        VBox vCargo = createLabeledControl("Cargo", txtCargo);
        grid.add(vCargo, 0, 2, 2, 1);

        // Experiência
        txtExperiencia = new TextArea();
        txtExperiencia.setPromptText("Experiência");
        txtExperiencia.setWrapText(true);
        VBox vExp = createLabeledControl("Experiência", txtExperiencia);
        grid.add(vExp, 0, 4, 2, 1);

        // Observações
        txtObservacoes = new TextArea();
        txtObservacoes.setPromptText("Observações");
        txtObservacoes.setWrapText(true);
        VBox vObs = createLabeledControl("Observações", txtObservacoes);
        grid.add(vObs, 0, 5, 2, 1);

        // Tipo de Acesso
        comboTipoAcesso = new ComboBox<>();
        comboTipoAcesso.getItems().addAll("RH", "Gestor Geral", "Gestor de Area", "Colaborador");
        comboTipoAcesso.setPromptText("Tipo de Acesso");
        VBox vTipo = createLabeledControl("Tipo de Acesso", comboTipoAcesso);
        grid.add(vTipo, 0, 6, 2, 1);

        // Botão Salvar
        btnSalvar = new Button("Cadastrar");
        btnSalvar.setOnAction(e -> salvarUsuario());
        btnSalvar.setStyle(
                "-fx-background-color: #2C2C2C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 5;"
        );

        HBox btnBox = new HBox(btnSalvar);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(18, 0, 0, 0));

        VBox centerBox = new VBox(grid, btnBox);
        centerBox.setSpacing(6);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setMaxWidth(920);
        root.setCenter(centerBox);

        centerBox.setPadding(new Insets(6)); // Adiciona um padding interno ao contêiner central

        //cena
        Scene scene = new Scene(root, 1000, 760);

        stage.setScene(scene);
        stage.show();
    }

    private VBox createLabeledControl(String labelText, Control control) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        control.setMaxWidth(Double.MAX_VALUE); // permite o controle ocupar a largura disponível
        VBox box = new VBox(6, label, control);
        box.setFillWidth(true);
        return box;
    }
    private void salvarUsuario() {
        // validações
        if (txtNome.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informe o nome.");
            return;
        }
        if (txtCpf.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informe o CPF.");
            return;
        }
        if (comboTipoAcesso.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Escolha o tipo de acesso.");
            return;
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setNome(txtNome.getText().trim());
            usuario.setCpf(txtCpf.getText().trim());
            usuario.setDataNascimento(datePicker.getValue());
            usuario.setCargo(txtCargo.getText().trim());
            usuario.setExperiencia(txtExperiencia.getText().trim());
            usuario.setObservacoes(txtObservacoes.getText().trim());
            usuario.setTipoAcesso(comboTipoAcesso.getValue());
            UsuarioDAO dao = new UsuarioDAO();
            dao.adiciona(usuario);

            showAlert(Alert.AlertType.INFORMATION, "Usuário cadastrado com sucesso.");
            clearForm();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Erro ao salvar: " + ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert a = new Alert(type, message, ButtonType.OK);
        a.showAndWait();
    }

    private void clearForm() {
        txtNome.clear();
        txtCpf.clear();
        datePicker.setValue(null);
        txtCargo.clear();
        txtExperiencia.clear();
        txtObservacoes.clear();
        comboTipoAcesso.setValue(null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

