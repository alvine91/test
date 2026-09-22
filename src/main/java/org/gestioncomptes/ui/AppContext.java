package org.gestioncomptes.ui;

import org.gestioncomptes.dao.AccountRepository;
import org.gestioncomptes.dao.BudgetRepository;
import org.gestioncomptes.dao.TransactionRepository;
import org.gestioncomptes.model.Account;
import org.gestioncomptes.service.AccountService;
import org.gestioncomptes.service.AuthService;

/**
 * Regroupe les dépôts et services partagés par les pages Swing, ainsi que le
 * compte actuellement connecté.
 */
public class AppContext {

    private final AuthService authService;
    private final AccountService accountService;

    private Account currentAccount;

    public AppContext(AccountRepository accountRepository, TransactionRepository transactionRepository,
                       BudgetRepository budgetRepository) {
        this.authService = new AuthService(accountRepository);
        this.accountService = new AccountService(accountRepository, transactionRepository, budgetRepository);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }
}
