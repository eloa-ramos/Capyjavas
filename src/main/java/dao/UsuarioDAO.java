package dao;

import factory.ConnectionFactory;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, experiencia, observacoes, tipo_acesso, id_area) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getNome());
            stmt.setString(4, usuario.getCpf());

            if (usuario.getDataNascimento() != null) {
                stmt.setDate(5, Date.valueOf(usuario.getDataNascimento()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }

            stmt.setString(6, usuario.getCargo());
            stmt.setString(7, usuario.getExperiencia());
            stmt.setString(8, usuario.getObservacoes());
            stmt.setString(9, usuario.getTipoAcesso());

            if (usuario.getIdArea() != null && usuario.getIdArea() > 0) {
                stmt.setInt(10, usuario.getIdArea());
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }

            stmt.execute();

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar usuário: " + e.getMessage());
            throw e;
        }
    }

    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT id_usuario, nome, email, tipo_acesso, id_area FROM usuarios WHERE email = ? AND senha = ?";

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

                    int idArea = rs.getInt("id_area");
                    if (!rs.wasNull()) {
                        usuario.setIdArea(idArea);
                    } else {
                        usuario.setIdArea(null);
                    }

                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Usuario> buscarPorNome(String nome) {
        String sql = "SELECT id_usuario, nome, email, tipo_acesso, id_area FROM usuarios WHERE nome LIKE ?";
        List<Usuario> usuarios = new ArrayList<>();

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoAcesso(rs.getString("tipo_acesso"));

                    int idArea = rs.getInt("id_area");
                    if (!rs.wasNull()) {
                        usuario.setIdArea(idArea);
                    } else {
                        usuario.setIdArea(null);
                    }

                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários por nome: " + e.getMessage(), e);
        }
        return usuarios;
    }
}