package org.gestioncomptes.model;

import java.time.LocalDate;

/**
 * Une opération (crédit ou débit) sur un compte (classe "Transaction" du
 * diagramme de classe).
 *
 * <p>{@code amount} est déjà signé : positif pour un crédit (ex: salaire),
 * négatif pour un débit (ex: achat). C'est ce qui permet à
 * {@link Account#doTransaction(Transaction)} de simplement additionner le
 * montant au solde, et à {@link History#totalDepensesParCategorie} de ne
 * garder que les montants négatifs pour calculer les dépenses par budget.
 */
public class Transaction {

    private final String id;
    private final String accountId;
    private String description;
    private double amount;
    private LocalDate date;
    private CategoryBudget categoryBudget;
    private boolean recurring;

    public Transaction(String id, String accountId, String description, double amount, LocalDate date,
                        CategoryBudget categoryBudget, boolean recurring) {
        this.id = id;
        this.accountId = accountId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.categoryBudget = categoryBudget;
        this.recurring = recurring;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public CategoryBudget getCategoryBudget() {
        return categoryBudget;
    }

    public boolean isRecurring() {
        return recurring;
    }
}
