package dao;

import factory.ConnectionFactory;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, cpf, data_nascimento, cargo, experiencia, observacoes) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setDate(3, java.sql.Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(4, usuario.getCargo());
            stmt.setString(5, usuario.getExperiencia());
            stmt.setString(6, usuario.getObservacoes());

            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
