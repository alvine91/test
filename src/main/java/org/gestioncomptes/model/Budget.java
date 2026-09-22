package org.gestioncomptes.model;

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
