package dao;

import modelo.Area; //
import factory.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    public List<Area> listarTodas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT id_area, nome_area FROM Areas ORDER BY nome_area";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Area area = new Area( //
                        rs.getInt("id_area"),
                        rs.getString("nome_area")
                );
                areas.add(area);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar áreas: " + e.getMessage());
            throw new RuntimeException("Falha ao listar áreas.", e);
        }
        return areas;
    }

    /**
     * NOVO: Busca uma área pelo nome exato.
     */
    public Area buscarPorNome(String nome) {
        String sql = "SELECT id_area, nome_area FROM Areas WHERE nome_area = ?";
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Area( //
                            rs.getInt("id_area"),
                            rs.getString("nome_area")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar área por nome: " + e.getMessage());
        }
        return null; // Não encontrou
    }

    /**
     * NOVO: Adiciona uma nova área e retorna o ID gerado.
     */
    public int adicionaERetornaId(String nomeArea) {
        String sql = "INSERT INTO Areas (nome_area) VALUES (?)";
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nomeArea);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Cadastro da Área falhou.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Retorna o novo ID
                } else {
                    throw new SQLException("Não foi possível obter o ID da nova Área.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar nova área: " + e.getMessage());
            throw new RuntimeException("Falha ao adicionar área.", e);
        }
    }

    /**
     * NOVO: Método principal "Find or Create".
     * Retorna o ID da área (seja ela existente ou recém-criada).
     */
    public int buscarOuCriarArea(String nomeArea) {
        // 1. Tenta buscar
        Area areaExistente = buscarPorNome(nomeArea);

        if (areaExistente != null) {
            // 2. Se encontrou, retorna o ID
            return areaExistente.getIdArea(); //
        } else {
            // 3. Se não encontrou, cria e retorna o novo ID
            return adicionaERetornaId(nomeArea);
        }
    }
}