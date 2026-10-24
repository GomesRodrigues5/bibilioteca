package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Emprestimo;

public class EmprestimoDAO {

    public boolean inserir(Emprestimo emprestimo) {
        String sql = "INSERT INTO Emprestimo (id_estudante, id_cabecalho, id_funcionario, data_emprestimo, hora_emprestimo, data_devolucao_prevista, hora_devolucao_prevista, status_emprestimo, numero_renovacoes, multa_aplicada, valor_multa, multa_paga) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, emprestimo.getIdEstudante());
            stmt.setInt(2, emprestimo.getIdCabecalho());
            stmt.setInt(3, emprestimo.getIdFuncionario());
            stmt.setDate(4, emprestimo.getDataEmprestimo());
            stmt.setTime(5, emprestimo.getHoraEmprestimo());
            stmt.setDate(6, emprestimo.getDataDevolutaoPrevista());
            stmt.setTime(7, emprestimo.getHoraDevolutaoPrevista());
            stmt.setString(8, emprestimo.getStatusEmprestimo());
            stmt.setInt(9, emprestimo.getNumeroRenovacoes());
            stmt.setBoolean(10, emprestimo.isMultaAplicada());
            stmt.setDouble(11, emprestimo.getValorMulta());
            stmt.setBoolean(12, emprestimo.isMultaPaga());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    emprestimo.setIdEmprestimo(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar empréstimo: " + e.getMessage());
        }
        return false;
    }

    public void salvar(Emprestimo emprestimo) {
        inserir(emprestimo);
    }

    public void atualizar(Emprestimo emprestimo) {
        String sql = "UPDATE Emprestimo SET id_estudante = ?, id_cabecalho = ?, id_funcionario = ?, data_emprestimo = ?, hora_emprestimo = ?, data_devolucao_prevista = ?, hora_devolucao_prevista = ?, data_devolucao_real = ?, hora_devolucao_real = ?, status_emprestimo = ?, numero_renovacoes = ?, multa_aplicada = ?, valor_multa = ?, multa_paga = ? WHERE id_emprestimo = ?";
        try (Connection conn = ConexaoMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emprestimo.getIdEstudante());
            stmt.setInt(2, emprestimo.getIdCabecalho());
            stmt.setInt(3, emprestimo.getIdFuncionario());
            stmt.setDate(4, emprestimo.getDataEmprestimo());
            stmt.setTime(5, emprestimo.getHoraEmprestimo());
            stmt.setDate(6, emprestimo.getDataDevolutaoPrevista());
            stmt.setTime(7, emprestimo.getHoraDevolutaoPrevista());
            stmt.setDate(8, emprestimo.getDataDevolutaoReal());
            stmt.setTime(9, emprestimo.getHoraDevolutaoReal());
            stmt.setString(10, emprestimo.getStatusEmprestimo());
            stmt.setInt(11, emprestimo.getNumeroRenovacoes());
            stmt.setBoolean(12, emprestimo.isMultaAplicada());
            stmt.setDouble(13, emprestimo.getValorMulta());
            stmt.setBoolean(14, emprestimo.isMultaPaga());
            stmt.setInt(15, emprestimo.getIdEmprestimo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar empréstimo: " + e.getMessage());
        }
    }

    public void deletar(int idEmprestimo) {
        String sql = "DELETE FROM Emprestimo WHERE id_emprestimo = ?";
        try (Connection conn = ConexaoMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmprestimo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao deletar empréstimo: " + e.getMessage());
        }
    }

    public List<Emprestimo> listarTodos() {
        List<Emprestimo> emprestimos = new ArrayList<>();
        String sql = "SELECT id_emprestimo, id_estudante, id_cabecalho, id_funcionario, data_emprestimo, hora_emprestimo, data_devolucao_prevista, hora_devolucao_prevista, data_devolucao_real, hora_devolucao_real, status_emprestimo, numero_renovacoes, multa_aplicada, valor_multa, multa_paga, data_pagamento_multa, observacoes FROM Emprestimo ORDER BY id_emprestimo DESC";
        try (Connection conn = ConexaoMySQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
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
                emprestimo.setStatusEmprestimo(rs.getString("status_emprestimo"));
                emprestimo.setNumeroRenovacoes(rs.getInt("numero_renovacoes"));
                emprestimo.setMultaAplicada(rs.getBoolean("multa_aplicada"));
                emprestimo.setValorMulta(rs.getDouble("valor_multa"));
                emprestimo.setMultaPaga(rs.getBoolean("multa_paga"));
                emprestimo.setDataPagamentoMulta(rs.getDate("data_pagamento_multa"));
                emprestimo.setObservacoes(rs.getString("observacoes"));
                emprestimo.setAcao(rs.getString("status_emprestimo"));
                emprestimos.add(emprestimo);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar empréstimos: " + e.getMessage());
        }
        return emprestimos;
    }

