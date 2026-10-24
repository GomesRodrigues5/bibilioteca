package util;

public class Validador {
    public static boolean validarEmail(String email) {
        return email != null && email.contains("@");
    }

    public static boolean validarMatricula(String matricula) {
        return matricula != null && matricula.length() >= 5;
    }

    public static boolean validarTelefone(String telefone) {
        return telefone != null && telefone.length() >= 10;
    }
}