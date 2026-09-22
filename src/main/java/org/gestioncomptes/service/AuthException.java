package org.gestioncomptes.service;

/**
 * Levée par {@link AuthService#checkAccount} quand la connexion échoue
 * (champ manquant, ou email/mot de passe ne correspondant pas au compte
 * trouvé). C'est une exception "checked" : LoginPage doit l'attraper avec
 * un try/catch et afficher {@link #getMessage()} à l'utilisateur.
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
