package util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Helper para hashing de senhas usando PBKDF2WithHmacSHA256.
 * Formato do hash armazenado: PBKDF2$iterations$saltBase64$hashBase64
 */
public class PasswordHasher {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int SALT_LEN = 16;
    private static final int HASH_LEN = 32; // 256 bits
    private static final int ITERATIONS = 65536;

    public static String hash(String senha) {
        if (senha == null) return null;
        // Tentar usar BCrypt se disponível (via reflection) para evitar dependência direta
        try {
            Class<?> bc = Class.forName("org.mindrot.jbcrypt.BCrypt");
            java.lang.reflect.Method gensalt = bc.getMethod("gensalt");
            java.lang.reflect.Method hashpw = bc.getMethod("hashpw", String.class, String.class);
            String salt = (String) gensalt.invoke(null);
            String hashed = (String) hashpw.invoke(null, senha, salt);
            // Prefixar para identificar o algoritmo usado
            return "BCRYPT$" + hashed;
        } catch (ClassNotFoundException cnf) {
            // bcrypt não disponível -> fallback para PBKDF2
        } catch (Exception ex) {
            // se ocorrer qualquer erro com bcrypt, fallback
        }

        try {
            byte[] salt = new byte[SALT_LEN];
            SecureRandom sr = SecureRandom.getInstanceStrong();
            sr.nextBytes(salt);

            byte[] hash = pbkdf2(senha.toCharArray(), salt, ITERATIONS, HASH_LEN);

            String saltB64 = Base64.getEncoder().encodeToString(salt);
            String hashB64 = Base64.getEncoder().encodeToString(hash);
            return String.format("PBKDF2$%d$%s$%s", ITERATIONS, saltB64, hashB64);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    public static boolean verify(String senha, String stored) {
        if (senha == null || stored == null) return false;
        try {
            if (stored.startsWith("BCRYPT$")) {
                String hashed = stored.substring("BCRYPT$".length());
                try {
                    Class<?> bc = Class.forName("org.mindrot.jbcrypt.BCrypt");
                    java.lang.reflect.Method checkpw = bc.getMethod("checkpw", String.class, String.class);
                    Object res = checkpw.invoke(null, senha, hashed);
                    return res instanceof Boolean && (Boolean) res;
                } catch (ClassNotFoundException cnf) {
                    // BCrypt não disponível -> não é possível verificar bcrypt
                    return false;
                }
            }

            if (!stored.startsWith("PBKDF2$")) return false;
            String[] parts = stored.split("\\$", 4);
            if (parts.length != 4) return false;
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);

            byte[] actual = pbkdf2(senha.toCharArray(), salt, iterations, expected.length);

            return slowEquals(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] senha, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(senha, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
