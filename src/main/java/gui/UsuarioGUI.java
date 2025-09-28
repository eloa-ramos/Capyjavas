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
import javafx.scene.control.PasswordField; // Importação adicionada
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.function.UnaryOperator;

public class UsuarioGUI extends Application {

    private final DashboardGUI dashboard = new DashboardGUI();

    // --- CAMPOS ADICIONADOS ---
    private TextField txtEmail;
    private PasswordField txtSenha; // Usar PasswordField para segurança
    // -------------------------

    private TextField txtNome;
    private TextField txtCpf;
    private TextField txtCargo;
    private TextField txtArea;
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
        root.setStyle("-fx-background-color: #f7f7f7;");

        // --- Título ---
        Label lblTitulo = new Label("Cadastro de Usuários");
        lblTitulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox topBox = new VBox(lblTitulo);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPadding(new Insets(0, 0, 40, 0));
        root.setTop(topBox);

        // --- Formulário ---
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(20);
        grid.setAlignment(Pos.TOP_CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        String inputStyle = "-fx-font-size: 14px; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 8 10; -fx-background-color: white; -fx-prompt-text-fill: #999;";

        // LINHA 0: Nome Completo (ocupa 2 colunas)
        txtNome = new TextField();
        txtNome.setPromptText("Ex: João Silva");
        txtNome.setStyle(inputStyle);
        VBox vNome = createLabeledControl("Nome completo", txtNome);
        grid.add(vNome, 0, 0, 2, 1);

        // LINHA 1: CPF e Data de Nascimento
        txtCpf = new TextField();
        txtCpf.setPromptText("Digite apenas números");
        UnaryOperator<TextFormatter.Change> cpfFilter = change -> {
            String newText = change.getControlNewText();
            if (!change.getText().matches("[0-9]*")) return null;
            if (newText.length() > 11) return null;
            return change;
        };
        txtCpf.setTextFormatter(new TextFormatter<>(cpfFilter));
        txtCpf.setStyle(inputStyle);
        VBox vCpf = createLabeledControl("CPF", txtCpf);
        grid.add(vCpf, 0, 1);

        datePicker = new DatePicker();
        datePicker.setPromptText("30/10/2000");
        datePicker.setStyle(inputStyle + "-fx-padding: 6 10;");
        VBox vData = createLabeledControl("Data de Nascimento", datePicker);
        grid.add(vData, 1, 1);

        // --- NOVAS LINHAS PARA EMAIL E SENHA ---
        // LINHA 2: E-mail
        txtEmail = new TextField();
        txtEmail.setPromptText("nome.sobrenome@empresa.com");
        txtEmail.setStyle(inputStyle);
        VBox vEmail = createLabeledControl("E-mail", txtEmail);
        grid.add(vEmail, 0, 2);

        // LINHA 2: Senha
        txtSenha = new PasswordField();
        txtSenha.setPromptText("Defina uma senha");
        txtSenha.setStyle(inputStyle);
        VBox vSenha = createLabeledControl("Senha", txtSenha);
        grid.add(vSenha, 1, 2);
        // ---------------------------------------

        // LINHA 3: Cargo e Área (Anteriormente Linha 2)
        txtCargo = new TextField();
        txtCargo.setPromptText("Escolha o Cargo");
        txtCargo.setStyle(inputStyle);
        VBox vCargo = createLabeledControl("Cargo", txtCargo);
        grid.add(vCargo, 0, 3);

        txtArea = new TextField();
        txtArea.setPromptText("Defina a Área");
        txtArea.setStyle(inputStyle);
        VBox vArea = createLabeledControl("Área", txtArea);
        grid.add(vArea, 1, 3);

        // LINHA 4: Tipo de Acesso (Anteriormente Linha 3)
        comboTipoAcesso = new ComboBox<>();
        comboTipoAcesso.getItems().addAll("RH", "Gestor Geral", "Gestor de Area", "Colaborador");
        comboTipoAcesso.setPromptText("Tipo de Acesso");
        comboTipoAcesso.setStyle(inputStyle);
        comboTipoAcesso.setMaxWidth(Double.MAX_VALUE);
        VBox vTipo = createLabeledControl("Tipo de Acesso", comboTipoAcesso);
        grid.add(vTipo, 0, 4, 2, 1);

        // LINHA 5: Experiência (Anteriormente Linha 4)
        txtExperiencia = new TextArea();
        txtExperiencia.setPromptText("Experiência");
        txtExperiencia.setWrapText(true);
        txtExperiencia.setPrefRowCount(3);
        txtExperiencia.setStyle(inputStyle);
        VBox vExp = createLabeledControl("Experiência", txtExperiencia);
        grid.add(vExp, 0, 5, 2, 1);

        // LINHA 6: Observações (Anteriormente Linha 5)
        txtObservacoes = new TextArea();
        txtObservacoes.setPromptText("Observações");
        txtObservacoes.setWrapText(true);
        txtObservacoes.setPrefRowCount(3);
        txtObservacoes.setStyle(inputStyle);
        VBox vObs = createLabeledControl("Observações", txtObservacoes);
        grid.add(vObs, 0, 6, 2, 1);

        // Botões
        btnSalvar = new Button("Cadastrar");
        btnSalvar.setOnAction(e -> salvarUsuario());
        btnSalvar.setStyle(
                "-fx-background-color: #2C2C2C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;"
        );

        Button btnVoltar = new Button("Voltar");
        btnVoltar.setOnAction(e -> {
            try {
                // Supondo que DashboardGUI pode ser carregada
                // DashboardGUI dashboard = new DashboardGUI(); // Já existe como campo
                dashboard.start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        btnVoltar.setStyle(
                "-fx-background-color: #E0E0E0; -fx-text-fill: #333; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;"
        );

        HBox btnBox = new HBox(15, btnVoltar, btnSalvar);
        btnBox.setAlignment(Pos.BOTTOM_RIGHT);
        btnBox.setPadding(new Insets(20, 0, 0, 0));

        VBox centerBox = new VBox(grid, btnBox);
        centerBox.setSpacing(6);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setMaxWidth(800);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1000, 800);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createLabeledControl(String labelText, Control control) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
        control.setMaxWidth(Double.MAX_VALUE);
        VBox box = new VBox(4, label, control);
        box.setFillWidth(true);
        VBox.setVgrow(control, Priority.ALWAYS);
        return box;
    }

    private void salvarUsuario() {
        // Lógica de salvamento e validação
        if (txtNome.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informe o nome.");
            return;
        }
        if (txtCpf.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informe o CPF.");
            return;
        }
        // --- VALIDAÇÃO ADICIONADA ---
        if (txtEmail.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informe o E-mail.");
            return;
        }
        if (txtSenha.getText().isEmpty()) { // Não use trim() para senhas
            showAlert(Alert.AlertType.WARNING, "Informe a Senha.");
            return;
        }
        // -----------------------------
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

            // --- DADOS DE ACESSO INCLUÍDOS ---
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setSenha(txtSenha.getText());
            // ----------------------------------

            // Nota: O campo txtArea não está no modelo Usuario e não está sendo salvo.
            usuario.setExperiencia(txtExperiencia.getText().trim());
            usuario.setObservacoes(txtObservacoes.getText().trim());
            usuario.setTipoAcesso(comboTipoAcesso.getValue());

            UsuarioDAO dao = new UsuarioDAO();
            dao.adiciona(usuario);

            showAlert(Alert.AlertType.INFORMATION, "✅ Usuário cadastrado com sucesso.");
            clearForm();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "❌ Erro ao salvar: " + ex.getMessage());
            ex.printStackTrace();
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
        txtArea.clear();
        txtExperiencia.clear();
        txtObservacoes.clear();
        comboTipoAcesso.setValue(null);

        // --- LIMPEZA DOS NOVOS CAMPOS ---
        txtEmail.clear();
        txtSenha.clear();
        // --------------------------------
    }

    public static void main(String[] args) {
        launch(args);
    }
}