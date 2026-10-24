package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Estudante;

public class EstudanteDAO {

    public void salvar(Estudante estudante) throws SQLException {
        try (Connection conn = getConnection()) {
            if (existeNumeroEstudante(conn, estudante.getNumeroEstudante(), 0)) {
                throw new SQLException("Já existe um estudante cadastrado com este número.");
            }
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

                int idEndereco = obterOuCriarEndereco(conn, estudante.getIdEndereco());
                if (idEndereco > 0) {
                    stmt.setInt(6, idEndereco);
                } else {
                    stmt.setNull(6, Types.INTEGER);
                }
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

    public void atualizar(Estudante estudante) throws SQLException {
        try (Connection conn = getConnection()) {
            if (existeNumeroEstudante(conn, estudante.getNumeroEstudante(), estudante.getIdEstudante())) {
                throw new SQLException("Já existe outro estudante cadastrado com este número.");
            }
            int idDepartamento = obterOuCriarDepartamento(conn, estudante.getDepartamento());
            int idCurso = estudante.getIdCurso();
            int idContacto = obterOuCriarContacto(conn, estudante.getContacto());
            int idEndereco = obterOuCriarEndereco(conn, estudante.getIdEndereco());

            String sql = "UPDATE Estudante SET nome = ?, numero_estudante = ?, id_curso = ?, id_contacto = ?, email = ?, id_endereco = ? WHERE id_estudante = ?";
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
                if (idEndereco > 0) {
                    stmt.setInt(6, idEndereco);
                } else {
                    stmt.setNull(6, Types.INTEGER);
                }
                stmt.setInt(7, estudante.getIdEstudante());
                stmt.executeUpdate();
            }
        }
    }

    public List<Estudante> listarTodos() throws SQLException {
        List<Estudante> estudantes = new ArrayList<>();
        String sql = "SELECT e.id_estudante, e.nome, e.numero_estudante, c.curso, d.departamento, "
                + "e.email, "
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
                estudante.setEmail(rs.getString("email"));
                estudante.setContacto(rs.getString("contacto"));
                estudantes.add(estudante);
            }
        }
        return estudantes;
    }

    public Estudante buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT e.id_estudante, e.nome, e.numero_estudante, c.curso, d.departamento, "
                + "CONCAT(co.telefone1, IFNULL(CONCAT(' / ', co.telefone2), '')) AS contacto, "
                + "e.id_curso, e.id_contacto, e.id_endereco, e.email "
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
                    estudante.setIdCurso(rs.getInt("id_curso"));
                    estudante.setIdContacto(rs.getInt("id_contacto"));
                    estudante.setIdEndereco(rs.getInt("id_endereco"));
                    estudante.setEmail(rs.getString("email"));
                    return estudante;
                }
            }
        }
        return null;
    }

    public Estudante buscarPorId(int idEstudante) throws SQLException {
        String sql = "SELECT e.id_estudante, e.nome, e.numero_estudante, c.curso, d.departamento, "
                + "CONCAT(co.telefone1, IFNULL(CONCAT(' / ', co.telefone2), '')) AS contacto, "
                + "e.id_curso, e.id_contacto, e.id_endereco, e.email "
                + "FROM Estudante e "
                + "LEFT JOIN Curso c ON e.id_curso = c.id_curso "
                + "LEFT JOIN Departamento d ON c.id_departamento = d.id_departamento "
                + "LEFT JOIN Contacto co ON e.id_contacto = co.id_contacto "
                + "WHERE e.id_estudante = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstudante);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Estudante estudante = new Estudante();
                    estudante.setIdEstudante(rs.getInt("id_estudante"));
                    estudante.setNome(rs.getString("nome"));
                    estudante.setNumeroEstudante(rs.getString("numero_estudante"));
                    estudante.setCurso(rs.getString("curso"));
                    estudante.setDepartamento(rs.getString("departamento"));
                    estudante.setContacto(rs.getString("contacto"));
                    estudante.setIdCurso(rs.getInt("id_curso"));
                    estudante.setIdContacto(rs.getInt("id_contacto"));
                    estudante.setIdEndereco(rs.getInt("id_endereco"));
                    estudante.setEmail(rs.getString("email"));
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

    public List<EnderecoItem> listarEnderecos() throws SQLException {
        List<EnderecoItem> enderecos = new ArrayList<>();
        String sql = "SELECT id_endereco, municipio, bairro FROM Endereco ORDER BY municipio, bairro";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enderecos.add(new EnderecoItem(rs.getInt("id_endereco"), rs.getString("municipio"), rs.getString("bairro")));
            }
        }
        return enderecos;
    }

    public List<ContactoItem> listarContactos() throws SQLException {
        List<ContactoItem> contactos = new ArrayList<>();
        String sql = "SELECT id_contacto, telefone1, telefone2 FROM Contacto ORDER BY telefone1";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                contactos.add(new ContactoItem(rs.getInt("id_contacto"), rs.getString("telefone1"), rs.getString("telefone2")));
            }
        }
        return contactos;
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

    private boolean existeNumeroEstudante(Connection conn, String numero, int excludeId) throws SQLException {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT id_estudante FROM Estudante WHERE numero_estudante = ?";
        if (excludeId > 0) {
            sql += " AND id_estudante <> ?";
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numero.trim());
            if (excludeId > 0) {
                stmt.setInt(2, excludeId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int obterOuCriarEndereco(Connection conn, int idEndereco) throws SQLException {
        if (idEndereco <= 0) {
            return 0;
        }
        String selectSql = "SELECT id_endereco FROM Endereco WHERE id_endereco = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, idEndereco);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_endereco");
                }
            }
        }
        return 0;
    }

    private Connection getConnection() throws SQLException {
        return ConexaoMySQL.getConnection();
    }

    public static class EnderecoItem {
        private final int id;
        private final String municipio;
        private final String bairro;

        public EnderecoItem(int id, String municipio, String bairro) {
            this.id = id;
            this.municipio = municipio;
            this.bairro = bairro;
        }

        public int getId() {
            return id;
        }

        public String getMunicipio() {
            return municipio;
        }

        public String getBairro() {
            return bairro;
        }

        @Override
        public String toString() {
            String text = "";
            if (municipio != null && !municipio.trim().isEmpty()) {
                text += municipio.trim();
            }
            if (bairro != null && !bairro.trim().isEmpty()) {
                if (!text.isEmpty()) {
                    text += " - ";
                }
                text += bairro.trim();
            }
            return text.isEmpty() ? "Sem endereço" : text;
        }
    }

    public static class ContactoItem {
        private final int id;
        private final String telefone1;
        private final String telefone2;

        public ContactoItem(int id, String telefone1, String telefone2) {
            this.id = id;
            this.telefone1 = telefone1;
            this.telefone2 = telefone2;
        }

        public int getId() {
            return id;
        }

        public String getTelefone1() {
            return telefone1;
        }

        public String getTelefone2() {
            return telefone2;
        }

        @Override
        public String toString() {
            if (telefone2 != null && !telefone2.trim().isEmpty()) {
                return telefone1 + " / " + telefone2;
            }
            return telefone1 != null ? telefone1 : "Sem contacto";
        }
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