package org.gestioncomptes.model;

import java.util.Collections;
import java.util.List;

public class History {

    private final Account account;
    private final List<Transaction> transactions;

    public History(Account account, List<Transaction> transactions) {
        this.account = account;
        this.transactions = transactions;
    }

    public Account getAccount() {
        return account;
    }

    public List<Transaction> afficherTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public double totalDepensesParCategorie(CategoryBudget categorie) {
        return transactions.stream()
                .filter(t -> t.getCategoryBudget() == categorie && t.getAmount() < 0)
                .mapToDouble(t -> -t.getAmount())
                .sum();
    }
}
