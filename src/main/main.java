package main;

import dao.ConexaoMySQL;
import view.TelaLogin;
import util.AlertaVoz;

public class main {
    public static void main(String[] args) {
        System.out.println("Sistema de Biblioteca iniciado.");
        // Iniciar com tela de login
        new TelaLogin();
        dao.ConexaoMySQL.conectar();
     
       
    }
}