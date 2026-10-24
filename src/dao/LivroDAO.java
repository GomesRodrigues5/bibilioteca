package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Livro;

public class LivroDAO {
    public void salvar(Livro livro) {
        String sql = "INSERT INTO Livro (titulo, cor, pagina, codigo, id_armario, disponivel) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getCor());
            stmt.setInt(3, livro.getPagina());
            stmt.setString(4, livro.getCodigo());
            stmt.setInt(5, livro.getIdArmario());
            stmt.setBoolean(6, livro.isDisponivel());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    livro.setIdLivro(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar livro: " + e.getMessage());
        }
    }

    public void atualizar(Livro livro) {
        String sql = "UPDATE Livro SET titulo = ?, cor = ?, pagina = ?, codigo = ?, id_armario = ?, disponivel = ? WHERE id_livro = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getCor());
            stmt.setInt(3, livro.getPagina());
            stmt.setString(4, livro.getCodigo());
            stmt.setInt(5, livro.getIdArmario());
            stmt.setBoolean(6, livro.isDisponivel());
            stmt.setInt(7, livro.getIdLivro());
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar livro: " + e.getMessage());
        }
    }

    public void deletar(int idLivro) {
        String sql = "DELETE FROM Livro WHERE id_livro = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar livro: " + e.getMessage());
        }
    }

    public List<Livro> listarTodos() {
        List<Livro> livros = new ArrayList<>();
        String sql = "SELECT l.id_livro, l.titulo, l.cor, l.pagina, l.codigo, l.id_armario, l.disponivel, a.armario "
                + "FROM Livro l LEFT JOIN Armario a ON l.id_armario = a.id_armario ORDER BY l.id_livro";
        try (Connection conn = ConexaoMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Livro livro = new Livro();
                livro.setIdLivro(rs.getInt("id_livro"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setCor(rs.getString("cor"));
                livro.setPagina(rs.getInt("pagina"));
                livro.setCodigo(rs.getString("codigo"));
                livro.setIdArmario(rs.getInt("id_armario"));
                livro.setArmarioNome(rs.getString("armario"));
                livro.setDisponivel(rs.getBoolean("disponivel"));
                livros.add(livro);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar livros: " + e.getMessage());
        }
        return livros;
    }

    public Livro buscarPorTitulo(String titulo) throws SQLException {
        String sql = "SELECT l.id_livro, l.titulo, l.cor, l.pagina, l.codigo, l.id_armario, l.disponivel, a.armario "
                + "FROM Livro l LEFT JOIN Armario a ON l.id_armario = a.id_armario WHERE l.titulo = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Livro livro = new Livro();
                    livro.setIdLivro(rs.getInt("id_livro"));
                    livro.setTitulo(rs.getString("titulo"));
                    livro.setCor(rs.getString("cor"));
                    livro.setPagina(rs.getInt("pagina"));
                    livro.setCodigo(rs.getString("codigo"));
                    livro.setIdArmario(rs.getInt("id_armario"));
                    livro.setArmarioNome(rs.getString("armario"));
                    livro.setDisponivel(rs.getBoolean("disponivel"));
                    return livro;
                }
            }
        }
        return null;
    }
}