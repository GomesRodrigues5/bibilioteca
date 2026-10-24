package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Livro;

/**
 * DAO para Livros.
 * Gerencia operações CRUD e de indexação conforme padrões internacionais ISBD.
 */
public class LivroDAO {
    private Connection conexao;

    public LivroDAO() {
        this.conexao = ConexaoMySQL.obterConexao();
    }

    public boolean inserir(Livro livro) {
        String sql = "INSERT INTO Livro (autor, titulo, subtitulo, editora, edicao, ano_publicacao, " +
                    "isbn, issn, eissn, ndi, material, idioma, ilustracao, encadernacao, " +
                    "altura_mm, largura_mm, profundidade_mm, numero_paginas, peso_gramas, " +
                    "codigo_interno, quantidade_exemplares, quantidade_disponivel, capa_livro, " +
                    "arquivo_pdf, id_armario, sinopse, palavras_chave, ativo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, livro.getAutor());
            pstmt.setString(2, livro.getTitulo());
            pstmt.setString(3, livro.getSubtitulo());
            pstmt.setString(4, livro.getEditora());
            pstmt.setString(5, livro.getEdicao());
            pstmt.setInt(6, livro.getAnoPublicacao());
            pstmt.setString(7, livro.getIsbn());
            pstmt.setString(8, livro.getIssn());
            pstmt.setString(9, livro.getEissn());
            pstmt.setString(10, livro.getNdi());
            pstmt.setString(11, livro.getMaterial());
            pstmt.setString(12, livro.getIdioma());
            pstmt.setString(13, livro.getIlustracao());
            pstmt.setString(14, livro.getEncadernacao());
            pstmt.setInt(15, livro.getAltura_mm());
            pstmt.setInt(16, livro.getLargura_mm());
            pstmt.setInt(17, livro.getProfundidade_mm());
            pstmt.setInt(18, livro.getNumeroPaginas());
            pstmt.setInt(19, livro.getPesoGramas());
            pstmt.setString(20, livro.getCodigoInterno());
            pstmt.setInt(21, livro.getQuantidadeExemplares());
            pstmt.setInt(22, livro.getQuantidadeDisponivel());
            pstmt.setBytes(23, livro.getCapaLivro());
            pstmt.setBytes(24, livro.getArquivoPdf());
            pstmt.setObject(25, livro.getIdArmario() > 0 ? livro.getIdArmario() : null);
            pstmt.setString(26, livro.getSinopse());
            pstmt.setString(27, livro.getPalavrasChave());
            pstmt.setBoolean(28, livro.isAtivo());
            
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    livro.setIdLivro(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir livro: " + e.getMessage());
        }
        return false;
    }

