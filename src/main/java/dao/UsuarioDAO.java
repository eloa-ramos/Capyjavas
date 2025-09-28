package dao;

import factory.ConnectionFactory;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, cpf, data_nascimento, cargo, experiencia, observacoes, tipo_acesso, email, senha) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setDate(3, java.sql.Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(4, usuario.getCargo());
            stmt.setString(5, usuario.getExperiencia());
            stmt.setString(6, usuario.getObservacoes());
            stmt.setString(7, usuario.getTipoAcesso());
            stmt.setString(8, usuario.getEmail());
            stmt.setString(9, usuario.getSenha());

            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoAcesso(rs.getString("tipo_acesso"));
                    // Adicione outros campos se precisar

                    return usuario; // Retorna o usuário encontrado
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage(), e);
        }

        return null; // Retorna null se nenhum usuário for encontrado
    }
}
