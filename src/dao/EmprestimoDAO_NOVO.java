package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Emprestimo;

/**
 * DAO para Empréstimos.
 * Gerencia operações de empréstimo com suporte a controle de multas e renovações.
 */
public class EmprestimoDAO_NOVO {
    private Connection conexao;

    public EmprestimoDAO_NOVO() {
        this.conexao = ConexaoMySQL.obterConexao();
    }

    public boolean inserir(Emprestimo emprestimo) {
        String sql = "INSERT INTO Emprestimo (id_estudante, id_cabecalho, id_funcionario, " +
                    "data_emprestimo, hora_emprestimo, data_devolucao_prevista, " +
                    "hora_devolucao_prevista, status_emprestimo, numero_renovacoes) " +
                    "VALUES (?, ?, ?, CURDATE(), CURTIME(), ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, emprestimo.getIdEstudante());
            pstmt.setInt(2, emprestimo.getIdCabecalho());
            pstmt.setInt(3, emprestimo.getIdFuncionario());
            pstmt.setDate(4, emprestimo.getDataDevolutaoPrevista());
            pstmt.setTime(5, emprestimo.getHoraDevolutaoPrevista());
            pstmt.setString(6, emprestimo.getStatusEmprestimo());
            pstmt.setInt(7, emprestimo.getNumeroRenovacoes());
            
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    emprestimo.setIdEmprestimo(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir empréstimo: " + e.getMessage());
        }
        return false;
    }

