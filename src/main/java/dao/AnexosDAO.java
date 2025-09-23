package dao;

import factory.ConnectionFactory;
import modelo.Anexos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AnexosDAO {

    private Connection connection;

    public AnexosDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // Método para adicionar um anexo
    public void adiciona(Anexos anexo) {
        String sql = "INSERT INTO Anexos (id_pdi, nome_arquivo, caminho_arquivo, tipo_arquivo, observacoes) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, anexo.getIdPdi());
            stmt.setString(2, anexo.getNomeArquivo());
            stmt.setString(3, anexo.getCaminhoArquivo());
            stmt.setString(4, anexo.getTipoArquivo());
            stmt.setString(5, anexo.getObservacoes());

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Aqui você pode adicionar outros métodos, como atualizar, deletar ou listar anexos
}
