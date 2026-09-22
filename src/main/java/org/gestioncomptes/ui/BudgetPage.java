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

public class BudgetPage extends JPanel {

    private final Navigator navigator;
    private final AppContext context;
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

    private void createBudget() {
        Account account = context.getCurrentAccount();
        AddBudgetDialog dialog = new AddBudgetDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), context, account);
        dialog.setVisible(true);
        refresh();
    }

    private void editBudget(int row) {
        Budget budget = tableModel.getBudgetAt(row);
        EditBudgetDialog dialog = new EditBudgetDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), context, budget);
        dialog.setVisible(true);
        refresh();
    }

    public void refresh() {
        Account account = context.getCurrentAccount();
        if (account == null) {
            return;
        }
        List<Budget> budgets = context.getAccountService().getBudgets(account);
        tableModel.setBudgets(budgets, account);
    }

    private class BudgetTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Catégorie", "Limite totale", "Dépensé", "Éditer"};
        private List<Budget> budgets = List.of();
        private Account account;

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
