package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;

/**
 * Abstraction de navigation entre les quatre pages de l'application.
 * Chaque page (LoginPage, AccountPage, ...) reçoit un Navigator dans son
 * constructeur au lieu de connaître {@link MainFrame} directement : cela
 * évite les dépendances circulaires et permet de tester les pages sans
 * Swing. {@link MainFrame} est la seule classe qui implémente cette
 * interface, en s'appuyant sur un {@link java.awt.CardLayout} pour changer
 * de panneau visible.
 */
public interface Navigator {
    void showLogin();

    void showAccount(Account account);

    void showBudget();

    void showHistory();
}