    public Livro obterPorId(int idLivro) {
        String sql = "SELECT * FROM Livro WHERE id_livro = ? AND ativo = TRUE";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idLivro);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter livro: " + e.getMessage());
        }
        return null;
    }

    public Livro buscarPorId(int idLivro) {
        return obterPorId(idLivro);
    }

    public Livro obterPorCodigo(String codigoInterno) {
        String sql = "SELECT * FROM Livro WHERE codigo_interno = ? AND ativo = TRUE";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, codigoInterno);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter livro por código: " + e.getMessage());
        }
        return null;
    }

    public Livro buscarPorCodigo(String codigoInterno) {
        return obterPorCodigo(codigoInterno);
    }

    public boolean salvar(Livro livro) {
        return inserir(livro);
    }

    public boolean deletar(int idLivro) {
        return deletarLogico(idLivro);
    }

    public Livro obterPorIsbn(String isbn) {
        String sql = "SELECT * FROM Livro WHERE isbn = ? AND ativo = TRUE";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter livro por ISBN: " + e.getMessage());
        }
        return null;
    }

    public List<Livro> listarTodos() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM Livro WHERE ativo = TRUE ORDER BY titulo";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros: " + e.getMessage());
        }
        return lista;
    }

    public List<Livro> pesquisarPorTitulo(String titulo) {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM Livro WHERE MATCH(titulo, sinopse) AGAINST(? IN BOOLEAN MODE) AND ativo = TRUE";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, titulo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao pesquisar livros: " + e.getMessage());
        }
        return lista;
    }

    public List<Livro> pesquisarPorAutor(String autor) {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM Livro WHERE autor LIKE ? AND ativo = TRUE ORDER BY titulo";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, "%" + autor + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao pesquisar por autor: " + e.getMessage());
        }
        return lista;
    }

    public List<Livro> obterLivrosDisponiveis() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT * FROM Livro WHERE quantidade_disponivel > 0 AND ativo = TRUE ORDER BY titulo";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter livros disponíveis: " + e.getMessage());
        }
        return lista;
    }

    public boolean atualizar(Livro livro) {
        String sql = "UPDATE Livro SET autor = ?, titulo = ?, subtitulo = ?, editora = ?, " +
                    "edicao = ?, ano_publicacao = ?, isbn = ?, issn = ?, eissn = ?, ndi = ?, " +
                    "material = ?, idioma = ?, ilustracao = ?, encadernacao = ?, altura_mm = ?, " +
                    "largura_mm = ?, profundidade_mm = ?, numero_paginas = ?, peso_gramas = ?, " +
                    "quantidade_exemplares = ?, quantidade_disponivel = ?, capa_livro = ?, " +
                    "arquivo_pdf = ?, id_armario = ?, sinopse = ?, palavras_chave = ?, ativo = ? WHERE id_livro = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, livro.getAutor());
            pstmt.setString(2, livro.getTitulo());
            pstmt.setString(3, livro.getSubtitulo());
            pstmt.setString(4, livro.getEditora());
            pstmt.setString(5, livro.getEdicao());
            pstmt.setInt(6, livro.getAnoPublicacao());
            pstmt.setString(7, livro.getIsbn());
            pstmt.setString(8, livro.getIssn());
            pstmt.setString(9, livro.getEissn());
            pstmt.setString(10, livro.getNdi());
            pstmt.setString(11, livro.getMaterial());
            pstmt.setString(12, livro.getIdioma());
            pstmt.setString(13, livro.getIlustracao());
            pstmt.setString(14, livro.getEncadernacao());
            pstmt.setInt(15, livro.getAltura_mm());
            pstmt.setInt(16, livro.getLargura_mm());
            pstmt.setInt(17, livro.getProfundidade_mm());
            pstmt.setInt(18, livro.getNumeroPaginas());
            pstmt.setInt(19, livro.getPesoGramas());
            pstmt.setInt(20, livro.getQuantidadeExemplares());
            pstmt.setInt(21, livro.getQuantidadeDisponivel());
            pstmt.setBytes(22, livro.getCapaLivro());
            pstmt.setBytes(23, livro.getArquivoPdf());
            pstmt.setObject(24, livro.getIdArmario() > 0 ? livro.getIdArmario() : null);
            pstmt.setString(25, livro.getSinopse());
            pstmt.setString(26, livro.getPalavrasChave());
            pstmt.setBoolean(27, livro.isAtivo());
            pstmt.setInt(28, livro.getIdLivro());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar livro: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarQuantidadeDisponivel(int idLivro, int novaQuantidade) {
        String sql = "UPDATE Livro SET quantidade_disponivel = ? WHERE id_livro = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, novaQuantidade);
            pstmt.setInt(2, idLivro);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar quantidade: " + e.getMessage());
            return false;
        }
    }

    public boolean deletarLogico(int idLivro) {
        String sql = "UPDATE Livro SET ativo = FALSE WHERE id_livro = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idLivro);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar livro: " + e.getMessage());
            return false;
        }
    }

    private Livro mapearResultSet(ResultSet rs) throws SQLException {
        Livro livro = new Livro();
        livro.setIdLivro(rs.getInt("id_livro"));
        livro.setAutor(rs.getString("autor"));
        livro.setTitulo(rs.getString("titulo"));
        livro.setSubtitulo(rs.getString("subtitulo"));
        livro.setEditora(rs.getString("editora"));
        livro.setEdicao(rs.getString("edicao"));
        livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
        livro.setIsbn(rs.getString("isbn"));
        livro.setIssn(rs.getString("issn"));
        livro.setEissn(rs.getString("eissn"));
        livro.setNdi(rs.getString("ndi"));
        livro.setMaterial(rs.getString("material"));
        livro.setIdioma(rs.getString("idioma"));
        livro.setIlustracao(rs.getString("ilustracao"));
        livro.setEncadernacao(rs.getString("encadernacao"));
        livro.setAltura_mm(rs.getInt("altura_mm"));
        livro.setLargura_mm(rs.getInt("largura_mm"));
        livro.setProfundidade_mm(rs.getInt("profundidade_mm"));
        livro.setNumeroPaginas(rs.getInt("numero_paginas"));
        livro.setPesoGramas(rs.getInt("peso_gramas"));
        livro.setCodigoInterno(rs.getString("codigo_interno"));
        livro.setQuantidadeExemplares(rs.getInt("quantidade_exemplares"));
        livro.setQuantidadeDisponivel(rs.getInt("quantidade_disponivel"));
        livro.setCapaLivro(rs.getBytes("capa_livro"));
        livro.setArquivoPdf(rs.getBytes("arquivo_pdf"));
        livro.setIdArmario(rs.getInt("id_armario"));
        livro.setSinopse(rs.getString("sinopse"));
        livro.setPalavrasChave(rs.getString("palavras_chave"));
        livro.setAtivo(rs.getBoolean("ativo"));
        livro.setCriadoEm(rs.getTimestamp("criado_em"));
        livro.setModificadoEm(rs.getTimestamp("modificado_em"));
        return livro;
    }
}