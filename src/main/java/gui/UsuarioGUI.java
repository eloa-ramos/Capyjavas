package gui;

import dao.UsuarioDAO;
import modelo.Usuario;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UsuarioGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cadastro de Usuário");

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        // Campos
        Label lblNome = new Label("Nome:");
        TextField txtNome = new TextField();
        grid.add(lblNome, 0, 0);
        grid.add(txtNome, 1, 0);

        Label lblCpf = new Label("CPF:");
        TextField txtCpf = new TextField();
        grid.add(lblCpf, 0, 1);
        grid.add(txtCpf, 1, 1);

        Label lblCargo = new Label("Cargo:");
        TextField txtCargo = new TextField();
        grid.add(lblCargo, 0, 2);
        grid.add(txtCargo, 1, 2);

        Label lblExperiencia = new Label("Experiência:");
        TextField txtExperiencia = new TextField();
        grid.add(lblExperiencia, 0, 3);
        grid.add(txtExperiencia, 1, 3);

        Label lblObservacoes = new Label("Observações:");
        TextField txtObservacoes = new TextField();
        grid.add(lblObservacoes, 0, 4);
        grid.add(txtObservacoes, 1, 4);

        Label lblTipoAcesso = new Label("Tipo de Acesso:");
        TextField txtTipoAcesso = new TextField();
        grid.add(lblTipoAcesso, 0, 5);
        grid.add(txtTipoAcesso, 1, 5);

        Label lblDataNascimento = new Label("Data de Nascimento:");
        DatePicker datePicker = new DatePicker();
        grid.add(lblDataNascimento, 0, 6);
        grid.add(datePicker, 1, 6);

        // Botão
        Button btnSalvar = new Button("Salvar");
        grid.add(btnSalvar, 1, 7);

        // Ação do botão
        btnSalvar.setOnAction(e -> {
            try {
                Usuario usuario = new Usuario();
                usuario.setNome(txtNome.getText());
                usuario.setCpf(txtCpf.getText());
                usuario.setCargo(txtCargo.getText());
                usuario.setExperiencia(txtExperiencia.getText());
                usuario.setObservacoes(txtObservacoes.getText());
                usuario.setTipoAcesso(txtTipoAcesso.getText());
                usuario.setDataNascimento(datePicker.getValue());

                UsuarioDAO dao = new UsuarioDAO();
                dao.adiciona(usuario);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Usuário cadastrado com sucesso!");
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erro: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        // Cena
        Scene scene = new Scene(grid, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
