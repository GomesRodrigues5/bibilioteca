package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Funcionario;

public class FuncionarioDAO {

    public void salvar(Funcionario funcionario) throws SQLException {
        try (Connection conn = ConexaoMySQL.getConnection()) {
            String sql = "INSERT INTO Funcionario (funcionario, id_funcao, id_departamento, id_contacto, senha) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, funcionario.getNome());
                if (funcionario.getIdDepartamento() > 0) {
                    stmt.setInt(2, funcionario.getIdDepartamento());
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }
                if (funcionario.getIdContacto() > 0) {
                    stmt.setInt(3, funcionario.getIdContacto());
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }
                stmt.setString(4, funcionario.getSenha());
                
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        funcionario.setIdFuncionario(rs.getInt(1));
                    }
                }
            }
        }
    }

    public void atualizar(Funcionario funcionario) throws SQLException {
        String sql = "UPDATE Funcionario SET funcionario = ?, senha = ? WHERE id_funcionario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getSenha());
            stmt.setInt(3, funcionario.getIdFuncionario());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idFuncionario) throws SQLException {
        String sql = "DELETE FROM Funcionario WHERE id_funcionario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFuncionario);
            stmt.executeUpdate();
        }
    }

    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> funcionarios = new ArrayList<>();
        // Query simplificada - removendo coluna 'Func' que pode não existir
        String sql = "SELECT f.id_funcionario, f.funcionario, f.senha "
                + "FROM Funcionario f "
                + "ORDER BY f.id_funcionario";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            int contador = 0;
            while (rs.next()) {
                contador++;
                Funcionario funcionario = new Funcionario();
                funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
                funcionario.setNome(rs.getString("funcionario"));
                funcionario.setSenha(rs.getString("senha"));
                
                // Debug
                System.out.println("[DAO DEBUG] Funcionário #" + contador + " carregado:");
                System.out.println("  - ID: " + funcionario.getIdFuncionario());
                System.out.println("  - Nome: '" + funcionario.getNome() + "'");
                System.out.println("  - Senha: '" + funcionario.getSenha() + "' (null=" + (funcionario.getSenha() == null) + ")");
                
                funcionarios.add(funcionario);
            }
            System.out.println("[DAO DEBUG] Total de funcionários carregados do banco: " + contador);
        }
        return funcionarios;
    }

    /**
     * Busca um funcionário por nome (usuário)
     */
    public Funcionario buscarPorNome(String nome) throws SQLException {
        // Query simplificada - removendo colunas que podem não existir
        String sql = "SELECT f.id_funcionario, f.funcionario, f.senha "
                + "FROM Funcionario f "
                + "WHERE f.funcionario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
                    funcionario.setNome(rs.getString("funcionario"));
                    funcionario.setSenha(rs.getString("senha"));
                    return funcionario;
                }
            }
        }
        return null;
    }

    /**
     * Valida as credenciais (usuário + senha) no banco de dados
     * @param usuario Nome do funcionário
     * @param senha Senha do funcionário
     * @return Funcionário se credenciais válidas, null caso contrário
     */
    public Funcionario validarCredenciais(String usuario, String senha) throws SQLException {
        String sql = "SELECT id_funcionario, funcionario, senha FROM Funcionario WHERE funcionario = ? AND senha = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
                    funcionario.setNome(rs.getString("funcionario"));
                    funcionario.setSenha(rs.getString("senha"));
                    
                    System.out.println("[DAO] ✓ Credenciais válidas para: " + funcionario.getNome());
                    return funcionario;
                } else {
                    System.out.println("[DAO] ✗ Credenciais inválidas! Usuario='" + usuario + "' | Senha='" + senha + "'");
                }
            }
        }
        return null;
    }
}