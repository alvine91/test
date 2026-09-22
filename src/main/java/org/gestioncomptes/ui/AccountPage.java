package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Écran "Compte" du mockup : affiche l'ID, le propriétaire, le type et le
 * solde du compte actuellement connecté ({@link AppContext#getCurrentAccount()}),
 * mis en avant dans une carte de solde colorée, avec les boutons vers
 * Budget, Historique, l'ajout d'une transaction, et la déconnexion. Le
 * contenu affiché n'est mis à jour que par {@link #refresh()} : Swing ne
 * redessine pas automatiquement les JLabel quand les données du compte
 * changent ailleurs, il faut donc appeler refresh() explicitement (fait
 * par {@link MainFrame#showAccount}).
 */
public class AccountPage extends JPanel {

    private final Navigator navigator;
    private final AppContext context;

    private final JLabel proprioLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();
    private final JLabel typeLabel = new JLabel();
    private final JLabel idLabel = new JLabel();

    public AccountPage(Navigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        buildUi();
    }

    private void buildUi() {
        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildBalanceCard());
        center.add(Box.createVerticalStrut(20));
        center.add(buildActions());
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(Theme.title("Mon compte"), BorderLayout.WEST);

        JButton logoutButton = Theme.secondaryButton("Se déconnecter");
        logoutButton.addActionListener(e -> navigator.showLogin());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(logoutButton);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    /**
     * Carte pleine largeur, fond coloré (RoundedPanel), qui met le solde en
     * évidence comme dans une application bancaire : c'est l'information
     * la plus importante de l'écran, elle doit sauter aux yeux avant le
     * reste (propriétaire, type, numéro de compte, affichés plus petits).
     */
    private RoundedPanel buildBalanceCard() {
        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Theme.PRIMARY);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel soldeCaption = new JLabel("SOLDE ACTUEL");
        soldeCaption.setFont(Theme.FONT_BOLD.deriveFont(12f));
        soldeCaption.setForeground(new java.awt.Color(255, 255, 255, 200));

        balanceLabel.setFont(Theme.FONT_BALANCE);
        balanceLabel.setForeground(java.awt.Color.WHITE);

        proprioLabel.setFont(Theme.FONT_SUBTITLE);
        proprioLabel.setForeground(new java.awt.Color(255, 255, 255, 220));

        JPanel meta = new JPanel(new GridLayout(1, 2, 24, 0));
        meta.setOpaque(false);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeLabel.setForeground(new java.awt.Color(255, 255, 255, 220));
        idLabel.setForeground(new java.awt.Color(255, 255, 255, 220));
        typeLabel.setFont(Theme.FONT_BODY);
        idLabel.setFont(Theme.FONT_BODY);
        meta.add(typeLabel);
        meta.add(idLabel);

        card.add(soldeCaption);
        card.add(Box.createVerticalStrut(6));
        card.add(balanceLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(proprioLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(meta);
        return card;
    }

    private JPanel buildActions() {
        JPanel actions = new JPanel(new GridLayout(1, 3, 16, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton addTransactionButton = Theme.primaryButton("+ Transaction");
        addTransactionButton.addActionListener(e -> openAddTransaction());

        JButton budgetButton = Theme.secondaryButton("Budgets");
        budgetButton.addActionListener(e -> navigator.showBudget());

        JButton historyButton = Theme.secondaryButton("Historique");
        historyButton.addActionListener(e -> navigator.showHistory());

        actions.add(addTransactionButton);
        actions.add(budgetButton);
        actions.add(historyButton);
        return actions;
    }

    /** Ouvre la boîte de dialogue modale d'ajout de transaction, puis rafraîchit le solde affiché. */
    private void openAddTransaction() {
        Account account = context.getCurrentAccount();
        AddTransactionDialog dialog = new AddTransactionDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), context, account);
        dialog.setVisible(true);
        refresh();
    }

    /** Recopie les données du compte courant dans les composants de la carte de solde. */
    public void refresh() {
        Account account = context.getCurrentAccount();
        if (account == null) {
            return;
        }
        balanceLabel.setText(Theme.formatMoney(account.getBalance()));
        proprioLabel.setText(account.getName());
        typeLabel.setText("Type : " + account.getType());
        idLabel.setText("N° " + account.getId());
    }
}
