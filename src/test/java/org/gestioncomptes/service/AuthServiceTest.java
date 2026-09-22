package org.gestioncomptes.service;

import org.gestioncomptes.dao.AccountRepository;
import org.gestioncomptes.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {

    @Test
    void createsAccountOnFirstLogin(@TempDir Path tempDir) throws AuthException {
        AccountRepository repository = new AccountRepository(tempDir.resolve("comptes.csv"));
        AuthService authService = new AuthService(repository);

        Account account = authService.checkAccount("GOUGANG", "Alvine", "alvine@example.com", "motdepasse1");

        assertNotNull(account.getId());
        assertEquals("GOUGANG", account.getNom());
    }

    @Test
    void rejectsWrongPasswordOnSecondLogin(@TempDir Path tempDir) throws AuthException {
        AccountRepository repository = new AccountRepository(tempDir.resolve("comptes.csv"));
        AuthService authService = new AuthService(repository);

        authService.checkAccount("GOUGANG", "Alvine", "alvine@example.com", "motdepasse1");

        assertThrows(AuthException.class, () ->
                authService.checkAccount("GOUGANG", "Alvine", "alvine@example.com", "mauvaisMotDePasse"));
    }

    @Test
    void claimsLegacyAccountOnFirstLoginWithCredentials(@TempDir Path tempDir) throws AuthException {
        AccountRepository repository = new AccountRepository(tempDir.resolve("comptes.csv"));
        repository.save(new Account("1", "GOUGANG", "Alvine", "", "", "Courant", 0.0));
        AuthService authService = new AuthService(repository);

        Account account = authService.checkAccount("GOUGANG", "Alvine", "alvine@example.com", "motdepasse1");

        assertEquals("1", account.getId());
        assertEquals("alvine@example.com", account.getEmail());
    }
}