    public Emprestimo obterPorId(int idEmprestimo) {
        String sql = "SELECT * FROM Emprestimo WHERE id_emprestimo = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idEmprestimo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter empréstimo: " + e.getMessage());
        }
        return null;
    }

    public List<Emprestimo> obterPorEstudante(int idEstudante) {
        List<Emprestimo> lista = new ArrayList<>();
        // JOIN para trazer título do livro e código interno, e nome do estudante
        String sql = "SELECT e.*, l.titulo AS livro_titulo, l.codigo_interno AS livro_codigo, s.nome AS estudante_nome " +
                     "FROM Emprestimo e " +
                     "LEFT JOIN CabecalhoLivro cb ON e.id_cabecalho = cb.id_cabecalho " +
                     "LEFT JOIN Livro l ON cb.id_livro = l.id_livro " +
                     "LEFT JOIN Estudante s ON e.id_estudante = s.id_estudante " +
                     "WHERE e.id_estudante = ? ORDER BY e.data_emprestimo DESC";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idEstudante);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Emprestimo emp = mapearResultSet(rs);
                    // preencher campos auxiliares com segurança
                    try { emp.setTituloLivro(rs.getString("livro_titulo")); } catch (SQLException ignore) {}
                    try { emp.setCodigoInternoLivro(rs.getString("livro_codigo")); } catch (SQLException ignore) {}
                    try { emp.setNomeEstudante(rs.getString("estudante_nome")); } catch (SQLException ignore) {}
                    lista.add(emp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter empréstimos do estudante: " + e.getMessage());
        }
        return lista;
    }

    public List<Emprestimo> obterAtivos() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, l.titulo AS livro_titulo, l.codigo_interno AS livro_codigo, s.nome AS estudante_nome " +
                     "FROM Emprestimo e " +
                     "LEFT JOIN CabecalhoLivro cb ON e.id_cabecalho = cb.id_cabecalho " +
                     "LEFT JOIN Livro l ON cb.id_livro = l.id_livro " +
                     "LEFT JOIN Estudante s ON e.id_estudante = s.id_estudante " +
                     "WHERE e.status_emprestimo IN ('Ativo', 'Atraso') ORDER BY e.data_devolucao_prevista";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Emprestimo emp = mapearResultSet(rs);
                try { emp.setTituloLivro(rs.getString("livro_titulo")); } catch (SQLException ignore) {}
                try { emp.setCodigoInternoLivro(rs.getString("livro_codigo")); } catch (SQLException ignore) {}
                try { emp.setNomeEstudante(rs.getString("estudante_nome")); } catch (SQLException ignore) {}
                lista.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter empréstimos ativos: " + e.getMessage());
        }
        return lista;
    }

    public List<Emprestimo> obterAtrasados() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, l.titulo AS livro_titulo, l.codigo_interno AS livro_codigo, s.nome AS estudante_nome " +
                     "FROM Emprestimo e " +
                     "LEFT JOIN CabecalhoLivro cb ON e.id_cabecalho = cb.id_cabecalho " +
                     "LEFT JOIN Livro l ON cb.id_livro = l.id_livro " +
                     "LEFT JOIN Estudante s ON e.id_estudante = s.id_estudante " +
                     "WHERE e.status_emprestimo = 'Atraso' ORDER BY e.data_devolucao_prevista";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Emprestimo emp = mapearResultSet(rs);
                try { emp.setTituloLivro(rs.getString("livro_titulo")); } catch (SQLException ignore) {}
                try { emp.setCodigoInternoLivro(rs.getString("livro_codigo")); } catch (SQLException ignore) {}
                try { emp.setNomeEstudante(rs.getString("estudante_nome")); } catch (SQLException ignore) {}
                lista.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter empréstimos atrasados: " + e.getMessage());
        }
        return lista;
    }

    public List<Emprestimo> obterPorCabecalho(int idCabecalho) {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Emprestimo WHERE id_cabecalho = ? ORDER BY data_emprestimo DESC";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idCabecalho);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter empréstimos por cabeçalho: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrarDevolucao(int idEmprestimo, String statusFinal) {
        String sql = "UPDATE Emprestimo SET data_devolucao_real = CURDATE(), " +
                    "hora_devolucao_real = CURTIME(), data_hora_devolucao_real = NOW(), " +
                    "status_emprestimo = ? WHERE id_emprestimo = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, statusFinal);
            pstmt.setInt(2, idEmprestimo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao registrar devolução: " + e.getMessage());
            return false;
        }
    }

    public boolean renovarEmprestimo(int idEmprestimo, Date novaDataDevolucao) {
        String sql = "UPDATE Emprestimo SET data_devolucao_prevista = ?, " +
                    "numero_renovacoes = numero_renovacoes + 1 WHERE id_emprestimo = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setDate(1, novaDataDevolucao);
            pstmt.setInt(2, idEmprestimo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao renovar empréstimo: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarStatusComMulta(int idEmprestimo, String novoStatus, 
                                          double valorMulta, boolean multaPaga) {
        String sql = "UPDATE Emprestimo SET status_emprestimo = ?, multa_aplicada = TRUE, " +
                    "valor_multa = ?, multa_paga = ? WHERE id_emprestimo = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, novoStatus);
            pstmt.setDouble(2, valorMulta);
            pstmt.setBoolean(3, multaPaga);
            pstmt.setInt(4, idEmprestimo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status com multa: " + e.getMessage());
            return false;
        }
    }

    public List<Emprestimo> obterComMultasPendentes() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Emprestimo WHERE multa_aplicada = TRUE AND multa_paga = FALSE " +
                    "ORDER BY data_emprestimo";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter empréstimos com multas pendentes: " + e.getMessage());
        }
        return lista;
    }

    public double obterTotalMultasPorMes(int mes, int ano) {
        String sql = "SELECT SUM(valor_multa) AS total FROM Emprestimo WHERE multa_aplicada = TRUE " +
                    "AND MONTH(data_emprestimo) = ? AND YEAR(data_emprestimo) = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, mes);
            pstmt.setInt(2, ano);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter total de multas: " + e.getMessage());
        }
        return 0;
    }

    public boolean atualizar(Emprestimo emprestimo) {
        String sql = "UPDATE Emprestimo SET id_estudante = ?, id_cabecalho = ?, " +
                    "id_funcionario = ?, data_devolucao_prevista = ?, status_emprestimo = ?, " +
                    "numero_renovacoes = ?, multa_aplicada = ?, valor_multa = ?, " +
                    "multa_paga = ?, observacoes = ? WHERE id_emprestimo = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, emprestimo.getIdEstudante());
            pstmt.setInt(2, emprestimo.getIdCabecalho());
            pstmt.setInt(3, emprestimo.getIdFuncionario());
            pstmt.setDate(4, emprestimo.getDataDevolutaoPrevista());
            pstmt.setString(5, emprestimo.getStatusEmprestimo());
            pstmt.setInt(6, emprestimo.getNumeroRenovacoes());
            pstmt.setBoolean(7, emprestimo.isMultaAplicada());
            pstmt.setDouble(8, emprestimo.getValorMulta());
            pstmt.setBoolean(9, emprestimo.isMultaPaga());
            pstmt.setString(10, emprestimo.getObservacoes());
            pstmt.setInt(11, emprestimo.getIdEmprestimo());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar empréstimo: " + e.getMessage());
            return false;
        }
    }

    private Emprestimo mapearResultSet(ResultSet rs) throws SQLException {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setIdEmprestimo(rs.getInt("id_emprestimo"));
        emprestimo.setIdEstudante(rs.getInt("id_estudante"));
        emprestimo.setIdCabecalho(rs.getInt("id_cabecalho"));
        emprestimo.setIdFuncionario(rs.getInt("id_funcionario"));
        emprestimo.setDataEmprestimo(rs.getDate("data_emprestimo"));
        emprestimo.setHoraEmprestimo(rs.getTime("hora_emprestimo"));
        emprestimo.setDataDevolutaoPrevista(rs.getDate("data_devolucao_prevista"));
        emprestimo.setHoraDevolutaoPrevista(rs.getTime("hora_devolucao_prevista"));
        emprestimo.setDataDevolutaoReal(rs.getDate("data_devolucao_real"));
        emprestimo.setHoraDevolutaoReal(rs.getTime("hora_devolucao_real"));
        emprestimo.setDataHoraDevolutaoReal(rs.getTimestamp("data_hora_devolucao_real"));
        emprestimo.setStatusEmprestimo(rs.getString("status_emprestimo"));
        emprestimo.setNumeroRenovacoes(rs.getInt("numero_renovacoes"));
        emprestimo.setMultaAplicada(rs.getBoolean("multa_aplicada"));
        emprestimo.setValorMulta(rs.getDouble("valor_multa"));
        emprestimo.setMultaPaga(rs.getBoolean("multa_paga"));
        emprestimo.setDataPagamentoMulta(rs.getDate("data_pagamento_multa"));
        emprestimo.setObservacoes(rs.getString("observacoes"));
        emprestimo.setCriadoEm(rs.getTimestamp("criado_em"));
        emprestimo.setModificadoEm(rs.getTimestamp("modificado_em"));
        return emprestimo;
    }
}
