package dao;

import factory.ConnectionFactory;
import modelo.Anexos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet; // NECESSÁRIO
import java.util.ArrayList; // NECESSÁRIO
import java.util.List; // NECESSÁRIO

public class AnexosDAO {

    public AnexosDAO() {}

    // Método para adicionar um anexo (Corrigido para garantir a conexão)
    public void adiciona(Anexos anexo) {
        String sql = "INSERT INTO Anexos (id_pdi, nome_arquivo, caminho_arquivo, tipo_arquivo, observacoes) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, anexo.getIdPdi());
            stmt.setString(2, anexo.getNomeArquivo());
            stmt.setString(3, anexo.getCaminhoArquivo());
            stmt.setString(4, anexo.getTipoArquivo());
            stmt.setString(5, anexo.getObservacoes());

            stmt.execute();

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar anexo: " + e.getMessage());
            throw new RuntimeException("Falha ao adicionar o anexo ao PDI. Detalhes: " + e.getMessage(), e);
        }
    }

    // --- NOVO MÉTODO: Listar Anexos por ID do PDI ---
    public List<Anexos> listarPorPdi(int idPdi) {
        List<Anexos> listaAnexos = new ArrayList<>();
        String sql = "SELECT id_anexo, nome_arquivo, caminho_arquivo, tipo_arquivo, observacoes FROM Anexos WHERE id_pdi = ?";

        try (Connection conn = new ConnectionFactory().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPdi);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Anexos anexo = new Anexos();
                    anexo.setIdAnexo(rs.getInt("id_anexo"));
                    anexo.setIdPdi(idPdi);
                    anexo.setNomeArquivo(rs.getString("nome_arquivo"));
                    anexo.setCaminhoArquivo(rs.getString("caminho_arquivo"));
                    anexo.setTipoArquivo(rs.getString("tipo_arquivo"));
                    anexo.setObservacoes(rs.getString("observacoes"));
                    listaAnexos.add(anexo);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar anexos por PDI: " + e.getMessage());
            throw new RuntimeException("Falha ao buscar anexos.", e);
        }
        return listaAnexos;
    }
}