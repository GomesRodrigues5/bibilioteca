package main;

import dao.ConexaoMySQL;
import view.TelaLogin;
import util.AlertaVoz;

public class main {
    public static void main(String[] args) {
        System.out.println("Sistema de Biblioteca iniciado.");

        // Conectar ao banco em segundo plano para não bloquear a interface
        new Thread(() -> {
            dao.ConexaoMySQL.conectar();
        }, "DB-Connect").start();

        // Iniciar a interface gráfica no Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> new TelaLogin());
    }
}