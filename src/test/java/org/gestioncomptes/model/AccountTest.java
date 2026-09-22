package org.gestioncomptes.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountTest {

    @Test
    void doTransactionUpdatesBalance() {
        Account account = new Account("1", "GOUGANG", "Alvine", "alvine@example.com", "hash", "Courant", 100.0);
        Transaction credit = new Transaction("1", "1", "Salaire", 500.0, LocalDate.now(), CategoryBudget.EPARGNE,
                false);
        Transaction debit = new Transaction("2", "1", "Courses", -50.0, LocalDate.now(), CategoryBudget.SANTE,
                false);

        account.doTransaction(credit);
        account.doTransaction(debit);

        assertEquals(550.0, account.getBalance(), 0.0001);
    }
}
