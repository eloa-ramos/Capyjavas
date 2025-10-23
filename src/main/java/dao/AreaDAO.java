package dao;

import modelo.Area;
import factory.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    public List<Area> listarTodas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT id_area, nome_area FROM Areas ORDER BY nome_area";

        // Obtém a conexão usando factory
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Area area = new Area(
                        rs.getInt("id_area"),
                        rs.getString("nome_area")
                );
                areas.add(area);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar áreas: " + e.getMessage());
            throw new RuntimeException("Falha ao listar áreas. Verifique a conexão com o banco de dados.", e);
        }
        return areas;
    }
}