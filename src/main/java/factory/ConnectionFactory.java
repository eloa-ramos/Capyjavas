package factory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public Connection getConnection(){
        try{
            String url = "jdbc:mysql://localhost:3306/db_pdi";
            String user = "root";
            String password = "Vitoria.1405";
            return DriverManager.getConnection(url, user, password);
        }
        catch(SQLException excecao){
            System.err.println("ERRO DE CONEXÃO SQL:");
            System.err.println("SQL State: " + excecao.getSQLState());
            System.err.println("Message: " + excecao.getMessage());

            throw new RuntimeException("Falha na ConnectionFactory. Verifique o console para detalhes do erro SQL.", excecao);
        }
    }
}