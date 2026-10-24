package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Funcionario;
import view.TelaPrincipalAdmin;

public class FuncionarioDAO {

    public void salvar(Funcionario funcionario) throws SQLException {
        try (Connection conn = ConexaoMySQL.getConnection()) {
            String sql = "INSERT INTO Funcionario (funcionario, id_funcao, id_departamento, id_contacto, senha) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                int idFuncao = obterOuCriarFuncao(conn, funcionario.getFuncao());
                int idDepartamento = funcionario.getIdDepartamento();
                int idContacto = funcionario.getIdContacto();

                stmt.setString(1, funcionario.getNome());
                if (idFuncao > 0) {
                    stmt.setInt(2, idFuncao);
                } else {
                    stmt.setNull(2, Types.INTEGER);
                }
                if (idDepartamento > 0) {
                    stmt.setInt(3, idDepartamento);
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }
                if (idContacto > 0) {
                    stmt.setInt(4, idContacto);
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }
                // Hash da senha se ainda não estiver no formato PBKDF2$
                String senhaParaSalvar = funcionario.getSenha();
                if (senhaParaSalvar != null && !senhaParaSalvar.startsWith("PBKDF2$") && !senhaParaSalvar.startsWith("BCRYPT$")) {
                    senhaParaSalvar = util.PasswordHasher.hash(senhaParaSalvar);
                }
                stmt.setString(5, senhaParaSalvar);
                
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
        String sql = "UPDATE Funcionario SET funcionario = ?, senha = ?, id_funcao = ?, id_departamento = ?, id_contacto = ? WHERE id_funcionario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idFuncao = obterOuCriarFuncao(conn, funcionario.getFuncao());
            int idDepartamento = funcionario.getIdDepartamento();
            int idContacto = funcionario.getIdContacto();

            stmt.setString(1, funcionario.getNome());
            // Atualizar senha: se já estiver em formato hash, salva como está; caso contrário, hash
            String senhaParaSalvar = funcionario.getSenha();
            if (senhaParaSalvar != null && !senhaParaSalvar.startsWith("PBKDF2$") && !senhaParaSalvar.startsWith("BCRYPT$")) {
                senhaParaSalvar = util.PasswordHasher.hash(senhaParaSalvar);
            }
            stmt.setString(2, senhaParaSalvar);
            if (idFuncao > 0) {
                stmt.setInt(3, idFuncao);
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            if (idDepartamento > 0) {
                stmt.setInt(4, idDepartamento);
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            if (idContacto > 0) {
                stmt.setInt(5, idContacto);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, funcionario.getIdFuncionario());
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
        String sql = "SELECT f.id_funcionario, f.funcionario, f.senha, fu.funcao, d.departamento, "
                + "CONCAT(co.telefone1, IFNULL(CONCAT(' / ', co.telefone2), '')) AS contacto "
                + "FROM Funcionario f "
                + "LEFT JOIN Funcao fu ON f.id_funcao = fu.id_funcao "
                + "LEFT JOIN Departamento d ON f.id_departamento = d.id_departamento "
                + "LEFT JOIN Contacto co ON f.id_contacto = co.id_contacto "
                + "ORDER BY f.id_funcionario";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            int contador = 0;
            while (rs.next()) {
                contador++;
                Funcionario funcionario = new Funcionario();
                funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
                funcionario.setNome(rs.getString("funcionario"));
                funcionario.setSenha(rs.getString("senha"));
                funcionario.setFuncao(rs.getString("funcao"));
                funcionario.setDepartamento(rs.getString("departamento"));
                funcionario.setContacto(rs.getString("contacto"));

                System.out.println("[DAO DEBUG] Funcionário #" + contador + " carregado:");
                System.out.println("  - ID: " + funcionario.getIdFuncionario());
                System.out.println("  - Nome: '" + funcionario.getNome() + "'");
                System.out.println("  - Função: '" + funcionario.getFuncao() + "'");
                System.out.println("  - Contacto: '" + funcionario.getContacto() + "'");
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
        String sql = "SELECT f.id_funcionario, f.funcionario, f.senha, fu.funcao, d.departamento, "
                + "CONCAT(co.telefone1, IFNULL(CONCAT(' / ', co.telefone2), '')) AS contacto "
                + "FROM Funcionario f "
                + "LEFT JOIN Funcao fu ON f.id_funcao = fu.id_funcao "
                + "LEFT JOIN Departamento d ON f.id_departamento = d.id_departamento "
                + "LEFT JOIN Contacto co ON f.id_contacto = co.id_contacto "
                + "WHERE f.funcionario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
                    funcionario.setNome(rs.getString("funcionario"));
                    funcionario.setSenha(rs.getString("senha"));
                    funcionario.setFuncao(rs.getString("funcao"));
                    funcionario.setDepartamento(rs.getString("departamento"));
                    funcionario.setContacto(rs.getString("contacto"));
                    return funcionario;
                }
            }
        }
        return null;
    }

    public Funcionario buscarPorId(int idFuncionario) throws SQLException {
        String sql = "SELECT f.id_funcionario, f.funcionario, f.senha, fu.funcao, d.departamento, "
                + "CONCAT(co.telefone1, IFNULL(CONCAT(' / ', co.telefone2), '')) AS contacto "
                + "FROM Funcionario f "
                + "LEFT JOIN Funcao fu ON f.id_funcao = fu.id_funcao "
                + "LEFT JOIN Departamento d ON f.id_departamento = d.id_departamento "
                + "LEFT JOIN Contacto co ON f.id_contacto = co.id_contacto "
                + "WHERE f.id_funcionario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFuncionario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Funcionario funcionario = new Funcionario();
                    funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
                    funcionario.setNome(rs.getString("funcionario"));
                    funcionario.setSenha(rs.getString("senha"));
                    funcionario.setFuncao(rs.getString("funcao"));
                    funcionario.setDepartamento(rs.getString("departamento"));
                    funcionario.setContacto(rs.getString("contacto"));
                    return funcionario;
                }
            }
        }
        return null;
    }

    private int obterOuCriarFuncao(Connection conn, String funcao) throws SQLException {
        if (funcao == null || funcao.trim().isEmpty()) {
            return 0;
        }

        String selectSql = "SELECT id_funcao FROM Funcao WHERE funcao = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, funcao.trim());
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_funcao");
                }
            }
        }

        String insertSql = "INSERT INTO Funcao (funcao, descricao) VALUES (?, NULL)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, funcao.trim());
            insertStmt.executeUpdate();
            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Valida as credenciais (usuário + senha) no banco de dados
     * @param usuario Nome do funcionário
     * @param senha Senha do funcionário
     * @return Funcionário se credenciais válidas, null caso contrário
     */
    public Funcionario validarCredenciais(String usuario, String senha) throws SQLException {
        String sql = "SELECT f.id_funcionario, f.funcionario, f.senha, fu.funcao, d.departamento, "
                + "CONCAT(co.telefone1, IFNULL(CONCAT(' / ', co.telefone2), '')) AS contacto "
                + "FROM Funcionario f "
                + "LEFT JOIN Funcao fu ON f.id_funcao = fu.id_funcao "
                + "LEFT JOIN Departamento d ON f.id_departamento = d.id_departamento "
                + "LEFT JOIN Contacto co ON f.id_contacto = co.id_contacto "
                + "WHERE f.funcionario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaArmazenada = rs.getString("senha");
                    boolean ok = util.PasswordHasher.verify(senha, senhaArmazenada);
                    if (!ok) {
                        System.out.println("[DAO] ✗ Credenciais inválidas para usuário '" + usuario + "'");
                        return null;
                    }
                    Funcionario funcionario = new Funcionario();
                    funcionario.setIdFuncionario(rs.getInt("id_funcionario"));
                    funcionario.setNome(rs.getString("funcionario"));
                    funcionario.setSenha(senhaArmazenada);
                    funcionario.setFuncao(rs.getString("funcao"));
                    funcionario.setDepartamento(rs.getString("departamento"));
                    funcionario.setContacto(rs.getString("contacto"));
                    return funcionario;
                } else {
                    System.out.println("[DAO] ✗ Usuário não encontrado: '" + usuario + "'");
                }
            }
        }
        return null;
    }
}