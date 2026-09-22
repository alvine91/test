package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class AccountPage extends JPanel {

    private final Navigator navigator;
    private final AppContext context;

    private final JLabel idLabel = new JLabel();
    private final JLabel proprioLabel = new JLabel();
    private final JLabel typeLabel = new JLabel();
    private final JLabel soldeLabel = new JLabel();

    public AccountPage(Navigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        buildUi();
    }

    private void buildUi() {
        JLabel title = new JLabel("Compte", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        add(title, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        Font infoFont = idLabel.getFont().deriveFont(16f);
        for (JLabel label : new JLabel[]{idLabel, proprioLabel, typeLabel, soldeLabel}) {
            label.setFont(infoFont);
        }
        infoPanel.add(idLabel);
        infoPanel.add(proprioLabel);
        infoPanel.add(typeLabel);
        infoPanel.add(soldeLabel);
        add(infoPanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 8, 8));
        JButton addTransactionButton = new JButton("Ajouter une transaction");
        addTransactionButton.addActionListener(e -> openAddTransaction());

        JButton budgetButton = new JButton("Liste des budgets");
        budgetButton.addActionListener(e -> navigator.showBudget());

        JButton historyButton = new JButton("Historique");
        historyButton.addActionListener(e -> navigator.showHistory());

        JButton logoutButton = new JButton("Se déconnecter");
        logoutButton.addActionListener(e -> navigator.showLogin());

        buttons.add(addTransactionButton);
        buttons.add(budgetButton);
        buttons.add(historyButton);
        buttons.add(logoutButton);
        add(buttons, BorderLayout.SOUTH);
    }

    private void openAddTransaction() {
        Account account = context.getCurrentAccount();
        AddTransactionDialog dialog = new AddTransactionDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), context, account);
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        Account account = context.getCurrentAccount();
        if (account == null) {
            return;
        }
        idLabel.setText("N° de compte : " + account.getId());
        proprioLabel.setText("Propriétaire : " + account.getName());
        typeLabel.setText("Type de compte : " + account.getType());
        soldeLabel.setText(String.format("Solde : %.2f CHF", account.getBalance()));
    }
}
