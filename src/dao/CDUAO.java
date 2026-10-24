package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a Classificação Decimal Universal (CDU).
 * Gerencia operações de leitura e escrita da tabela CDU no banco de dados.
 */
public class CDUAO {
    private Connection conexao;

    public CDUAO() {
        this.conexao = ConexaoMySQL.obterConexao();
    }

    public boolean inserir(model.CDU cdu) {
        String sql = "INSERT INTO CDU (codigo_cdu, descricao, classe_pai_id, tipo_tabela, ativo) " +
                    "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cdu.getCodigoCdu());
            pstmt.setString(2, cdu.getDescricao());
            pstmt.setObject(3, cdu.getClassePaiId());
            pstmt.setString(4, cdu.getTipoTabela());
            pstmt.setBoolean(5, cdu.isAtivo());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir CDU: " + e.getMessage());
            return false;
        }
    }

    public model.CDU obterPorId(int idCdu) {
        String sql = "SELECT * FROM CDU WHERE id_cdu = ? AND ativo = TRUE";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idCdu);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter CDU: " + e.getMessage());
        }
        return null;
    }

    public model.CDU obterPorCodigo(String codigoCdu) {
        String sql = "SELECT * FROM CDU WHERE codigo_cdu = ? AND ativo = TRUE";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, codigoCdu);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter CDU por código: " + e.getMessage());
        }
        return null;
    }

    public List<model.CDU> obterTodas() {
        List<model.CDU> lista = new ArrayList<>();
        String sql = "SELECT * FROM CDU WHERE ativo = TRUE ORDER BY codigo_cdu";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter todas CDUs: " + e.getMessage());
        }
        return lista;
    }

    public List<model.CDU> obterPorClassePai(Integer classePaiId) {
        List<model.CDU> lista = new ArrayList<>();
        String sql = "SELECT * FROM CDU WHERE classe_pai_id = ? AND ativo = TRUE ORDER BY codigo_cdu";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setObject(1, classePaiId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter CDUs por classe pai: " + e.getMessage());
        }
        return lista;
    }

    public boolean atualizar(model.CDU cdu) {
        String sql = "UPDATE CDU SET descricao = ?, classe_pai_id = ?, tipo_tabela = ?, ativo = ? " +
                    "WHERE id_cdu = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cdu.getDescricao());
            pstmt.setObject(2, cdu.getClassePaiId());
            pstmt.setString(3, cdu.getTipoTabela());
            pstmt.setBoolean(4, cdu.isAtivo());
            pstmt.setInt(5, cdu.getIdCdu());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar CDU: " + e.getMessage());
            return false;
        }
    }

    public boolean deletar(int idCdu) {
        String sql = "UPDATE CDU SET ativo = FALSE WHERE id_cdu = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idCdu);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar CDU: " + e.getMessage());
            return false;
        }
    }

    private model.CDU mapearResultSet(ResultSet rs) throws SQLException {
        model.CDU cdu = new model.CDU();
        cdu.setIdCdu(rs.getInt("id_cdu"));
        cdu.setCodigoCdu(rs.getString("codigo_cdu"));
        cdu.setDescricao(rs.getString("descricao"));
        cdu.setClassePaiId((Integer) rs.getObject("classe_pai_id"));
        cdu.setTipoTabela(rs.getString("tipo_tabela"));
        cdu.setAtivo(rs.getBoolean("ativo"));
        cdu.setCriadoEm(rs.getTimestamp("criado_em"));
        cdu.setModificadoEm(rs.getTimestamp("modificado_em"));
        return cdu;
    }
}
