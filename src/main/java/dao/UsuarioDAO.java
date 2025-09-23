package dao;

import factory.ConnectionFactory;
import modelo.Usuario;

import java.sql.*;
import java.sql.PreparedStatement;

public class UsuarioDAO {
    private Connection connection;


    public UsuarioDAO() {
        this.connection = new ConnectionFactory().getConnection();

    }

    public void adiciona(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, cpf, cargo, experiencia, observacoes, tipo_acesso, data_nascimento) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getCargo());
            stmt.setString(4, usuario.getExperiencia());
            stmt.setString(5, usuario.getObservacoes());
            stmt.setString(6, usuario.getTipoAcesso());
            stmt.setDate(7, java.sql.Date.valueOf(usuario.getDataNascimento())); // LocalDate -> SQL Date

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
