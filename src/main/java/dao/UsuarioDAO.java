package dao;

import factory.ConnectionFactory;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList; // Importado
import java.util.List;     // Importado

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Usuario usuario) throws SQLException {
        // INCLUIDO id_area no SQL INSERT
        String sql = "INSERT INTO Usuarios (email, senha, nome, cpf, data_nascimento, cargo, experiencia, observacoes, tipo_acesso, id_area) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Mapeamento dos parâmetros
            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getNome());
            stmt.setString(4, usuario.getCpf());

            // Tratamento para data nula (se dataNascimento for opcional)
            if (usuario.getDataNascimento() != null) {
                stmt.setDate(5, Date.valueOf(usuario.getDataNascimento()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }

            stmt.setString(6, usuario.getCargo());
            stmt.setString(7, usuario.getExperiencia());
            stmt.setString(8, usuario.getObservacoes());
            stmt.setString(9, usuario.getTipoAcesso());

            // NOVO PARAMETRO: id_area
            stmt.setInt(10, usuario.getIdArea());

            stmt.execute();

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar usuário: " + e.getMessage());
            throw e; // Lança a exceção para ser tratada no Controller
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

                    return usuario;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * NOVO: Método para buscar usuários por nome (para autocomplete).
     */
    public List<Usuario> buscarPorNome(String nome) {
        // Busca usuários cujo nome COMEÇA COM o texto digitado
        String sql = "SELECT id_usuario, nome, email, tipo_acesso FROM usuarios WHERE nome LIKE ?";
        List<Usuario> usuarios = new ArrayList<>();

        // Usamos a conexão da instância (this.connection)
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, nome + "%"); // Adiciona o curinga '%'

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setTipoAcesso(rs.getString("tipo_acesso"));
                    usuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários por nome: " + e.getMessage(), e);
        }
        return usuarios;
    }
}