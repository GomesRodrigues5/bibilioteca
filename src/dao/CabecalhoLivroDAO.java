package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CabecalhoLivro;

public class CabecalhoLivroDAO {
    public CabecalhoLivro buscarPorLivro(int idLivro) throws SQLException {
        String sql = "SELECT * FROM CabecalhoLivro WHERE id_livro = ? LIMIT 1";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
    }

    public CabecalhoLivro obterPorId(int idCabecalho) throws SQLException {
        String sql = "SELECT * FROM CabecalhoLivro WHERE id_cabecalho = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCabecalho);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapearResultSet(rs) : null;
            }
        }
    }

    public List<CabecalhoLivro> obterPorIdLivro(int idLivro) throws SQLException {
        return listarPorLivro(idLivro);
    }

    public CabecalhoLivro buscarPorLivroUnico(int idLivro) throws SQLException {
        return buscarPorLivro(idLivro);
    }

    public List<CabecalhoLivro> listarPorLivro(int idLivro) throws SQLException {
        List<CabecalhoLivro> lista = new ArrayList<>();
        String sql = "SELECT * FROM CabecalhoLivro WHERE id_livro = ? ORDER BY numero_capitulo";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }

    public boolean inserir(CabecalhoLivro cabecalho) {
        try {
            salvar(cabecalho);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cabeçalho: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(CabecalhoLivro cabecalho) {
        try {
            atualizarDados(cabecalho);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cabeçalho: " + e.getMessage());
            return false;
        }
    }

    private CabecalhoLivro mapearResultSet(ResultSet rs) throws SQLException {
        CabecalhoLivro cabecalho = new CabecalhoLivro();
        cabecalho.setIdCabecalho(rs.getInt("id_cabecalho"));
        cabecalho.setIdLivro(rs.getInt("id_livro"));
        cabecalho.setIdCdu(rs.getInt("id_cdu"));
        cabecalho.setIndexacaoDescritiva(rs.getString("indexacao_descritiva"));
        cabecalho.setQuantidadeExemplares(rs.getInt("quantidade_exemplares"));
        cabecalho.setNumeroCapitulo(rs.getInt("numero_capitulo"));
        cabecalho.setTituloCapitulo(rs.getString("titulo_capitulo"));
        cabecalho.setPaginaInicio(rs.getInt("pagina_inicio"));
        cabecalho.setPaginaFim(rs.getInt("pagina_fim"));
        cabecalho.setNumeroChamada(rs.getString("numero_chamada"));
        cabecalho.setPalavrasChaveCapitulo(rs.getString("palavras_chave_capitulo"));
        cabecalho.setCriadoEm(rs.getTimestamp("criado_em"));
        cabecalho.setModificadoEm(rs.getTimestamp("modificado_em"));
        return cabecalho;
    }

    public void salvar(CabecalhoLivro cabecalho) throws SQLException {
        String sql = "INSERT INTO CabecalhoLivro (id_livro, id_cdu, indexacao_descritiva, quantidade_exemplares, numero_capitulo, titulo_capitulo, pagina_inicio, pagina_fim, numero_chamada, palavras_chave_capitulo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cabecalho.getIdLivro());
            stmt.setInt(2, cabecalho.getIdCdu());
            stmt.setString(3, cabecalho.getIndexacaoDescritiva());
            stmt.setInt(4, cabecalho.getQuantidadeExemplares());
            stmt.setInt(5, cabecalho.getNumeroCapitulo());
            stmt.setString(6, cabecalho.getTituloCapitulo());
            stmt.setObject(7, cabecalho.getPaginaInicio() > 0 ? cabecalho.getPaginaInicio() : null);
            stmt.setObject(8, cabecalho.getPaginaFim() > 0 ? cabecalho.getPaginaFim() : null);
            stmt.setString(9, cabecalho.getNumeroChamada());
            stmt.setString(10, cabecalho.getPalavrasChaveCapitulo());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cabecalho.setIdCabecalho(rs.getInt(1));
                }
            }
        }
    }

    private void atualizarDados(CabecalhoLivro cabecalho) throws SQLException {
        String sql = "UPDATE CabecalhoLivro SET id_cdu = ?, indexacao_descritiva = ?, quantidade_exemplares = ?, numero_capitulo = ?, titulo_capitulo = ?, pagina_inicio = ?, pagina_fim = ?, numero_chamada = ?, palavras_chave_capitulo = ? WHERE id_cabecalho = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cabecalho.getIdCdu());
            stmt.setString(2, cabecalho.getIndexacaoDescritiva());
            stmt.setInt(3, cabecalho.getQuantidadeExemplares());
            stmt.setInt(4, cabecalho.getNumeroCapitulo());
            stmt.setString(5, cabecalho.getTituloCapitulo());
            stmt.setObject(6, cabecalho.getPaginaInicio() > 0 ? cabecalho.getPaginaInicio() : null);
            stmt.setObject(7, cabecalho.getPaginaFim() > 0 ? cabecalho.getPaginaFim() : null);
            stmt.setString(8, cabecalho.getNumeroChamada());
            stmt.setString(9, cabecalho.getPalavrasChaveCapitulo());
            stmt.setInt(10, cabecalho.getIdCabecalho());
            stmt.executeUpdate();
        }
    }

    public void salvarOuAtualizar(CabecalhoLivro cabecalho) throws SQLException {
        if (cabecalho == null || cabecalho.getIdLivro() <= 0) {
            return;
        }
        CabecalhoLivro existente = buscarPorLivro(cabecalho.getIdLivro());
        if (existente == null) {
            salvar(cabecalho);
        } else {
            cabecalho.setIdCabecalho(existente.getIdCabecalho());
            atualizarDados(cabecalho);
        }
    }
}
