package org.gestioncomptes.service;

import org.gestioncomptes.dao.AccountRepository;
import org.gestioncomptes.dao.BudgetRepository;
import org.gestioncomptes.dao.TransactionRepository;
import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.Budget;
import org.gestioncomptes.model.CategoryBudget;
import org.gestioncomptes.model.History;
import org.gestioncomptes.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                           BudgetRepository budgetRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    public History getHistory(Account account) {
        return new History(account, transactionRepository.findByAccountId(account.getId()));
    }

    public List<Budget> getBudgets(Account account) {
        return budgetRepository.findByAccountId(account.getId());
    }

    public Transaction addTransaction(Account account, String description, double amount, LocalDate date,
                                       CategoryBudget category, boolean recurring) {
        String id = transactionRepository.nextId();
        Transaction transaction = new Transaction(id, account.getId(), description, amount, date, category,
                recurring);
        account.doTransaction(transaction);
        transactionRepository.save(transaction);
        accountRepository.save(account);
        return transaction;
    }

    public Budget addBudget(Account account, CategoryBudget category, double limit) {
        String id = budgetRepository.nextId();
        Budget budget = new Budget(id, account.getId(), category, limit);
        budgetRepository.save(budget);
        return budget;
    }

    public void editBudget(Budget budget, double newLimit) {
        budget.setTotalLimit(newLimit);
        budgetRepository.save(budget);
    }

    public double depensesPourCategorie(Account account, CategoryBudget category) {
        return getHistory(account).totalDepensesParCategorie(category);
    }
}
