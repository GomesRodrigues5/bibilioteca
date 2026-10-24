package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Departamento;

public class DepartamentoDAO {
    public void salvar(Departamento departamento) throws SQLException {
        String sql = "INSERT INTO Departamento (departamento) VALUES (?)";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, departamento.getDepartamento());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    departamento.setIdDepartamento(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Departamento departamento) throws SQLException {
        String sql = "UPDATE Departamento SET departamento = ? WHERE id_departamento = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, departamento.getDepartamento());
            stmt.setInt(2, departamento.getIdDepartamento());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idDepartamento) throws SQLException {
        String sql = "DELETE FROM Departamento WHERE id_departamento = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDepartamento);
            stmt.executeUpdate();
        }
    }

    public List<Departamento> listarTodos() throws SQLException {
        List<Departamento> departamentos = new ArrayList<>();
        String sql = "SELECT id_departamento, departamento FROM Departamento ORDER BY departamento";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Departamento departamento = new Departamento();
                departamento.setIdDepartamento(rs.getInt("id_departamento"));
                departamento.setDepartamento(rs.getString("departamento"));
                departamentos.add(departamento);
            }
        }
        return departamentos;
    }

    public Departamento buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT id_departamento, departamento FROM Departamento WHERE departamento = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Departamento departamento = new Departamento();
                    departamento.setIdDepartamento(rs.getInt("id_departamento"));
                    departamento.setDepartamento(rs.getString("departamento"));
                    return departamento;
                }
            }
        }
        return null;
    }
}