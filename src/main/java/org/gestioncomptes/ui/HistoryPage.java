package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.Transaction;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Écran "Historique" du mockup : liste en lecture seule des transactions du
 * compte courant, dans un {@link JTable}. Le tableau est piloté par un
 * {@link DefaultTableModel} : on ne modifie jamais les JLabel/cellules à la
 * main, on vide et on repeuple le modèle dans {@link #refresh()}, et Swing
 * se charge de redessiner la table. La colonne "Montant" garde la valeur
 * numérique brute (pas une chaîne déjà formatée) afin que
 * {@link MoneyCellRenderer} puisse colorer le texte selon le signe.
 */
public class HistoryPage extends JPanel {

    private final Navigator navigator;
    private final AppContext context;

    // DefaultTableModel générique avec des colonnes fixes ; on interdit l'édition
    // car cette page n'affiche que de l'historique (pas de modification possible).
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Description", "Montant", "Date", "Catégorie", "Récurrente"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 1 ? Double.class : Object.class;
        }
    };
    private final JTable table = new JTable(tableModel);

    public HistoryPage(Navigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        buildUi();
    }

    private void buildUi() {
        add(buildHeader(), BorderLayout.NORTH);

        table.setRowHeight(32);
        table.setFont(Theme.FONT_BODY);
        table.setShowGrid(false);
        table.getTableHeader().setFont(Theme.FONT_BOLD.deriveFont(12f));
        table.setDefaultRenderer(Object.class, new ZebraCellRenderer());
        table.setDefaultRenderer(Double.class, new MoneyCellRenderer());

        JPanel tableCard = Theme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(Theme.title("Historique"), BorderLayout.WEST);

        JButton backButton = Theme.secondaryButton("Retour");
        backButton.addActionListener(e -> navigator.showAccount(context.getCurrentAccount()));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(backButton);
        header.add(right, BorderLayout.EAST);
        return header;
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
                    t.getDescription(),
                    t.getAmount(),
                    t.getDate().toString(),
                    t.getCategoryBudget().getLibelle(),
                    t.isRecurring() ? "Oui" : "Non"
            });
        }
    }

    /** Alterne un fond blanc et un fond légèrement grisé d'une ligne à l'autre, pour guider la lecture du tableau. */
    private static class ZebraCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : Theme.BACKGROUND);
            }
            return c;
        }
    }

    /** Comme ZebraCellRenderer, mais formate le montant et le colore en vert (crédit) ou rouge (débit). */
    private static class MoneyCellRenderer extends ZebraCellRenderer {
        MoneyCellRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            double amount = (double) value;
            Component c = super.getTableCellRendererComponent(table, Theme.formatMoney(amount), isSelected,
                    hasFocus, row, column);
            c.setForeground(Theme.amountColor(amount));
            return c;
        }
    }
}
