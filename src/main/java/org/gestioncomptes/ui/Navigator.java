package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;

public interface Navigator {
    void showLogin();

    void showAccount(Account account);

    void showBudget();

    void showHistory();
}
