package dao;

import factory.ConnectionFactory;
import modelo.PDIDashItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public DashboardDAO() {}


    public List<PDIDashItem> buscarPDIsRH() {
        String sql = """
                SELECT u.id_usuario, u.nome AS colaborador, s.nome_skill AS objetivo,
                       DATE_FORMAT(p.data_fim, '%d/%m/%Y') AS prazo,
                       CASE
                           WHEN m.percentual_atingido >= 100 THEN 'Concluído'
                           WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado'
                           ELSE 'Em Andamento'
                       END AS status,
                       g.nome AS area
                FROM Usuarios u
                JOIN PDI p ON u.id_usuario = p.id_colaborador
                LEFT JOIN Metas m ON p.id_pdi = m.id_pdi
                LEFT JOIN Skills s ON m.id_skill = s.id_skill
                LEFT JOIN Usuarios g ON u.id_gestor_de_area = g.id_usuario
                WHERE u.tipo_acesso = 'Colaborador'
                GROUP BY u.id_usuario, u.nome, s.nome_skill, p.data_fim, m.percentual_atingido, g.nome
                ORDER BY g.nome, u.nome;
                """;

        return buscarPDIsGenerico(sql, null);
    }

    // =========================
    // Gestor Geral (Chamada de compatibilidade)
    // O Gestor Geral ainda usa o método que lista todos.
    // =========================
    public List<PDIDashItem> buscarPDIsGestorGeral() {
        return buscarPDIsRH();
    }

    // =========================
    // Gestor de Área (Mantido)
    // =========================
    public List<PDIDashItem> buscarPDIsGestorArea(int idGestor) {
        String sql = """
                SELECT u.id_usuario, u.nome AS colaborador, s.nome_skill AS objetivo,
                       DATE_FORMAT(p.data_fim, '%d/%m/%Y') AS prazo,
                       CASE
                           WHEN m.percentual_atingido >= 100 THEN 'Concluído'
                           WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado'
                           ELSE 'Em Andamento'
                       END AS status,
                       g.nome AS area
                FROM Usuarios u
                JOIN PDI p ON u.id_usuario = p.id_colaborador
                LEFT JOIN Metas m ON p.id_pdi = m.id_pdi
                LEFT JOIN Skills s ON m.id_skill = s.id_skill
                LEFT JOIN Usuarios g ON u.id_gestor_de_area = g.id_usuario
                WHERE u.tipo_acesso = 'Colaborador'
                  AND u.id_gestor_de_area = ?
                GROUP BY u.id_usuario, u.nome, s.nome_skill, p.data_fim, m.percentual_atingido, g.nome
                ORDER BY u.nome;
                """;

        return buscarPDIsGenerico(sql, idGestor);
    }

    public List<PDIDashItem> listarPDIColaborador(int idColaborador) {
        String sql = """
                SELECT u.id_usuario, u.nome AS colaborador, s.nome_skill AS objetivo,
                       DATE_FORMAT(p.data_fim, '%d/%m/%Y') AS prazo,
                       CASE
                           WHEN m.percentual_atingido >= 100 THEN 'Concluído'
                           WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado'
                           ELSE 'Em Andamento'
                       END AS status,
                       g.nome AS area
                FROM Usuarios u
                JOIN PDI p ON u.id_usuario = p.id_colaborador
                LEFT JOIN Metas m ON p.id_pdi = m.id_pdi
                LEFT JOIN Skills s ON m.id_skill = s.id_skill
                LEFT JOIN Usuarios g ON u.id_gestor_de_area = g.id_usuario
                WHERE u.id_usuario = ? 
                GROUP BY u.id_usuario, u.nome, s.nome_skill, p.data_fim, m.percentual_atingido, g.nome
                ORDER BY p.data_fim DESC;
                """;

        return buscarPDIsGenerico(sql, idColaborador);
    }


    /**
     * Conta o número total de usuários com tipo_acesso = 'Colaborador'.
     */
    public int contarTotalColaboradores() {
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE tipo_acesso = 'Colaborador'";
        int total = 0;

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1); // O COUNT(*) sempre estará na primeira coluna (índice 1)
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar o total de colaboradores: " + e.getMessage());
            // Se houver erro, podemos retornar 0 ou relançar. Retornar 0 é mais seguro para a UI.
            return 0;
        }

        return total;
    }

    /**
     * Retorna a quantidade total de PDIs existentes na tabela PDI.
     */
    public int contarTotalPDIs() {
        String sql = "SELECT COUNT(*) FROM PDI";
        int total = 0;

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar o total de PDIs: " + e.getMessage());
            return 0;
        }

        return total;
    }

    /**
     * Retorna a contagem de PDIs por status real (Concluído/Atrasado/Em Andamento)
     * para todos os PDIs no sistema.
     */
    public java.util.Map<String, Integer> contarPDIsPorStatus() {
        // Esta query replica a lógica de status que você usa para os cards
        String sql = """
                SELECT 
                    CASE
                        WHEN m.percentual_atingido >= 100 THEN 'Concluído'
                        WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado'
                        ELSE 'Em Andamento'
                    END AS status_calculado,
                    COUNT(p.id_pdi) AS total
                FROM PDI p
                LEFT JOIN Metas m ON p.id_pdi = m.id_pdi
                GROUP BY status_calculado;
                """;

        java.util.Map<String, Integer> contagem = new java.util.HashMap<>();

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("status_calculado");
                int totalStatus = rs.getInt("total");
                contagem.put(status, totalStatus);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar PDIs por status: " + e.getMessage());
            return contagem;
        }

        return contagem;
    }

    // =========================
    // Método genérico de busca
    // =========================
    private List<PDIDashItem> buscarPDIsGenerico(String sql, Integer idParametro) {
        List<PDIDashItem> listaPDIs = new ArrayList<>();

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Mudança de nome da variável de 'idGestor' para 'idParametro' para ser mais genérico
            if (idParametro != null) stmt.setInt(1, idParametro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PDIDashItem item = new PDIDashItem(
                            rs.getInt("id_usuario"),
                            rs.getString("colaborador"),
                            rs.getString("objetivo"),
                            rs.getString("prazo"),
                            rs.getString("status"),
                            rs.getString("area")
                    );
                    listaPDIs.add(item);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar PDIs: " + e.getMessage(), e);
        }

        return listaPDIs;
    }
}