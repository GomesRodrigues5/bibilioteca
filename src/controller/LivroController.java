package controller;

import dao.LivroDAO;
import java.sql.SQLException;
import java.util.List;
import model.Livro;

public class LivroController {
    private LivroDAO livroDAO = new LivroDAO();

    public void salvarLivro(Livro livro) throws SQLException {
        livroDAO.salvar(livro);
    }

    public void atualizarLivro(Livro livro) throws SQLException {
        livroDAO.atualizar(livro);
    }

    public void deletarLivro(int idLivro) throws SQLException {
        livroDAO.deletar(idLivro);
    }

    public List<Livro> listarLivros() throws SQLException {
        return livroDAO.listarTodos();
    }

    public Livro buscarPorTitulo(String titulo) throws SQLException {
        return livroDAO.buscarPorTitulo(titulo);
    }
}
