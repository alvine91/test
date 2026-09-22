package org.gestioncomptes.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Hachage simple des mots de passe (SHA-256) avant stockage dans le CSV des
 * comptes : on évite de conserver les mots de passe en clair sur disque.
 */
public final class PasswordUtils {

    private PasswordUtils() {
    }

    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponible sur cette JVM", e);
        }
    }
}
