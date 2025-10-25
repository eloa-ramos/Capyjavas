package dao;

import factory.ConnectionFactory;
import modelo.PDI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // <-- IMPORTANTE

public class PDIDAO {

    public PDIDAO() {}

    /**
     * MODIFICADO: Agora retorna o ID do PDI cadastrado.
     */
    public int cadastrarPDI(PDI pdi) {
        String sql = "INSERT INTO PDI (id_colaborador, data_inicio, data_fim, observacoes) VALUES (?, ?, ?, ?)";

        // Usamos Statement.RETURN_GENERATED_KEYS para pegar o ID
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pdi.getIdColaborador());
            stmt.setDate(2, java.sql.Date.valueOf(pdi.getDataInicio()));
            stmt.setDate(3, java.sql.Date.valueOf(pdi.getDataFim()));
            stmt.setString(4, pdi.getObservacoes());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Cadastro do PDI falhou, nenhuma linha afetada.");
            }

            // --- ESTE BLOCO É A CORREÇÃO ---
            // Recupera o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Retorna o ID (ex: 4)
                } else {
                    throw new SQLException("Cadastro do PDI falhou, não foi possível obter o ID.");
                }
            }
            // --- FIM DO BLOCO ---

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar PDI: " + e.getMessage(), e);
        }
    }

    /**
     * NOVO: Busca um PDI completo pelo seu ID.
     */
    public PDI buscarPdiPorId(int idPdi) {
        String sql = "SELECT * FROM PDI WHERE id_pdi = ?";
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPdi);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PDI pdi = new PDI();
                pdi.setIdPdi(rs.getInt("id_pdi"));
                pdi.setIdColaborador(rs.getInt("id_colaborador"));
                pdi.setDataInicio(rs.getDate("data_inicio").toLocalDate());
                pdi.setDataFim(rs.getDate("data_fim").toLocalDate());
                pdi.setObservacoes(rs.getString("observacoes"));
                return pdi;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar PDI por ID: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * NOVO: Atualiza um PDI existente no banco de dados.
     */
    public void atualizarPDI(PDI pdi) {
        String sql = "UPDATE PDI SET data_inicio = ?, data_fim = ?, observacoes = ? WHERE id_pdi = ?";
        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(pdi.getDataInicio()));
            stmt.setDate(2, java.sql.Date.valueOf(pdi.getDataFim()));
            stmt.setString(3, pdi.getObservacoes());
            stmt.setInt(4, pdi.getIdPdi());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar PDI: " + e.getMessage(), e);
        }
    }
}