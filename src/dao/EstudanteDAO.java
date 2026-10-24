package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Estudante;

public class EstudanteDAO {

    public void salvar(Estudante estudante) throws SQLException {
        try (Connection conn = getConnection()) {
            int idDepartamento = obterOuCriarDepartamento(conn, estudante.getDepartamento());
            int idCurso = estudante.getIdCurso();
            int idContacto = obterOuCriarContacto(conn, estudante.getContacto());

            String sql = "INSERT INTO Estudante (nome, numero_estudante, id_curso, id_contacto, email, id_endereco) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, estudante.getNome());
                stmt.setString(2, estudante.getNumeroEstudante());

                if (idCurso > 0) {
                    stmt.setInt(3, idCurso);
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }

                if (idContacto > 0) {
                    stmt.setInt(4, idContacto);
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }

                if (estudante.getEmail() != null && !estudante.getEmail().trim().isEmpty()) {
                    stmt.setString(5, estudante.getEmail());
                } else {
                    stmt.setNull(5, Types.VARCHAR);
                }

                stmt.setNull(6, Types.INTEGER);
                stmt.executeUpdate();
            }
        }
    }

    public void deletar(int idEstudante) throws SQLException {
        String sql = "DELETE FROM Estudante WHERE id_estudante = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstudante);
            stmt.executeUpdate();
        }
    }

    public List<Estudante> listarTodos() throws SQLException {
        List<Estudante> estudantes = new ArrayList<>();
        String sql = "SELECT e.id_estudante, e.nome, e.numero_estudante, c.curso, d.departamento, "
                + "CONCAT(co.telefone1, IFNULL(CONCAT(' / ', co.telefone2), '')) AS contacto "
                + "FROM Estudante e "
                + "LEFT JOIN Curso c ON e.id_curso = c.id_curso "
                + "LEFT JOIN Departamento d ON c.id_departamento = d.id_departamento "
                + "LEFT JOIN Contacto co ON e.id_contacto = co.id_contacto";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Estudante estudante = new Estudante();
                estudante.setIdEstudante(rs.getInt("id_estudante"));
                estudante.setNome(rs.getString("nome"));
                estudante.setNumeroEstudante(rs.getString("numero_estudante"));
                estudante.setCurso(rs.getString("curso"));
                estudante.setDepartamento(rs.getString("departamento"));
                estudante.setContacto(rs.getString("contacto"));
                estudantes.add(estudante);
            }
        }
        return estudantes;
    }

    public Estudante buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT e.id_estudante, e.nome, e.numero_estudante, c.curso, d.departamento, "
                + "CONCAT(co.telefone1, IFNULL(CONCAT(' / ', co.telefone2), '')) AS contacto "
                + "FROM Estudante e "
                + "LEFT JOIN Curso c ON e.id_curso = c.id_curso "
                + "LEFT JOIN Departamento d ON c.id_departamento = d.id_departamento "
                + "LEFT JOIN Contacto co ON e.id_contacto = co.id_contacto "
                + "WHERE e.numero_estudante = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Estudante estudante = new Estudante();
                    estudante.setIdEstudante(rs.getInt("id_estudante"));
                    estudante.setNome(rs.getString("nome"));
                    estudante.setNumeroEstudante(rs.getString("numero_estudante"));
                    estudante.setCurso(rs.getString("curso"));
                    estudante.setDepartamento(rs.getString("departamento"));
                    estudante.setContacto(rs.getString("contacto"));
                    return estudante;
                }
            }
        }
        return null;
    }

    private int obterOuCriarDepartamento(Connection conn, String departamento) throws SQLException {
        if (departamento == null || departamento.trim().isEmpty()) {
            return 0;
        }

        String selectSql = "SELECT id_departamento FROM Departamento WHERE departamento = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, departamento.trim());
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_departamento");
                }
            }
        }

        String insertSql = "INSERT INTO Departamento (departamento) VALUES (?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, departamento.trim());
            insertStmt.executeUpdate();
            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<CursoItem> listarCursos() throws SQLException {
        List<CursoItem> cursos = new ArrayList<>();
        String sql = "SELECT id_curso, curso FROM Curso ORDER BY curso";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cursos.add(new CursoItem(rs.getInt("id_curso"), rs.getString("curso")));
            }
        }
        return cursos;
    }

    private int obterOuCriarContacto(Connection conn, String contacto) throws SQLException {
        if (contacto == null || contacto.trim().isEmpty()) {
            return 0;
        }

        String telefone1 = contacto.trim();
        if (telefone1.contains("/")) {
            telefone1 = telefone1.split("/")[0].trim();
        }

        String selectSql = "SELECT id_contacto FROM Contacto WHERE telefone1 = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, telefone1);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_contacto");
                }
            }
        }

        String insertSql = "INSERT INTO Contacto (telefone1, telefone2) VALUES (?, NULL)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, telefone1);
            insertStmt.executeUpdate();
            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Connection getConnection() throws SQLException {
        return ConexaoMySQL.getConnection();
    }

    public static class CursoItem {
        private final int id;
        private final String nome;

        public CursoItem(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }

        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        @Override
        public String toString() {
            return nome;
        }
    }
}