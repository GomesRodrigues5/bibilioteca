package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Endereco;

public class EnderecoDAO {

    public void salvar(Endereco endereco) throws SQLException {
        String sql = "INSERT INTO Endereco (municipio, bairro) VALUES (?, ?)";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, endereco.getMunicipio());
            stmt.setString(2, endereco.getBairro());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    endereco.setIdEndereco(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Endereco endereco) throws SQLException {
        String sql = "UPDATE Endereco SET municipio = ?, bairro = ? WHERE id_endereco = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, endereco.getMunicipio());
            stmt.setString(2, endereco.getBairro());
            stmt.setInt(3, endereco.getIdEndereco());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idEndereco) throws SQLException {
        String sql = "DELETE FROM Endereco WHERE id_endereco = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEndereco);
            stmt.executeUpdate();
        }
    }

    public List<Endereco> listarTodos() throws SQLException {
        List<Endereco> enderecos = new ArrayList<>();
        String sql = "SELECT id_endereco, municipio, bairro FROM Endereco ORDER BY municipio, bairro";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Endereco endereco = new Endereco();
                endereco.setIdEndereco(rs.getInt("id_endereco"));
                endereco.setMunicipio(rs.getString("municipio"));
                endereco.setBairro(rs.getString("bairro"));
                enderecos.add(endereco);
            }
        }
        return enderecos;
    }

    public Endereco buscarPorId(int idEndereco) throws SQLException {
        String sql = "SELECT id_endereco, municipio, bairro FROM Endereco WHERE id_endereco = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEndereco);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Endereco endereco = new Endereco();
                    endereco.setIdEndereco(rs.getInt("id_endereco"));
                    endereco.setMunicipio(rs.getString("municipio"));
                    endereco.setBairro(rs.getString("bairro"));
                    return endereco;
                }
            }
        }
        return null;
    }
}