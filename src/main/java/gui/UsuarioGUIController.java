package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import modelo.Usuario;

public class UsuarioGUIController {

    // Campos do formulário conectados pelo fx:id do FXML
    @FXML
    private TextField nomeUsuario;
    @FXML
    private TextField cpfUsuario;
    @FXML
    private TextField cargoUsuario;

    @FXML
    private Button cadastrarUsuarioBtn;

    @FXML
    private Button sairBtn;

    /**
     * Quando o botão "Cadastrar" for clicado,
     * cria um objeto Usuario e envia para o DAO salvar.
     */
    @FXML
    void clickCadastrar(ActionEvent event) {
        String nome = nomeUsuario.getText().trim();
        String cpf = cpfUsuario.getText().trim();
        String cargo = cargoUsuario.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || cargo.isEmpty()) {
            System.out.println("⚠️ Preencha todos os campos!");
        } else {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = new Usuario(nome, cpf, cargo);
            usuarioDAO.adiciona(usuario);

            System.out.println("✅ Usuário cadastrado: " + usuario.getNome());

            // Limpa os campos
            nomeUsuario.clear();
            cpfUsuario.clear();
            cargoUsuario.clear();
        }
    }

    /**
     * Fecha a aplicação de forma segura ao clicar em "Sair".
     */
    @FXML
    void clickSair(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
