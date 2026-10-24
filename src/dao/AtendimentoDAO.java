package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Atendimento;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AtendimentoDAO {
    public void salvar(Atendimento atendimento) throws SQLException, IOException, InterruptedException {
        String sql = "INSERT INTO Atendimento (id_estudante, id_computador, data_de_entrada, hora_de_entrada, hora_de_saida, tempo_limite_minutos, acao) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, atendimento.getIdEstudante());
            stmt.setInt(2, atendimento.getIdComputador());
            stmt.setDate(3, atendimento.getDataDeEntrada());
            stmt.setTime(4, atendimento.getHoraDeEntrada());
            stmt.setTime(5, atendimento.getHoraDeSaida());
            stmt.setInt(6, atendimento.getTempoLimiteMinutos());
            stmt.setString(7, atendimento.getAcao());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {

                    atendimento.setIdAtendimento(rs.getInt(1));
                  
                }
            }
        }
    }

    public void atualizar(Atendimento atendimento) throws SQLException {
        String sql = "UPDATE Atendimento SET id_estudante = ?, id_computador = ?, data_de_entrada = ?, hora_de_entrada = ?, hora_de_saida = ?, tempo_limite_minutos = ?, acao = ? WHERE id_atendimento = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, atendimento.getIdEstudante());
            stmt.setInt(2, atendimento.getIdComputador());
            stmt.setDate(3, atendimento.getDataDeEntrada());
            stmt.setTime(4, atendimento.getHoraDeEntrada());
            stmt.setTime(5, atendimento.getHoraDeSaida());
            stmt.setInt(6, atendimento.getTempoLimiteMinutos());
            stmt.setString(7, atendimento.getAcao());
            stmt.setInt(8, atendimento.getIdAtendimento());
            stmt.executeUpdate();
        }
    }

    public void deletar(int idAtendimento) throws SQLException {
        String sql = "DELETE FROM Atendimento WHERE id_atendimento = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAtendimento);
            stmt.executeUpdate();
        }
    }

    public List<Atendimento> listarTodos() throws SQLException {
        List<Atendimento> atendimentos = new ArrayList<>();
        String sql = "SELECT id_atendimento, id_estudante, id_computador, data_de_entrada, hora_de_entrada, hora_de_saida, tempo_limite_minutos, acao FROM Atendimento ORDER BY id_atendimento";
        try (Connection conn = ConexaoMySQL.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Atendimento atendimento = new Atendimento();
                atendimento.setIdAtendimento(rs.getInt("id_atendimento"));
                atendimento.setIdEstudante(rs.getInt("id_estudante"));
                atendimento.setIdComputador(rs.getInt("id_computador"));
                atendimento.setDataDeEntrada(rs.getDate("data_de_entrada"));
                atendimento.setHoraDeEntrada(rs.getTime("hora_de_entrada"));
                atendimento.setHoraDeSaida(rs.getTime("hora_de_saida"));
                atendimento.setTempoLimiteMinutos(rs.getInt("tempo_limite_minutos"));
                atendimento.setAcao(rs.getString("acao"));
                atendimentos.add(atendimento);
            }
        }
        return atendimentos;
    }

    public List<Atendimento> listarEmUso() throws SQLException {
        List<Atendimento> atendimentos = new ArrayList<>();
        String sql = "SELECT id_atendimento, id_estudante, id_computador, data_de_entrada, hora_de_entrada, hora_de_saida, tempo_limite_minutos, acao FROM Atendimento WHERE acao = 'Em Uso' ORDER BY hora_de_entrada DESC";
        try (Connection conn = ConexaoMySQL.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Atendimento atendimento = new Atendimento();
                atendimento.setIdAtendimento(rs.getInt("id_atendimento"));
                atendimento.setIdEstudante(rs.getInt("id_estudante"));
                atendimento.setIdComputador(rs.getInt("id_computador"));
                atendimento.setDataDeEntrada(rs.getDate("data_de_entrada"));
                atendimento.setHoraDeEntrada(rs.getTime("hora_de_entrada"));
                atendimento.setHoraDeSaida(rs.getTime("hora_de_saida"));
                atendimento.setTempoLimiteMinutos(rs.getInt("tempo_limite_minutos"));
                atendimento.setAcao(rs.getString("acao"));
                atendimentos.add(atendimento);
            }
        }
        return atendimentos;
    }

    public List<Atendimento> listarPorEstudante(int idEstudante) throws SQLException {
        List<Atendimento> atendimentos = new ArrayList<>();
        String sql = "SELECT id_atendimento, id_estudante, id_computador, data_de_entrada, hora_de_entrada, hora_de_saida, tempo_limite_minutos, acao FROM Atendimento WHERE id_estudante = ? ORDER BY data_de_entrada DESC, hora_de_entrada DESC";
        try (Connection conn = ConexaoMySQL.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstudante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Atendimento atendimento = new Atendimento();
                    atendimento.setIdAtendimento(rs.getInt("id_atendimento"));
                    atendimento.setIdEstudante(rs.getInt("id_estudante"));
                    atendimento.setIdComputador(rs.getInt("id_computador"));
                    atendimento.setDataDeEntrada(rs.getDate("data_de_entrada"));
                    atendimento.setHoraDeEntrada(rs.getTime("hora_de_entrada"));
                    atendimento.setHoraDeSaida(rs.getTime("hora_de_saida"));
                    atendimento.setTempoLimiteMinutos(rs.getInt("tempo_limite_minutos"));
                    atendimento.setAcao(rs.getString("acao"));
                    atendimentos.add(atendimento);
                }
            }
        }
        return atendimentos;
    }

    public Atendimento buscarPorId(int idAtendimento) throws SQLException {
        String sql = "SELECT id_atendimento, id_estudante, id_computador, data_de_entrada, hora_de_entrada, hora_de_saida, tempo_limite_minutos, acao FROM Atendimento WHERE id_atendimento = ?";
        try (Connection conn = ConexaoMySQL.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAtendimento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Atendimento atendimento = new Atendimento();
                    atendimento.setIdAtendimento(rs.getInt("id_atendimento"));
                    atendimento.setIdEstudante(rs.getInt("id_estudante"));
                    atendimento.setIdComputador(rs.getInt("id_computador"));
                    atendimento.setDataDeEntrada(rs.getDate("data_de_entrada"));
                    atendimento.setHoraDeEntrada(rs.getTime("hora_de_entrada"));
                    atendimento.setHoraDeSaida(rs.getTime("hora_de_saida"));
                    atendimento.setTempoLimiteMinutos(rs.getInt("tempo_limite_minutos"));
                    atendimento.setAcao(rs.getString("acao"));
                    return atendimento;
                }
            }
        }
        return null;
    }
}