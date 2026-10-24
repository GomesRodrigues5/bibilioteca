package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Armario;

public class ArmarioDAO {
    public void salvar(Armario armario) throws SQLException {
        String sql = "INSERT INTO Armario (armario) VALUES (?)";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, armario.getArmario());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    armario.setIdArmario(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Armario armario) throws SQLException {
        String sql = "UPDATE Armario SET armario = ? WHERE id_armario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, armario.getArmario());
            stmt.setInt(2, armario.getIdArmario());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idArmario) throws SQLException {
        String sql = "DELETE FROM Armario WHERE id_armario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idArmario);
            stmt.executeUpdate();
        }
    }

    public List<Armario> listarTodos() throws SQLException {
        List<Armario> armarios = new ArrayList<>();
        String sql = "SELECT id_armario, armario FROM Armario ORDER BY armario";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Armario armario = new Armario();
                armario.setIdArmario(rs.getInt("id_armario"));
                armario.setArmario(rs.getString("armario"));
                armarios.add(armario);
            }
        }
        return armarios;
    }

    public Armario buscarPorLocalizacao(String localizacao) throws SQLException {
        String sql = "SELECT id_armario, armario FROM Armario WHERE armario = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, localizacao);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Armario armario = new Armario();
                    armario.setIdArmario(rs.getInt("id_armario"));
                    armario.setArmario(rs.getString("armario"));
                    return armario;
                }
            }
        }
        return null;
    }
}