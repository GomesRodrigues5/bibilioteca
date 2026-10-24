package util;

import java.util.regex.Pattern;

/**
 * Classe de validação para campos de formulário conforme tipos de dados.
 * Segue as normas internacionais de validação de dados.
 */
public class ValidadorFormulario {
    
    private static final Pattern PATTERN_EMAIL = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    private static final Pattern PATTERN_TELEFONE = Pattern.compile(
        "^[+]?[0-9\\s\\-()]{8,15}$"
    );
    
    private static final Pattern PATTERN_ISBN = Pattern.compile(
        "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[X0-9]$"
    );
    
    private static final Pattern PATTERN_ISSN = Pattern.compile(
        "^\\d{4}-\\d{3}[0-9X]$"
    );

    /**
     * Valida se o texto não está vazio e não é apenas espaços em branco
     */
    public static boolean validarTextoObrigatorio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Valida comprimento máximo do texto
     */
    public static boolean validarTamanhoMaximo(String texto, int maxCaracteres) {
        if (texto == null) return true;
        return texto.length() <= maxCaracteres;
    }

    /**
     * Valida e-mail
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // Campo opcional
        return PATTERN_EMAIL.matcher(email).matches();
    }

    /**
     * Valida telefone
     */
    public static boolean validarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) return true; // Campo opcional
        return PATTERN_TELEFONE.matcher(telefone.replaceAll("\\s", "")).matches();
    }

    /**
     * Valida ISBN-10 ou ISBN-13
     */
    public static boolean validarISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) return true; // Campo opcional
        isbn = isbn.replaceAll("[^0-9X]", "").toUpperCase();
        
        if (isbn.length() == 10) {
            return validarISBN10(isbn);
        } else if (isbn.length() == 13) {
            return validarISBN13(isbn);
        }
        return false;
    }

    /**
     * Valida ISSN
     */
    public static boolean validarISSN(String issn) {
        if (issn == null || issn.trim().isEmpty()) return true; // Campo opcional
        return PATTERN_ISSN.matcher(issn).matches();
    }

    /**
     * Valida um número inteiro
     */
    public static boolean validarNumeroInteiro(String numero) {
        if (numero == null || numero.trim().isEmpty()) return true;
        try {
            Integer.parseInt(numero.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida um número inteiro positivo
     */
    public static boolean validarNumeroPositivo(String numero) {
        if (!validarNumeroInteiro(numero)) return false;
        try {
            return Integer.parseInt(numero.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida um número decimal
     */
    public static boolean validarNumeroDecimal(String numero) {
        if (numero == null || numero.trim().isEmpty()) return true;
        try {
            Double.parseDouble(numero.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida uma data no formato DD/MM/YYYY
     */
    public static boolean validarDataDDMMYYYY(String data) {
        if (data == null || data.trim().isEmpty()) return true;
        
        Pattern pattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/([0-9]{4})$");
        if (!pattern.matcher(data).matches()) return false;
        
        String[] partes = data.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        
        int[] diasPorMes = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        
        // Verificar se é ano bissexto
        if (ano % 4 == 0 && (ano % 100 != 0 || ano % 400 == 0)) {
            diasPorMes[1] = 29;
        }
        
        return dia <= diasPorMes[mes - 1];
    }

    /**
     * Valida código de barras (EAN-13)
     */
    public static boolean validarCodigoBarras(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return true;
        
        codigo = codigo.replaceAll("[^0-9]", "");
        
        if (codigo.length() != 13) return false;
        
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(codigo.charAt(i));
            soma += digit * (i % 2 == 0 ? 1 : 3);
        }
        
        int verificador = (10 - (soma % 10)) % 10;
        return verificador == Character.getNumericValue(codigo.charAt(12));
    }

    /**
     * Valida número de documento (CC portuguesa)
     */
    public static boolean validarNumeroCC(String cc) {
        if (cc == null || cc.trim().isEmpty()) return true;
        
        cc = cc.toUpperCase().replaceAll("\\s", "");
        return cc.matches("^[0-9]{8}[A-Z]{2}[0-9]$");
    }

    /**
     * Valida código postal português
     */
    public static boolean validarCodigoPostal(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return true;
        return codigo.matches("^[0-9]{4}-[0-9]{3}$");
    }

    // Métodos auxiliares privados

    private static boolean validarISBN10(String isbn) {
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (isbn.charAt(i) - '0') * (10 - i);
        }
        int verificador = (11 - (soma % 11)) % 11;
        char esperado = verificador == 10 ? 'X' : (char) ('0' + verificador);
        return isbn.charAt(9) == esperado;
    }

    private static boolean validarISBN13(String isbn) {
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += (isbn.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        int verificador = (10 - (soma % 10)) % 10;
        return isbn.charAt(12) == (char) ('0' + verificador);
    }
}
