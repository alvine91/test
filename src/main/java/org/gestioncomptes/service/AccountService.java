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

/**
 * Logique métier autour d'un compte : ajouter une transaction, créer ou
 * éditer un budget, consulter l'historique et les dépenses. Cette classe
 * fait le lien entre les pages Swing (qui ne connaissent que
 * {@link org.gestioncomptes.ui.AppContext}) et les trois dépôts CSV : les
 * pages n'appellent jamais les Repository directement.
 */
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

    /** Reconstruit l'historique du compte à partir des transactions stockées (page Historique). */
    public History getHistory(Account account) {
        return new History(account, transactionRepository.findByAccountId(account.getId()));
    }

    /** Liste des budgets du compte (page Budget). */
    public List<Budget> getBudgets(Account account) {
        return budgetRepository.findByAccountId(account.getId());
    }

    /**
     * Crée une transaction, met à jour le solde en mémoire du compte, puis
     * persiste à la fois la transaction et le compte (deux fichiers CSV
     * distincts). Utilisée par AddTransactionDialog.
     */
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

    /** Crée un nouveau budget pour une catégorie donnée. Utilisée par AddBudgetDialog. */
    public Budget addBudget(Account account, CategoryBudget category, double limit) {
        String id = budgetRepository.nextId();
        Budget budget = new Budget(id, account.getId(), category, limit);
        budgetRepository.save(budget);
        return budget;
    }

    /** Modifie la limite d'un budget existant (bouton "Éditer"). Utilisée par EditBudgetDialog. */
    public void editBudget(Budget budget, double newLimit) {
        budget.setTotalLimit(newLimit);
        budgetRepository.save(budget);
    }

    /** Dépenses déjà effectuées pour une catégorie, affichées en regard de la limite du budget. */
    public double depensesPourCategorie(Account account, CategoryBudget category) {
        return getHistory(account).totalDepensesParCategorie(category);
    }
}
