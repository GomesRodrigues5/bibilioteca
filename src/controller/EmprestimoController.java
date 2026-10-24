package controller;

import dao.EstudanteDAO;
import dao.LivroDAO;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import model.Emprestimo;
import model.Estudante;
import model.Livro;

public class EmprestimoController {
    private final EstudanteDAO estudanteDAO = new EstudanteDAO();
    private final LivroDAO livroDAO = new LivroDAO();

    public void emprestarLivro(int idEstudante, int idLivro) throws SQLException {
        Estudante estudante = estudanteDAO.buscarPorNumero(String.valueOf(idEstudante));
        Livro livro = livroDAO.listarTodos().stream()
                .filter(l -> l.getIdLivro() == idLivro && l.isDisponivel()).findFirst().orElse(null);

        if (estudante != null && livro != null) {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setIdEstudante(estudante.getIdEstudante());
            emprestimo.setIdLivro(livro.getIdLivro());
            emprestimo.setDataDeEmprestimo(new Date(System.currentTimeMillis()));
            emprestimo.setHoraDeEmprestimo(new Time(System.currentTimeMillis()));
            // TODO: Salvar emprestimo no banco
            livro.setDisponivel(false);
            // TODO: Atualizar livro no banco
        }
    }

    // TODO: Adicionar métodos para devolver livro, listar empréstimos
}
