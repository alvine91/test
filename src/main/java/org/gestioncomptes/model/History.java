package org.gestioncomptes.model;

import java.util.Collections;
import java.util.List;

/**
 * Regroupe un compte et la liste de ses transactions (classe "History" du
 * diagramme de classe). Cette classe n'est pas persistée elle-même : elle
 * est reconstruite à la demande par
 * {@link org.gestioncomptes.service.AccountService#getHistory(Account)} en
 * allant chercher les transactions du compte dans le
 * {@link org.gestioncomptes.dao.TransactionRepository}. C'est donc un objet
 * "vue" plutôt qu'une entité stockée en CSV.
 */
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

    /** Implémente afficherTransactions() du diagramme de classe (page HistoryPage). */
    public List<Transaction> afficherTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /**
     * Somme des débits (montants négatifs) pour une catégorie donnée,
     * ramenée en valeur positive. Utilisé par BudgetPage pour afficher la
     * colonne "Dépensé" en regard de la limite du budget.
     */
    public double totalDepensesParCategorie(CategoryBudget categorie) {
        return transactions.stream()
                .filter(t -> t.getCategoryBudget() == categorie && t.getAmount() < 0)
                .mapToDouble(t -> -t.getAmount())
                .sum();
    }
}
