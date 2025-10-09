package dao;

import factory.ConnectionFactory;
import modelo.PDI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PDIDAO {

    public PDIDAO() {}

    /**
     * Cadastra um novo PDI no banco de dados.
     *
     * @param pdi Objeto PDI a ser inserido
     */
    public void cadastrarPDI(PDI pdi) {
        String sql = "INSERT INTO PDI (id_colaborador, data_inicio, data_fim, observacoes) VALUES (?, ?, ?, ?)";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pdi.getIdColaborador());
            stmt.setDate(2, java.sql.Date.valueOf(pdi.getDataInicio()));
            stmt.setDate(3, java.sql.Date.valueOf(pdi.getDataFim()));
            stmt.setString(4, pdi.getObservacoes());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar PDI: " + e.getMessage(), e);
        }
    }
}
