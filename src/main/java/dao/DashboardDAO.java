package dao;

import factory.ConnectionFactory;
import modelo.PDIDashItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DashboardDAO {

    public DashboardDAO() {}

    public List<PDIDashItem> buscarPDIsRH() {
        String sql = """
                SELECT DISTINCT p.id_pdi, u.id_usuario, u.nome AS colaborador, s.nome_skill AS objetivo,
                       DATE_FORMAT(p.data_fim, '%d/%m/%Y') AS prazo,
                       CASE
                           WHEN m.percentual_atingido >= 100 THEN 'Concluído'
                           WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado'
                           ELSE 'Em Andamento'
                       END AS status,
                       a.nome_area AS area
                FROM Usuarios u
                JOIN PDI p ON u.id_usuario = p.id_colaborador
                LEFT JOIN Areas a ON u.id_area = a.id_area
                LEFT JOIN Metas m ON p.id_pdi = m.id_pdi
                LEFT JOIN Skills s ON m.id_skill = s.id_skill
                -- REMOVIDA A CLÁUSULA GROUP BY
                ORDER BY a.nome_area, u.nome;
                """;
        return buscarPDIsGenerico(sql, null);
    }

    public List<PDIDashItem> buscarPDIsGestorGeral() {
        return buscarPDIsRH();
    }

    public List<PDIDashItem> buscarPDIsGestorArea(int idGestor) {
        String sql = """
                SELECT DISTINCT p.id_pdi, u.id_usuario, u.nome AS colaborador, s.nome_skill AS objetivo,
                       DATE_FORMAT(p.data_fim, '%d/%m/%Y') AS prazo,
                       CASE
                           WHEN m.percentual_atingido >= 100 THEN 'Concluído'
                           WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado'
                           ELSE 'Em Andamento'
                       END AS status,
                       a.nome_area AS area
                FROM Usuarios u
                JOIN PDI p ON u.id_usuario = p.id_colaborador
                LEFT JOIN Areas a ON u.id_area = a.id_area
                LEFT JOIN Metas m ON p.id_pdi = m.id_pdi
                LEFT JOIN Skills s ON m.id_skill = s.id_skill
                WHERE u.id_gestor_de_area = ?
                -- REMOVIDA A CLÁUSULA GROUP BY
                ORDER BY u.nome;
                """;
        return buscarPDIsGenerico(sql, idGestor);
    }

    public List<PDIDashItem> listarPDIColaborador(int idColaborador) {
        String sql = """
                SELECT DISTINCT p.id_pdi, u.id_usuario, u.nome AS colaborador, s.nome_skill AS objetivo,
                       DATE_FORMAT(p.data_fim, '%d/%m/%Y') AS prazo,
                       CASE
                           WHEN m.percentual_atingido >= 100 THEN 'Concluído'
                           WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado'
                           ELSE 'Em Andamento'
                       END AS status,
                       a.nome_area AS area
                FROM Usuarios u
                JOIN PDI p ON u.id_usuario = p.id_colaborador
                LEFT JOIN Areas a ON u.id_area = a.id_area
                LEFT JOIN Metas m ON p.id_pdi = m.id_pdi
                LEFT JOIN Skills s ON m.id_skill = s.id_skill
                WHERE u.id_usuario = ? 
                -- REMOVIDA A CLÁUSULA GROUP BY
                ORDER BY p.data_fim DESC;
                """;
        return buscarPDIsGenerico(sql, idColaborador);
    }

    // --- MÉTODOS DE CONTAGEM (NÃO PRECISAM DE MUDANÇA) ---
    public int contarTotalColaboradores() {
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE tipo_acesso = 'Colaborador'";
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int contarTotalPDIs() {
        String sql = "SELECT COUNT(*) FROM PDI";
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> contarPDIsPorStatus() {
        String sql = """
                SELECT 
                    CASE
                        WHEN m.percentual_atingido >= 100 THEN 'Concluído'
                        WHEN m.percentual_atingido < 100 AND p.data_fim < CURDATE() THEN 'Atrasado'
                        ELSE 'Em Andamento'
                    END AS status_calculado,
                    COUNT(DISTINCT p.id_pdi) AS total
                FROM PDI p
                LEFT JOIN Metas m ON p.id_pdi = m.id_pdi
                GROUP BY status_calculado;
                """;
        Map<String, Integer> contagem = new HashMap<>();
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("status_calculado");
                if (status != null) {
                    contagem.put(status, rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contagem;
    }

    public Map<String, Integer> contarPDIsPorArea() {
        String sql = """
                SELECT a.nome_area AS area_nome, COUNT(p.id_pdi) AS total
                FROM PDI p
                JOIN Usuarios u ON p.id_colaborador = u.id_usuario
                JOIN Areas a ON u.id_area = a.id_area
                GROUP BY a.nome_area
                ORDER BY total DESC;
                """;
        Map<String, Integer> contagem = new HashMap<>();
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String area = rs.getString("area_nome");
                contagem.put(area, rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contagem;
    }

    // --- MÉTODO GENÉRICO (NÃO PRECISA DE MUDANÇA) ---
    private List<PDIDashItem> buscarPDIsGenerico(String sql, Integer idParametro) {
        List<PDIDashItem> listaPDIs = new ArrayList<>();
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (idParametro != null) stmt.setInt(1, idParametro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PDIDashItem item = new PDIDashItem(
                            rs.getInt("id_pdi"),
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