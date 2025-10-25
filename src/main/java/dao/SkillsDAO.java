package dao;

import factory.ConnectionFactory;
import modelo.Skills; //

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // IMPORTADO
import java.sql.SQLException;
import java.sql.Statement; // IMPORTADO
import java.util.ArrayList; // IMPORTADO
import java.util.List; // IMPORTADO

public class SkillsDAO {
    private Connection connection;

    public SkillsDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    /**
     * MODIFICADO: Adiciona uma skill e RETORNA seu novo ID.
     */
    public int adicionaERetornaId(Skills skill) { //
        String sql = "INSERT INTO skills (nome_skill, tipo_skill) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, skill.getNomeSkill()); //
            stmt.setString(2, skill.getTipoSkill()); //

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Cadastro da Skill falhou, nenhuma linha afetada.");
            }

            // Recupera o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    skill.setIdSkill(newId); // Atualiza o objeto original
                    return newId; // Retorna o ID
                } else {
                    throw new SQLException("Cadastro da Skill falhou, não foi possível obter o ID.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * NOVO: Busca uma skill pelo nome exato.
     * Retorna o objeto Skills se encontrar, ou null se não encontrar.
     */
    public Skills buscarPorNome(String nomeSkill) {
        String sql = "SELECT * FROM skills WHERE nome_skill = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nomeSkill);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Skills skill = new Skills(); //
                    skill.setIdSkill(rs.getInt("id_skill")); //
                    skill.setNomeSkill(rs.getString("nome_skill")); //
                    skill.setTipoSkill(rs.getString("tipo_skill")); //
                    return skill;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar skill por nome: " + e.getMessage(), e);
        }
        return null; // Não encontrou
    }

    /**
     * NOVO: Método "Find or Create"
     * Tenta buscar uma skill pelo nome. Se não existir, cria uma nova.
     * @param nomeSkill O nome da skill a ser buscada ou criada.
     * @param tipoSkillPadrao O tipo (ex: "Soft Skill") a ser usado se a skill for nova.
     * @return O ID da skill (existente ou recém-criada).
     */
    public int buscarOuCriarSkill(String nomeSkill, String tipoSkillPadrao) {
        // 1. Tenta buscar
        Skills skillExistente = buscarPorNome(nomeSkill);

        if (skillExistente != null) {
            // 2. Se encontrou, retorna o ID
            return skillExistente.getIdSkill(); //
        } else {
            // 3. Se não encontrou, cria
            Skills novaSkill = new Skills(); //
            novaSkill.setNomeSkill(nomeSkill); //
            novaSkill.setTipoSkill(tipoSkillPadrao); //

            // 4. Adiciona e retorna o novo ID
            return adicionaERetornaId(novaSkill);
        }
    }

    // Método para fechar a conexão
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}