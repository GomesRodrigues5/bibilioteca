package controller;

import dao.ComputadorDAO;
import java.sql.SQLException;
import java.util.List;
import model.Computador;

public class ComputadorController {
    private ComputadorDAO computadorDAO = new ComputadorDAO();

    public void salvarComputador(Computador computador) throws SQLException {
        computadorDAO.salvar(computador);
    }

    public void atualizarComputador(Computador computador) throws SQLException {
        computadorDAO.atualizar(computador);
    }

    public void deletarComputador(int idComputador) throws SQLException {
        computadorDAO.deletar(idComputador);
    }

    public List<Computador> listarComputadores() throws SQLException {
        return computadorDAO.listarTodos();
    }

    public Computador buscarPorTag(String tag) throws SQLException {
        return computadorDAO.buscarPorTag(tag);
    }
}
