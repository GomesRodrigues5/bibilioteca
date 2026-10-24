package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Contacto;

public class ContactoDAO {

    public void salvar(Contacto contacto) throws SQLException {
        try (Connection conn = ConexaoMySQL.getConnection()) {
            if (existeContacto(conn, contacto.getTelefone1(), contacto.getTelefone2(), 0)) {
                throw new SQLException("Já existe um contacto cadastrado com este telefone.");
            }
            String sql = "INSERT INTO Contacto (telefone1, telefone2) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, contacto.getTelefone1());
                stmt.setString(2, contacto.getTelefone2());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        contacto.setIdContacto(rs.getInt(1));
                    }
                }
            }
        }
    }

    public void atualizar(Contacto contacto) throws SQLException {
        try (Connection conn = ConexaoMySQL.getConnection()) {
            if (existeContacto(conn, contacto.getTelefone1(), contacto.getTelefone2(), contacto.getIdContacto())) {
                throw new SQLException("Já existe outro contacto cadastrado com este telefone.");
            }
            String sql = "UPDATE Contacto SET telefone1 = ?, telefone2 = ? WHERE id_contacto = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, contacto.getTelefone1());
                stmt.setString(2, contacto.getTelefone2());
                stmt.setInt(3, contacto.getIdContacto());
                stmt.executeUpdate();
            }
        }
    }

    private boolean existeContacto(Connection conn, String telefone1, String telefone2, int excludeId) throws SQLException {
        if ((telefone1 == null || telefone1.trim().isEmpty()) && (telefone2 == null || telefone2.trim().isEmpty())) {
            return false;
        }
        String sql = "SELECT id_contacto FROM Contacto WHERE (telefone1 = ? OR telefone2 = ? OR telefone1 = ? OR telefone2 = ?)";
        if (excludeId > 0) {
            sql += " AND id_contacto <> ?";
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefone1 == null ? "" : telefone1.trim());
            stmt.setString(2, telefone1 == null ? "" : telefone1.trim());
            stmt.setString(3, telefone2 == null ? "" : telefone2.trim());
            stmt.setString(4, telefone2 == null ? "" : telefone2.trim());
            if (excludeId > 0) {
                stmt.setInt(5, excludeId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void deletar(int idContacto) throws SQLException {
        String sql = "DELETE FROM Contacto WHERE id_contacto = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idContacto);
            stmt.executeUpdate();
        }
    }

    public List<Contacto> listarTodos() throws SQLException {
        List<Contacto> contactos = new ArrayList<>();
        String sql = "SELECT id_contacto, telefone1, telefone2 FROM Contacto ORDER BY telefone1";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Contacto contacto = new Contacto();
                contacto.setIdContacto(rs.getInt("id_contacto"));
                contacto.setTelefone1(rs.getString("telefone1"));
                contacto.setTelefone2(rs.getString("telefone2"));
                contactos.add(contacto);
            }
        }
        return contactos;
    }

    public Contacto buscarPorId(int idContacto) throws SQLException {
        String sql = "SELECT id_contacto, telefone1, telefone2 FROM Contacto WHERE id_contacto = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idContacto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Contacto contacto = new Contacto();
                    contacto.setIdContacto(rs.getInt("id_contacto"));
                    contacto.setTelefone1(rs.getString("telefone1"));
                    contacto.setTelefone2(rs.getString("telefone2"));
                    return contacto;
                }
            }
        }
        return null;
    }
}