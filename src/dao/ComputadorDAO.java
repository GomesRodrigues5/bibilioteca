package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Computador;

public class ComputadorDAO {
    public void salvar(Computador computador) throws SQLException {
        String sql = "INSERT INTO Computador (marca, modelo, numero, emUso) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, computador.getMarca());
            stmt.setString(2, computador.getModelo());
            stmt.setString(3, computador.getNumero());
            stmt.setBoolean(4, computador.isEmUso());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    computador.setIdComputador(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Computador computador) throws SQLException {
        String sql = "UPDATE Computador SET marca = ?, modelo = ?, numero = ?, emUso = ? WHERE id_computador = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, computador.getMarca());
            stmt.setString(2, computador.getModelo());
            stmt.setString(3, computador.getNumero());
            stmt.setBoolean(4, computador.isEmUso());
            stmt.setInt(5, computador.getIdComputador());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idComputador) throws SQLException {
        String sql = "DELETE FROM Computador WHERE id_computador = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idComputador);
            stmt.executeUpdate();
        }
    }

    public List<Computador> listarTodos() throws SQLException {
        List<Computador> computadores = new ArrayList<>();
        String sql = "SELECT id_computador, marca, modelo, numero, emUso FROM Computador ORDER BY id_computador";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Computador computador = new Computador();
                computador.setIdComputador(rs.getInt("id_computador"));
                computador.setMarca(rs.getString("marca"));
                computador.setModelo(rs.getString("modelo"));
                computador.setNumero(rs.getString("numero"));
                computador.setEmUso(rs.getBoolean("emUso"));
                computadores.add(computador);
            }
        }
        return computadores;
    }

    public Computador buscarPorTag(String tag) throws SQLException {
        String sql = "SELECT id_computador, marca, modelo, numero, emUso FROM Computador WHERE numero = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tag);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Computador computador = new Computador();
                    computador.setIdComputador(rs.getInt("id_computador"));
                    computador.setMarca(rs.getString("marca"));
                    computador.setModelo(rs.getString("modelo"));
                    computador.setNumero(rs.getString("numero"));
                    computador.setEmUso(rs.getBoolean("emUso"));
                    return computador;
                }
            }
        }
        return null;
    }
}