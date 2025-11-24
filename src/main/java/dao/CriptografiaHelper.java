package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe utilitária para lidar com a criptografia de senhas usando SHA-256.
 */
public class CriptografiaHelper {

    /**
     * Gera o hash SHA-256 de uma string (senha em texto limpo).
     *
     * @param senha A string de entrada.
     * @return O hash da string em formato hexadecimal.
     * @throws RuntimeException se o algoritmo SHA-256 não for encontrado.
     */
    public static String hashSHA256(String senha) {
        try {
            // 1. Obter uma instância do MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 2. Aplicar o hash na senha
            byte[] hash = digest.digest(senha.getBytes());

            // 3. Converter o array de bytes para formato hexadecimal (String)
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Exceção improvável em JVMs modernas, mas tratada.
            throw new RuntimeException("Erro ao encontrar algoritmo de criptografia (SHA-256).", e);
        }
    }
}