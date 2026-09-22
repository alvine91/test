package org.gestioncomptes.model;

/**
 * Un budget associe une limite de dépenses ({@code totalLimit}) à une
 * {@link CategoryBudget} pour un compte donné (classe "budget" du diagramme
 * de classe). Le montant réellement dépensé n'est pas stocké ici : il est
 * recalculé à la volée à partir de l'historique des transactions, voir
 * {@link History#totalDepensesParCategorie} et
 * {@link org.gestioncomptes.service.AccountService#depensesPourCategorie}.
 */
public class Budget {

    private final String id;
    private final String accountId;
    private CategoryBudget categoryBudget;
    private double totalLimit;

    public Budget(String id, String accountId, CategoryBudget categoryBudget, double totalLimit) {
        this.id = id;
        this.accountId = accountId;
        this.categoryBudget = categoryBudget;
        this.totalLimit = totalLimit;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public CategoryBudget getCategoryBudget() {
        return categoryBudget;
    }

    public double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(double totalLimit) {
        this.totalLimit = totalLimit;
    }
}
