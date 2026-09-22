package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame implements Navigator {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);
    private final AppContext context;

    private final AccountPage accountPage;
    private final BudgetPage budgetPage;
    private final HistoryPage historyPage;

    public MainFrame(AppContext context) {
        super("Gestion de Comptes");
        this.context = context;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(700, 550));
        setLocationRelativeTo(null);

        LoginPage loginPage = new LoginPage(this, context);
        accountPage = new AccountPage(this, context);
        budgetPage = new BudgetPage(this, context);
        historyPage = new HistoryPage(this, context);

        container.add(loginPage, "LOGIN");
        container.add(accountPage, "ACCOUNT");
        container.add(budgetPage, "BUDGET");
        container.add(historyPage, "HISTORY");

        setContentPane(container);
        showLogin();
    }

    @Override
    public void showLogin() {
        context.setCurrentAccount(null);
        cardLayout.show(container, "LOGIN");
    }

    @Override
    public void showAccount(Account account) {
        context.setCurrentAccount(account);
        accountPage.refresh();
        cardLayout.show(container, "ACCOUNT");
    }

    @Override
    public void showBudget() {
        budgetPage.refresh();
        cardLayout.show(container, "BUDGET");
    }

    @Override
    public void showHistory() {
        historyPage.refresh();
        cardLayout.show(container, "HISTORY");
    }
}
