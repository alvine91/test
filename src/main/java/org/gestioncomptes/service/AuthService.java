package org.gestioncomptes.service;

import org.gestioncomptes.dao.AccountRepository;
import org.gestioncomptes.model.Account;
import org.gestioncomptes.util.PasswordUtils;

import java.util.Optional;

/**
 * Implémente checkAccount() du LoginPage du diagramme de classe.
 *
 * <p>Trois cas sont gérés :
 * <ul>
 *     <li>Nom/Prénom inconnus : un nouveau compte est créé (solde à 0).</li>
 *     <li>Nom/Prénom connus mais sans email/mot de passe (import depuis
 *     compte.csv) : le compte est "réclamé" avec les identifiants fournis.</li>
 *     <li>Nom/Prénom connus avec des identifiants déjà définis : l'email et
 *     le mot de passe doivent correspondre.</li>
 * </ul>
 */
public class AuthService {

    private final AccountRepository accountRepository;

    public AuthService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account checkAccount(String nom, String prenom, String email, String motDePasse) throws AuthException {
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()
                || email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            throw new AuthException("Tous les champs sont obligatoires.");
        }

        String hash = PasswordUtils.hash(motDePasse);
        Optional<Account> existing = accountRepository.findByNomPrenom(nom.trim(), prenom.trim());

        if (existing.isPresent()) {
            Account account = existing.get();
            if (account.getMotDePasseHash() == null || account.getMotDePasseHash().isBlank()) {
                account.setEmail(email.trim());
                account.setMotDePasseHash(hash);
                accountRepository.save(account);
                return account;
            }
            if (!account.getEmail().equalsIgnoreCase(email.trim()) || !account.getMotDePasseHash().equals(hash)) {
                throw new AuthException("Email ou mot de passe incorrect pour ce compte.");
            }
            return account;
        }

        String id = accountRepository.nextId();
        Account account = new Account(id, nom.trim(), prenom.trim(), email.trim(), hash, "Courant", 0.0);
        accountRepository.save(account);
        return account;
    }
}
