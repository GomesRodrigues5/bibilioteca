package controller;

import dao.CursoDAO;
import java.sql.SQLException;
import java.util.List;
import model.Curso;

public class CursoController {
    private CursoDAO cursoDAO = new CursoDAO();

    public void salvarCurso(Curso curso) throws SQLException {
        cursoDAO.salvar(curso);
    }

    public void atualizarCurso(Curso curso) throws SQLException {
        cursoDAO.atualizar(curso);
    }

    public void deletarCurso(int idCurso) throws SQLException {
        cursoDAO.deletar(idCurso);
    }

    public List<Curso> listarCursos() throws SQLException {
        return cursoDAO.listarTodos();
    }

    public Curso buscarPorNome(String nome) throws SQLException {
        return cursoDAO.buscarPorNome(nome);
    }
}
