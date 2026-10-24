package controller;

import dao.EstudanteDAO;
import java.sql.SQLException;
import java.util.List;
import model.Estudante;

public class EstudanteController {
    private EstudanteDAO estudanteDAO = new EstudanteDAO();

    public void salvarEstudante(Estudante estudante) throws SQLException {
        estudanteDAO.salvar(estudante);
    }

    public void deletarEstudante(int idEstudante) throws SQLException {
        estudanteDAO.deletar(idEstudante);
    }

    public List<Estudante> listarEstudantes() throws SQLException {
        return estudanteDAO.listarTodos();
    }

    public List<EstudanteDAO.CursoItem> listarCursos() throws SQLException {
        return estudanteDAO.listarCursos();
    }

    public Estudante buscarPorNumero(String numero) {
        try {
            return estudanteDAO.buscarPorNumero(numero);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Estudante buscarPorId(int idEstudante) {
        try {
            return estudanteDAO.buscarPorId(idEstudante);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void atualizarEstudante(Estudante estudante) throws SQLException {
        estudanteDAO.atualizar(estudante);
    }

    public List<EstudanteDAO.EnderecoItem> listarEnderecos() throws SQLException {
        return estudanteDAO.listarEnderecos();
    }

    public List<EstudanteDAO.ContactoItem> listarContactos() throws SQLException {
        return estudanteDAO.listarContactos();
    }
}