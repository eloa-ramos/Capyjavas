package gui;

import dao.UsuarioDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import modelo.Usuario;

import java.time.LocalDate;

public class UsuarioGUIController {

    @FXML private TextField nomeUsuario;
    @FXML private TextField cpfUsuario;
    @FXML private DatePicker dataNascimentoUsuario;
    @FXML private TextField cargoUsuario;
    @FXML private TextField experienciaUsuario;
    @FXML private TextField observacoesUsuario;

    @FXML private Button cadastrarUsuarioBtn;
    @FXML private Button sairBtn;

    @FXML
    private void clickCadastrar(ActionEvent event) {
        String nome = nomeUsuario.getText().trim();
        String cpf = cpfUsuario.getText().trim();
        LocalDate dataNascimento = dataNascimentoUsuario.getValue();
        String cargo = cargoUsuario.getText().trim();
        String experiencia = experienciaUsuario.getText().trim();
        String observacoes = observacoesUsuario.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || dataNascimento == null || cargo.isEmpty()) {
            System.out.println("⚠️ Preencha os campos obrigatórios!");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setDataNascimento(dataNascimento);
        usuario.setCargo(cargo);
        usuario.setExperiencia(experiencia);
        usuario.setObservacoes(observacoes);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.adiciona(usuario);

        System.out.println("✅ Usuário cadastrado: " + usuario.getNome());

        // Limpa os campos
        nomeUsuario.clear();
        cpfUsuario.clear();
        dataNascimentoUsuario.setValue(null);
        cargoUsuario.clear();
        experienciaUsuario.clear();
        observacoesUsuario.clear();
    }

    @FXML
    private void clickSair(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
