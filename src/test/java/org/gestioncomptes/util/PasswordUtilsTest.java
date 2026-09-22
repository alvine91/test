package org.gestioncomptes.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PasswordUtilsTest {

    @Test
    void hashIsDeterministic() {
        assertEquals(PasswordUtils.hash("secret123"), PasswordUtils.hash("secret123"));
    }

    @Test
    void differentPasswordsProduceDifferentHashes() {
        assertNotEquals(PasswordUtils.hash("secret123"), PasswordUtils.hash("autreMotDePasse"));
    }
}
