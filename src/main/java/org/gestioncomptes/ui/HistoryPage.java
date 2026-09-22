package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.Transaction;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/**
 * Écran "Historique" du mockup : liste en lecture seule des transactions du
 * compte courant, dans un {@link JTable}. Le tableau est piloté par un
 * {@link DefaultTableModel} : on ne modifie jamais les JLabel/cellules à la
 * main, on vide et on repeuple le modèle dans {@link #refresh()}, et Swing
 * se charge de redessiner la table.
 */
public class HistoryPage extends JPanel {

    private final Navigator navigator;
    private final AppContext context;

    // DefaultTableModel générique avec des colonnes fixes ; on interdit l'édition
    // car cette page n'affiche que de l'historique (pas de modification possible).
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Description", "Montant", "Date", "Catégorie", "Récurrente"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public HistoryPage(Navigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        buildUi();
    }

    private void buildUi() {
        JLabel title = new JLabel("Historique", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        add(title, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton backButton = new JButton("Retour au compte");
        backButton.addActionListener(e -> navigator.showAccount(context.getCurrentAccount()));
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(backButton);
        add(buttons, BorderLayout.SOUTH);
    }

    /** Vide puis repeuple la table à partir de l'historique du compte courant. */
    public void refresh() {
        Account account = context.getCurrentAccount();
        if (account == null) {
            return;
        }
        tableModel.setRowCount(0);
        List<Transaction> transactions = context.getAccountService().getHistory(account).afficherTransactions();
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getDescription(),
                    String.format("%.2f CHF", t.getAmount()),
                    t.getDate(),
                    t.getCategoryBudget().getLibelle(),
                    t.isRecurring() ? "Oui" : "Non"
            });
        }
    }
}
