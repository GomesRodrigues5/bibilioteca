package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Curso;

public class CursoDAO {
    public void salvar(Curso curso) throws SQLException {
        String sql = "INSERT INTO Curso (curso, id_departamento) VALUES (?, ?)";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, curso.getCurso());
            if (curso.getIdDepartamento() > 0) {
                stmt.setInt(2, curso.getIdDepartamento());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    curso.setIdCurso(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Curso curso) throws SQLException {
        String sql = "UPDATE Curso SET curso = ?, id_departamento = ? WHERE id_curso = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, curso.getCurso());
            if (curso.getIdDepartamento() > 0) {
                stmt.setInt(2, curso.getIdDepartamento());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setInt(3, curso.getIdCurso());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idCurso) throws SQLException {
        String sql = "DELETE FROM Curso WHERE id_curso = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCurso);
            stmt.executeUpdate();
        }
    }

    public List<Curso> listarTodos() throws SQLException {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT c.id_curso, c.curso, d.departamento FROM Curso c LEFT JOIN Departamento d ON c.id_departamento = d.id_departamento ORDER BY c.curso";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("id_curso"));
                curso.setCurso(rs.getString("curso"));
                curso.setDepartamento(rs.getString("departamento"));
                cursos.add(curso);
            }
        }
        return cursos;
    }

    public Curso buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT c.id_curso, c.curso, d.departamento FROM Curso c LEFT JOIN Departamento d ON c.id_departamento = d.id_departamento WHERE c.curso = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Curso curso = new Curso();
                    curso.setIdCurso(rs.getInt("id_curso"));
                    curso.setCurso(rs.getString("curso"));
                    curso.setDepartamento(rs.getString("departamento"));
                    return curso;
                }
            }
        }
        return null;
    }

    public Curso buscarPorId(int idCurso) throws SQLException {
        String sql = "SELECT c.id_curso, c.curso, d.departamento FROM Curso c LEFT JOIN Departamento d ON c.id_departamento = d.id_departamento WHERE c.id_curso = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCurso);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Curso curso = new Curso();
                    curso.setIdCurso(rs.getInt("id_curso"));
                    curso.setCurso(rs.getString("curso"));
                    curso.setDepartamento(rs.getString("departamento"));
                    return curso;
                }
            }
        }
        return null;
    }
}