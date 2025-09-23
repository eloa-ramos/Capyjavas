package dao;

import factory.ConnectionFactory;
import modelo.Metas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class MetasDAO {

    private Connection connection;

    public MetasDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // Método para adicionar uma nova meta
    public void adiciona(Metas meta) {
        String sql = "INSERT INTO Metas (id_pdi, id_skill, meta_pontuacao, pontuacao_obtida) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, meta.getIdPdi());
            stmt.setInt(2, meta.getIdSkill());
            stmt.setBigDecimal(3, meta.getMetaPontuacao());
            stmt.setBigDecimal(4, meta.getPontuacaoObtida());

            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para atualizar uma meta existente
    public void atualiza(Metas meta) {
        String sql = "UPDATE Metas SET id_pdi = ?, id_skill = ?, meta_pontuacao = ?, pontuacao_obtida = ? WHERE id_meta = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, meta.getIdPdi());
            stmt.setInt(2, meta.getIdSkill());
            stmt.setBigDecimal(3, meta.getMetaPontuacao());
            stmt.setBigDecimal(4, meta.getPontuacaoObtida());
            stmt.setInt(5, meta.getIdMeta());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para deletar uma meta pelo ID
    public void deleta(int idMeta) {
        String sql = "DELETE FROM Metas WHERE id_meta = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMeta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para listar todas as metas
    public List<Metas> listaTodas() {
        List<Metas> metasList = new ArrayList<>();
        String sql = "SELECT id_meta, id_pdi, id_skill, meta_pontuacao, pontuacao_obtida FROM Metas";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Metas meta = new Metas();
                meta.setIdMeta(rs.getInt("id_meta"));
                meta.setIdPdi(rs.getInt("id_pdi"));
                meta.setIdSkill(rs.getInt("id_skill"));
                meta.setMetaPontuacao(rs.getBigDecimal("meta_pontuacao"));
                meta.setPontuacaoObtida(rs.getBigDecimal("pontuacao_obtida"));

                metasList.add(meta);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return metasList;
    }
}
