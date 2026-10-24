package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Controle de Multas.
 * Gerencia o cálculo, registro e pagamento de multas por atraso.
 */
public class ControleMultaDAO {
    private Connection conexao;

    public ControleMultaDAO() {
        this.conexao = ConexaoMySQL.obterConexao();
    }

    public boolean inserir(model.ControleMulta multa) {
        String sql = "INSERT INTO ControleMulta (id_emprestimo, id_estudante, valor_multa_diaria, " +
                    "numero_dias_atraso, valor_total_multa, data_calculo, status_pagamento) " +
                    "VALUES (?, ?, ?, ?, ?, CURDATE(), ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, multa.getIdEmprestimo());
            pstmt.setInt(2, multa.getIdEstudante());
            pstmt.setDouble(3, multa.getValorMultaDiaria());
            pstmt.setInt(4, multa.getNumeroDiasAtraso());
            pstmt.setDouble(5, multa.getValorTotalMulta());
            pstmt.setString(6, multa.getStatusPagamento());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir ControleMulta: " + e.getMessage());
            return false;
        }
    }

    public model.ControleMulta obterPorId(int idMulta) {
        String sql = "SELECT * FROM ControleMulta WHERE id_multa = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idMulta);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter ControleMulta: " + e.getMessage());
        }
        return null;
    }

    public List<model.ControleMulta> obterPorEstudante(int idEstudante) {
        List<model.ControleMulta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ControleMulta WHERE id_estudante = ? ORDER BY data_calculo DESC";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idEstudante);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter multas do estudante: " + e.getMessage());
        }
        return lista;
    }

    public List<model.ControleMulta> obterMultasPendentes() {
        List<model.ControleMulta> lista = new ArrayList<>();
        String sql = "SELECT * FROM ControleMulta WHERE status_pagamento = 'Pendente' OR status_pagamento = 'Parcialmente Pago' ORDER BY data_calculo";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter multas pendentes: " + e.getMessage());
        }
        return lista;
    }

    public double obterTotalMultasPorMes(int mes, int ano) {
        String sql = "SELECT SUM(valor_total_multa) AS total FROM ControleMulta " +
                    "WHERE MONTH(data_calculo) = ? AND YEAR(data_calculo) = ?";
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

    public boolean registrarPagamento(int idMulta, double valorPago, String metodoPagamento, String numeroRecibo) {
        String sql = "UPDATE ControleMulta SET data_pagamento = CURDATE(), valor_pago = ?, " +
                    "status_pagamento = CASE WHEN valor_pago + ? >= valor_total_multa THEN 'Pago' ELSE 'Parcialmente Pago' END, " +
                    "metodo_pagamento = ?, numero_recibo = ? WHERE id_multa = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setDouble(1, valorPago);
            pstmt.setDouble(2, valorPago);
            pstmt.setString(3, metodoPagamento);
            pstmt.setString(4, numeroRecibo);
            pstmt.setInt(5, idMulta);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao registrar pagamento: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(model.ControleMulta multa) {
        String sql = "UPDATE ControleMulta SET numero_dias_atraso = ?, valor_total_multa = ?, " +
                    "status_pagamento = ?, observacoes = ? WHERE id_multa = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, multa.getNumeroDiasAtraso());
            pstmt.setDouble(2, multa.getValorTotalMulta());
            pstmt.setString(3, multa.getStatusPagamento());
            pstmt.setString(4, multa.getObservacoes());
            pstmt.setInt(5, multa.getIdMulta());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar ControleMulta: " + e.getMessage());
            return false;
        }
    }

    private model.ControleMulta mapearResultSet(ResultSet rs) throws SQLException {
        model.ControleMulta multa = new model.ControleMulta();
        multa.setIdMulta(rs.getInt("id_multa"));
        multa.setIdEmprestimo(rs.getInt("id_emprestimo"));
        multa.setIdEstudante(rs.getInt("id_estudante"));
        multa.setValorMultaDiaria(rs.getDouble("valor_multa_diaria"));
        multa.setNumeroDiasAtraso(rs.getInt("numero_dias_atraso"));
        multa.setValorTotalMulta(rs.getDouble("valor_total_multa"));
        multa.setDataCalculo(rs.getDate("data_calculo"));
        multa.setStatusPagamento(rs.getString("status_pagamento"));
        multa.setDataPagamento(rs.getDate("data_pagamento"));
        multa.setValorPago(rs.getDouble("valor_pago"));
        multa.setMetodoPagamento(rs.getString("metodo_pagamento"));
        multa.setNumeroRecibo(rs.getString("numero_recibo"));
        multa.setObservacoes(rs.getString("observacoes"));
        multa.setCriadoEm(rs.getTimestamp("criado_em"));
        multa.setModificadoEm(rs.getTimestamp("modificado_em"));
        return multa;
    }
}
