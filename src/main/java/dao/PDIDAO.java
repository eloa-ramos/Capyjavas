package dao;

import factory.ConnectionFactory;
import modelo.PDI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class PDIDAO {
    private Connection connection;

    public PDIDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // Método para adicionar um PDI no banco
    public void adiciona(PDI pdi) {
        String sql = "INSERT INTO PDI (id_colaborador, data_inicio, data_fim, observacoes) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, pdi.getIdColaborador());
            stmt.setDate(2, Date.valueOf(pdi.getDataInicio()));
            stmt.setDate(3, Date.valueOf(pdi.getDataFim()));
            stmt.setString(4, pdi.getObservacoes());

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
