package dao;

import factory.ConnectionFactory;
import modelo.Skills;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SkillsDAO {
    private Connection connection;

    public SkillsDAO() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Skills skill) {
        String sql = "INSERT INTO skills (nome_skill, tipo_skill) VALUES (?, ?)";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, skill.getNomeSkill());
            stmt.setString(2, skill.getTipoSkill());

            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
