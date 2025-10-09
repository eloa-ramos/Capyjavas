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

    // =========================
    // RH
    // =========================
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
    // Gestor Geral
    // =========================
    public List<PDIDashItem> buscarPDIsGestorGeral() {
        // Mesma query do RH (somente leitura no controller)
        return buscarPDIsRH();
    }

    // =========================
    // Gestor de Área
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

    // =========================
    // Método genérico
    // =========================
    private List<PDIDashItem> buscarPDIsGenerico(String sql, Integer idGestor) {
        List<PDIDashItem> listaPDIs = new ArrayList<>();

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (idGestor != null) stmt.setInt(1, idGestor);

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