    public Emprestimo buscarPorId(int idEmprestimo) {
        String sql = "SELECT * FROM Emprestimo WHERE id_emprestimo = ?";
        try (Connection conn = ConexaoMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmprestimo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar empréstimo: " + e.getMessage());
        }
        return null;
    }

    public Emprestimo obterPorId(int idEmprestimo) {
        return buscarPorId(idEmprestimo);
    }

    public List<Emprestimo> obterAtivos() {
        List<Emprestimo> ativos = new ArrayList<>();
        for (Emprestimo emprestimo : listarTodos()) {
            String status = emprestimo.getStatusEmprestimo();
            if (status != null && (status.equalsIgnoreCase("Ativo") || status.equalsIgnoreCase("Atraso"))) {
                ativos.add(emprestimo);
            }
        }
        return ativos;
    }

    public boolean atualizarStatusComMulta(int idEmprestimo, String statusFinal, double valorMulta, boolean multaPaga) {
        String sql = "UPDATE Emprestimo SET status_emprestimo = ?, multa_aplicada = TRUE, valor_multa = ?, multa_paga = ? WHERE id_emprestimo = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statusFinal);
            stmt.setDouble(2, valorMulta);
            stmt.setBoolean(3, multaPaga);
            stmt.setInt(4, idEmprestimo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar status do empréstimo: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarDevolucao(int idEmprestimo, String statusFinal) {
        String sql = "UPDATE Emprestimo SET data_devolucao_real = CURDATE(), hora_devolucao_real = CURTIME(), data_hora_devolucao_real = NOW(), status_emprestimo = ? WHERE id_emprestimo = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statusFinal);
            stmt.setInt(2, idEmprestimo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao registrar devolução: " + e.getMessage());
            return false;
        }
    }

    public boolean renovarEmprestimo(int idEmprestimo, Date novaData) {
        String sql = "UPDATE Emprestimo SET data_devolucao_prevista = ?, numero_renovacoes = numero_renovacoes + 1 WHERE id_emprestimo = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, novaData);
            stmt.setInt(2, idEmprestimo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao renovar empréstimo: " + e.getMessage());
            return false;
        }
    }

    public List<Emprestimo> obterPorEstudante(int idEstudante) {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, s.nome AS estudante_nome, cb.id_livro, l.titulo AS titulo_livro FROM Emprestimo e " +
                "LEFT JOIN Estudante s ON s.id_estudante = e.id_estudante " +
                "LEFT JOIN CabecalhoLivro cb ON cb.id_cabecalho = e.id_cabecalho " +
                "LEFT JOIN Livro l ON l.id_livro = cb.id_livro " +
                "WHERE e.id_estudante = ? ORDER BY e.data_emprestimo DESC";
        try (Connection conn = ConexaoMySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstudante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Emprestimo emp = mapearResultSet(rs);
                    emp.setNomeEstudante(rs.getString("estudante_nome"));
                    emp.setTituloLivro(rs.getString("titulo_livro"));
                    lista.add(emp);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar empréstimos do estudante: " + e.getMessage());
        }
        return lista;
    }

    public List<Emprestimo> obterAtrasados() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT e.*, s.nome AS estudante_nome, cb.id_livro, l.titulo AS titulo_livro FROM Emprestimo e " +
                "LEFT JOIN Estudante s ON s.id_estudante = e.id_estudante " +
                "LEFT JOIN CabecalhoLivro cb ON cb.id_cabecalho = e.id_cabecalho " +
                "LEFT JOIN Livro l ON l.id_livro = cb.id_livro " +
                "WHERE e.status_emprestimo = 'Atraso' ORDER BY e.data_devolucao_prevista";
        try (Connection conn = ConexaoMySQL.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Emprestimo emp = mapearResultSet(rs);
                emp.setNomeEstudante(rs.getString("estudante_nome"));
                emp.setTituloLivro(rs.getString("titulo_livro"));
                lista.add(emp);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar empréstimos atrasados: " + e.getMessage());
        }
        return lista;
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
        Timestamp ts = rs.getTimestamp("data_hora_devolucao_real");
        if (ts != null) {
            emprestimo.setDataHoraDevolutaoReal(ts);
        }
        emprestimo.setStatusEmprestimo(rs.getString("status_emprestimo"));
        emprestimo.setNumeroRenovacoes(rs.getInt("numero_renovacoes"));
        emprestimo.setMultaAplicada(rs.getBoolean("multa_aplicada"));
        emprestimo.setValorMulta(rs.getDouble("valor_multa"));
        emprestimo.setMultaPaga(rs.getBoolean("multa_paga"));
        emprestimo.setDataPagamentoMulta(rs.getDate("data_pagamento_multa"));
        emprestimo.setObservacoes(rs.getString("observacoes"));
        return emprestimo;
    }

    public boolean existeEmprestimoAtivoPorLivro(int idLivro) {
        String sql = "SELECT COUNT(*) FROM Emprestimo e JOIN CabecalhoLivro cb ON cb.id_cabecalho = e.id_cabecalho WHERE cb.id_livro = ? AND e.status_emprestimo IN ('Ativo', 'Atraso')";
        try (Connection conn = ConexaoMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLivro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao verificar empréstimo ativo do livro: " + e.getMessage());
        }
        return false;
    }

    /**
     * Calcula a multa por atraso de devolução de livro
     * Multa: 200,00 KZ por dia de atraso
     * A multa só é calculada se o status NÃO for "Devolvido"
     * 
     * @param emprestimo O empréstimo a calcular a multa
     * @return O valor da multa em KZ. Retorna 0 se não há atraso ou se já foi devolvido
     */
    public double calcularMultaPorAtraso(Emprestimo emprestimo) {
        if (emprestimo == null) {
            return 0.0;
        }
        
        // Se foi devolvido, não há mais multa
        String acaoAtual = emprestimo.getAcao();
        if (acaoAtual != null && acaoAtual.equalsIgnoreCase("Devolvido")) {
            return 0.0;
        }

        if (emprestimo.getStatusEmprestimo() != null && emprestimo.getStatusEmprestimo().equalsIgnoreCase("Devolvido")) {
            return 0.0;
        }
        
        // Se não tem data de entrega, não há como calcular
        if (emprestimo.getDataEntrega() == null) {
            return 0.0;
        }
        
        java.util.Date hoje = new java.util.Date();
        java.sql.Date dataEntrega = emprestimo.getDataEntrega();
        java.util.Date dataEntregaUtil = new java.util.Date(dataEntrega.getTime());
        
        // Se a data de entrega ainda não passou, sem multa
        if (hoje.before(dataEntregaUtil)) {
            return 0.0;
        }
        
        // Calcular dias de atraso
        long diffMs = hoje.getTime() - dataEntregaUtil.getTime();
        long diasAtraso = diffMs / (1000 * 60 * 60 * 24); // Converter ms para dias
        
        // Multa: 200 KZ por dia de atraso
        double MULTA_DIARIA = 200.00;
        double multa = diasAtraso * MULTA_DIARIA;
        
        return multa;
    }

    /**
     * Atualiza o valor total (valor original + multa por atraso) de um empréstimo
     * Este método recalcula a multa e atualiza apenas o campo "valor" na base de dados
     * 
     * @param emprestimo O empréstimo a atualizar
     */
    public void atualizarValorComMulta(Emprestimo emprestimo) {
        if (emprestimo == null) {
            return;
        }
        
        double multa = calcularMultaPorAtraso(emprestimo);
        double valorTotal = emprestimo.getValor() + multa;
        
        String sql = "UPDATE Emprestimo SET valor_multa = ? WHERE id_emprestimo = ?";
        try (Connection conn = ConexaoMySQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, valorTotal);
            stmt.setInt(2, emprestimo.getIdEmprestimo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar multa do empréstimo: " + e.getMessage());
        }
    }
}
