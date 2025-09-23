package factory;

import java.sql.SQLException;
import java.sql.Connection;

public class TestaConexao {

    public static void main(String[] args) throws SQLException {
        Connection connection = new ConnectionFactory().getConnection();
        System.out.println("Conexão aberta");
        connection.close();
    }
}
