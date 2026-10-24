package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexaoMySQL {
    
    private static final String URL = "jdbc:mysql://localhost:3306/BibliotecaDBA?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection conectar() {
        try {
            Connection conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            JOptionPane.showMessageDialog(null,
                "Conexão com o banco de dados estabelecida com sucesso!",
                "Conexão Bem Sucedida",
                JOptionPane.INFORMATION_MESSAGE
            );
            return conexao;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao conectar com a base de dados: " + e.getMessage(),
                "Erro de Conexão",
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Connection obterConexao() {
        try {
            return getConnection();
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão com a base de dados: " + e.getMessage());
            return null;
        }
    }
}