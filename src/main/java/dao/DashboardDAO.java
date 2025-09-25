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

    public DashboardDAO() {
        // O construtor está vazio para evitar crash no startup.
    }

    public List<PDIDashItem> buscarPDIs() {
        List<PDIDashItem> listaPDIs = new ArrayList<>();

        String sql = "SELECT u.id_usuario, u.nome, s.nome_skill AS objetivo, DATE_FORMAT(p.data_fim, '%d/%m/%Y') AS prazo, " +
                "CASE " +
                "  WHEN m.percentual_atingido >= 100 THEN 'Concluído' " +
                "  WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado' " +
                "  ELSE 'Em Andamento' END AS status " +
                "FROM Usuarios u " +
                "JOIN PDI p ON u.id_usuario = p.id_colaborador " +
                "LEFT JOIN Metas m ON p.id_pdi = m.id_pdi " +
                "LEFT JOIN Skills s ON m.id_skill = s.id_skill " +
                "WHERE u.tipo_acesso = 'Colaborador' " +
                "GROUP BY u.id_usuario, u.nome, s.nome_skill, p.data_fim, m.percentual_atingido " +
                "ORDER BY u.nome";
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PDIDashItem item = new PDIDashItem(
                        rs.getInt("id_usuario"),
                        rs.getString("nome"),
                        rs.getString("objetivo"),
                        rs.getString("prazo"),
                        rs.getString("status")
                );
                listaPDIs.add(item);
            }
        } catch (SQLException e) {
            // Se falhar aqui, o erro é do DB (credenciais, driver, etc.)
            throw new RuntimeException("Erro ao buscar PDIs. Verifique sua ConnectionFactory.", e);
        }
        return listaPDIs;
    }
}