package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Funcao;

public class FuncaoDAO {

    public void salvar(Funcao funcao) throws SQLException {
        String nome = funcao.getFuncao();
        try (Connection conn = ConexaoMySQL.getConnection()) {
            if (existeFuncao(conn, nome, 0)) {
                throw new SQLException("Já existe uma função cadastrada com este nome.");
            }
            String sql = "INSERT INTO Funcao (funcao, descricao) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, nome);
                stmt.setString(2, funcao.getDescricao());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        funcao.setIdFuncao(rs.getInt(1));
                    }
                }
            }
        }
    }

    public void atualizar(Funcao funcao) throws SQLException {
        String nome = funcao.getFuncao();
        try (Connection conn = ConexaoMySQL.getConnection()) {
            if (existeFuncao(conn, nome, funcao.getIdFuncao())) {
                throw new SQLException("Já existe outra função cadastrada com este nome.");
            }
            String sql = "UPDATE Funcao SET funcao = ?, descricao = ? WHERE id_funcao = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nome);
                stmt.setString(2, funcao.getDescricao());
                stmt.setInt(3, funcao.getIdFuncao());
                stmt.executeUpdate();
            }
        }
    }

    public void deletar(int idFuncao) throws SQLException {
        String sql = "DELETE FROM Funcao WHERE id_funcao = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFuncao);
            stmt.executeUpdate();
        }
    }

    public List<Funcao> listarTodos() throws SQLException {
        List<Funcao> funcoes = new ArrayList<>();
        String sql = "SELECT id_funcao, funcao, descricao FROM Funcao ORDER BY funcao";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Funcao funcao = new Funcao();
                funcao.setIdFuncao(rs.getInt("id_funcao"));
                funcao.setFuncao(rs.getString("funcao"));
                funcao.setDescricao(rs.getString("descricao"));
                funcoes.add(funcao);
            }
        }
        return funcoes;
    }

    public Funcao buscarPorId(int idFuncao) throws SQLException {
        String sql = "SELECT id_funcao, funcao, descricao FROM Funcao WHERE id_funcao = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFuncao);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Funcao funcao = new Funcao();
                    funcao.setIdFuncao(rs.getInt("id_funcao"));
                    funcao.setFuncao(rs.getString("funcao"));
                    funcao.setDescricao(rs.getString("descricao"));
                    return funcao;
                }
            }
        }
        return null;
    }

    private boolean existeFuncao(Connection conn, String nome, int excludeId) throws SQLException {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT id_funcao FROM Funcao WHERE funcao = ?";
        if (excludeId > 0) {
            sql += " AND id_funcao <> ?";
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            if (excludeId > 0) {
                stmt.setInt(2, excludeId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}