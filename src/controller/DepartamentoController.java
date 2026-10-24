package controller;

import dao.DepartamentoDAO;
import java.sql.SQLException;
import java.util.List;
import model.Departamento;

public class DepartamentoController {
    private DepartamentoDAO departamentoDAO = new DepartamentoDAO();

    public void salvarDepartamento(Departamento departamento) throws SQLException {
        departamentoDAO.salvar(departamento);
    }

    public void atualizarDepartamento(Departamento departamento) throws SQLException {
        departamentoDAO.atualizar(departamento);
    }

    public void deletarDepartamento(int idDepartamento) throws SQLException {
        departamentoDAO.deletar(idDepartamento);
    }

    public List<Departamento> listarDepartamentos() throws SQLException {
        return departamentoDAO.listarTodos();
    }

    public Departamento buscarPorNome(String nome) throws SQLException {
        return departamentoDAO.buscarPorNome(nome);
    }
}
