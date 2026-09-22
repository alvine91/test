package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.Budget;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/**
 * Écran "Budget" du mockup : tableau des budgets du compte courant
 * (catégorie, limite, dépenses déjà effectuées) avec une colonne "Éditer"
 * cliquable (voir {@link ButtonCellRenderer}/{@link ButtonCellEditor}), et
 * un bouton pour créer un nouveau budget.
 */
public class BudgetPage extends JPanel {

    private final Navigator navigator;
    private final AppContext context;
    // Modèle de table "maison" (contrairement à HistoryPage) car on a besoin d'une
    // colonne calculée (les dépenses) et d'une colonne bouton, que DefaultTableModel
    // ne sait pas représenter directement.
    private final BudgetTableModel tableModel = new BudgetTableModel();
    private final JTable table = new JTable(tableModel);

    public BudgetPage(Navigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        buildUi();
    }

    private void buildUi() {
        JLabel title = new JLabel("Budget", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        add(title, BorderLayout.NORTH);

        table.setRowHeight(28);
        // Renderer = dessine le bouton, Editor = réagit au clic (voir Javadoc des deux classes).
        table.getColumn("Éditer").setCellRenderer(new ButtonCellRenderer());
        table.getColumn("Éditer").setCellEditor(new ButtonCellEditor(this::editBudget));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout());
        JButton addBudgetButton = new JButton("Créer un budget");
        addBudgetButton.addActionListener(e -> createBudget());
        JButton backButton = new JButton("Retour au compte");
        backButton.addActionListener(e -> navigator.showAccount(context.getCurrentAccount()));
        buttons.add(addBudgetButton);
        buttons.add(backButton);
        add(buttons, BorderLayout.SOUTH);
    }

    /** Ouvre la boîte de dialogue de création de budget, puis rafraîchit le tableau. */
    private void createBudget() {
        Account account = context.getCurrentAccount();
        AddBudgetDialog dialog = new AddBudgetDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), context, account);
        dialog.setVisible(true);
        refresh();
    }

    /** Appelée par ButtonCellEditor quand on clique sur "Éditer" d'une ligne donnée. */
    private void editBudget(int row) {
        Budget budget = tableModel.getBudgetAt(row);
        EditBudgetDialog dialog = new EditBudgetDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), context, budget);
        dialog.setVisible(true);
        refresh();
    }

    /** Recharge la liste des budgets du compte courant et notifie le tableau. */
    public void refresh() {
        Account account = context.getCurrentAccount();
        if (account == null) {
            return;
        }
        List<Budget> budgets = context.getAccountService().getBudgets(account);
        tableModel.setBudgets(budgets, account);
    }

    /**
     * Modèle de table maison : contrairement à DefaultTableModel, les
     * valeurs ne sont pas stockées cellule par cellule mais calculées à la
     * volée dans {@link #getValueAt} à partir de la liste de Budget
     * (nécessaire pour la colonne "Dépensé", qui vient d'un calcul dans
     * AccountService, et pour la colonne "Éditer" qui n'existe pas dans le
     * modèle Budget).
     */
    private class BudgetTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Catégorie", "Limite totale", "Dépensé", "Éditer"};
        private List<Budget> budgets = List.of();
        private Account account;

        /** Remplace les données affichées et prévient JTable qu'il doit se redessiner. */
        void setBudgets(List<Budget> budgets, Account account) {
            this.budgets = budgets;
            this.account = account;
            fireTableDataChanged();
        }

        Budget getBudgetAt(int row) {
            return budgets.get(row);
        }

        @Override
        public int getRowCount() {
            return budgets.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        // Seule la colonne "Éditer" (index 4) est "éditable" : c'est ce qui déclenche
        // l'affichage du ButtonCellEditor au lieu du simple renderer sur cette colonne.
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Budget b = budgets.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> b.getId();
                case 1 -> b.getCategoryBudget().getLibelle();
                case 2 -> String.format("%.2f CHF", b.getTotalLimit());
                case 3 -> String.format("%.2f CHF",
                        context.getAccountService().depensesPourCategorie(account, b.getCategoryBudget()));
                case 4 -> "Éditer";
                default -> "";
            };
        }
    }
}
