package controller;

import dao.ArmarioDAO;
import java.sql.SQLException;
import java.util.List;
import model.Armario;

public class ArmarioController {
    private ArmarioDAO armarioDAO = new ArmarioDAO();

    public void salvarArmario(Armario armario) throws SQLException {
        armarioDAO.salvar(armario);
    }

    public void atualizarArmario(Armario armario) throws SQLException {
        armarioDAO.atualizar(armario);
    }

    public void deletarArmario(int idArmario) throws SQLException {
        armarioDAO.deletar(idArmario);
    }

    public List<Armario> listarArmarios() throws SQLException {
        return armarioDAO.listarTodos();
    }

    public Armario buscarPorLocalizacao(String localizacao) throws SQLException {
        return armarioDAO.buscarPorLocalizacao(localizacao);
    }
}
