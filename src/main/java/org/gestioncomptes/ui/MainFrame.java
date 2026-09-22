package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Fenêtre principale de l'application. Utilise un {@link CardLayout} : les
 * quatre pages (Login, Compte, Budget, Historique) sont toutes ajoutées
 * une fois au même conteneur, empilées comme des cartes, et seule une
 * carte est visible à la fois. Naviguer d'une page à l'autre ({@link Navigator})
 * revient donc juste à appeler {@code cardLayout.show(container, "NOM")},
 * sans créer/détruire de fenêtres. MainFrame est la seule classe qui
 * implémente Navigator ; toutes les pages lui délèguent leur navigation.
 */
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
        setMinimumSize(new Dimension(760, 600));
        setSize(860, 640);
        setLocationRelativeTo(null);
        container.setBackground(Theme.BACKGROUND);

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

    /** Retour à l'écran de connexion ; on oublie le compte courant (déconnexion). */
    @Override
    public void showLogin() {
        context.setCurrentAccount(null);
        cardLayout.show(container, "LOGIN");
    }

    /** Mémorise le compte connecté dans le contexte partagé, rafraîchit l'affichage, puis navigue. */
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
