package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Reservas de Livros.
 * Gerencia operações de reserva de livros pelos estudantes.
 */
public class ReservaDAO {
    private Connection conexao;

    public ReservaDAO() {
        this.conexao = ConexaoMySQL.obterConexao();
    }

    public boolean inserir(model.Reserva reserva) {
        String sql = "INSERT INTO Reserva (id_estudante, id_cabecalho, id_funcionario, " +
                    "data_reserva, hora_reserva, data_retirada_prevista, hora_retirada_prevista, " +
                    "dias_reserva, status_reserva) " +
                    "VALUES (?, ?, ?, CURDATE(), CURTIME(), ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, reserva.getIdEstudante());
            pstmt.setInt(2, reserva.getIdCabecalho());
            pstmt.setInt(3, reserva.getIdFuncionario());
            pstmt.setDate(4, reserva.getDataRetiradadaPrevista());
            pstmt.setTime(5, reserva.getHoraRetiradadaPrevista());
            pstmt.setInt(6, reserva.getDiasReserva());
            pstmt.setString(7, reserva.getStatusReserva());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir Reserva: " + e.getMessage());
            return false;
        }
    }

    public model.Reserva obterPorId(int idReserva) {
        String sql = "SELECT * FROM Reserva WHERE id_reserva = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idReserva);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter Reserva: " + e.getMessage());
        }
        return null;
    }

    public List<model.Reserva> obterPorEstudante(int idEstudante) {
        List<model.Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE id_estudante = ? ORDER BY data_reserva DESC";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idEstudante);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter reservas do estudante: " + e.getMessage());
        }
        return lista;
    }

    public List<model.Reserva> obterPorCabecalho(int idCabecalho) {
        List<model.Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE id_cabecalho = ? AND status_reserva != 'Cancelado' ORDER BY data_reserva";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idCabecalho);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter reservas por cabeçalho: " + e.getMessage());
        }
        return lista;
    }

    public List<model.Reserva> obterReservasAtivas() {
        List<model.Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE status_reserva = 'Reservado' ORDER BY data_retirada_prevista";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter reservas ativas: " + e.getMessage());
        }
        return lista;
    }

    public boolean atualizarStatus(int idReserva, String novoStatus) {
        String sql = "UPDATE Reserva SET status_reserva = ? WHERE id_reserva = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, novoStatus);
            pstmt.setInt(2, idReserva);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status da reserva: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarRetirada(int idReserva) {
        String sql = "UPDATE Reserva SET data_retirada_real = CURDATE(), " +
                    "hora_retirada_real = CURTIME(), status_reserva = 'Retirado' WHERE id_reserva = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setInt(1, idReserva);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao registrar retirada: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelar(int idReserva, String motivo) {
        String sql = "UPDATE Reserva SET status_reserva = 'Cancelado', observacoes = ? WHERE id_reserva = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, motivo);
            pstmt.setInt(2, idReserva);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao cancelar reserva: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizar(model.Reserva reserva) {
        String sql = "UPDATE Reserva SET data_retirada_prevista = ?, hora_retirada_prevista = ?, " +
                    "dias_reserva = ?, observacoes = ? WHERE id_reserva = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setDate(1, reserva.getDataRetiradadaPrevista());
            pstmt.setTime(2, reserva.getHoraRetiradadaPrevista());
            pstmt.setInt(3, reserva.getDiasReserva());
            pstmt.setString(4, reserva.getObservacoes());
            pstmt.setInt(5, reserva.getIdReserva());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar Reserva: " + e.getMessage());
            return false;
        }
    }

    private model.Reserva mapearResultSet(ResultSet rs) throws SQLException {
        model.Reserva reserva = new model.Reserva();
        reserva.setIdReserva(rs.getInt("id_reserva"));
        reserva.setIdEstudante(rs.getInt("id_estudante"));
        reserva.setIdCabecalho(rs.getInt("id_cabecalho"));
        reserva.setIdFuncionario(rs.getInt("id_funcionario"));
        reserva.setDataReserva(rs.getDate("data_reserva"));
        reserva.setHoraReserva(rs.getTime("hora_reserva"));
        reserva.setDataRetiradadaPrevista(rs.getDate("data_retirada_prevista"));
        reserva.setHoraRetiradadaPrevista(rs.getTime("hora_retirada_prevista"));
        reserva.setDataRetiradadaReal(rs.getDate("data_retirada_real"));
        reserva.setHoraRetiradadaReal(rs.getTime("hora_retirada_real"));
        reserva.setStatusReserva(rs.getString("status_reserva"));
        reserva.setDiasReserva(rs.getInt("dias_reserva"));
        reserva.setObservacoes(rs.getString("observacoes"));
        reserva.setCriadoEm(rs.getTimestamp("criado_em"));
        reserva.setModificadoEm(rs.getTimestamp("modificado_em"));
        return reserva;
    }
}
