package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.Budget;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Écran "Budget" du mockup : tableau des budgets du compte courant
 * (catégorie, limite, progression des dépenses) avec une colonne "Éditer"
 * cliquable (voir {@link ButtonCellRenderer}/{@link ButtonCellEditor}), et
 * un bouton pour créer un nouveau budget.
 */
public class BudgetPage extends JPanel {

    private final Navigator navigator;
    private final AppContext context;
    // Modèle de table "maison" (contrairement à HistoryPage) car on a besoin d'une
    // colonne calculée (la progression) et d'une colonne bouton, que DefaultTableModel
    // ne sait pas représenter directement.
    private final BudgetTableModel tableModel = new BudgetTableModel();
    private final JTable table = new JTable(tableModel);

    public BudgetPage(Navigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        buildUi();
    }

    private void buildUi() {
        add(buildHeader(), BorderLayout.NORTH);

        table.setRowHeight(40);
        table.setFont(Theme.FONT_BODY);
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.getTableHeader().setFont(Theme.FONT_BOLD.deriveFont(12f));
        // Renderer = dessine le composant, Editor = réagit au clic (voir Javadoc des deux classes).
        table.getColumn("Progression").setCellRenderer(new ProgressCellRenderer());
        table.getColumn("Éditer").setCellRenderer(new ButtonCellRenderer());
        table.getColumn("Éditer").setCellEditor(new ButtonCellEditor(this::editBudget));
        // Largeurs adaptées au contenu : "ID" et "Éditer" sont courts, "Progression"
        // a besoin de place pour afficher "dépensé / limite" sans être coupé.
        table.getColumn("ID").setPreferredWidth(40);
        table.getColumn("ID").setMaxWidth(50);
        table.getColumn("Progression").setPreferredWidth(240);
        table.getColumn("Éditer").setPreferredWidth(80);
        table.getColumn("Éditer").setMaxWidth(90);

        JPanel tableCard = Theme.card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(Theme.title("Budgets"), BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton addBudgetButton = Theme.primaryButton("+ Nouveau budget");
        addBudgetButton.addActionListener(e -> createBudget());
        JButton backButton = Theme.secondaryButton("Retour");
        backButton.addActionListener(e -> navigator.showAccount(context.getCurrentAccount()));
        right.add(backButton);
        right.add(addBudgetButton);
        header.add(right, BorderLayout.EAST);
        return header;
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

    /** Valeur affichée dans la colonne "Progression" : ce qui a été dépensé face à la limite. */
    private record Progress(double spent, double limit) {
        double ratio() {
            return limit <= 0 ? 0 : spent / limit;
        }
    }

    /**
     * Dessine la colonne "Progression" comme une barre de progression
     * colorée plutôt qu'un simple pourcentage texte : vert tant que les
     * dépenses restent sous 80% du budget, orange entre 80% et 100%,
     * rouge au-delà (budget dépassé). JProgressBar limite sa valeur à 100,
     * on plafonne donc le pourcentage affiché sans perdre l'information
     * de dépassement, portée par la couleur.
     */
    private static class ProgressCellRenderer extends JProgressBar implements TableCellRenderer {
        ProgressCellRenderer() {
            setStringPainted(true);
            setMinimum(0);
            setMaximum(100);
            setFont(Theme.FONT_BODY.deriveFont(12f));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Progress progress = (Progress) value;
            int percent = (int) Math.round(progress.ratio() * 100);
            setValue(Math.min(100, Math.max(0, percent)));
            // Une seule fois "CHF" (plutôt que sur les deux montants) pour que le texte
            // tienne dans la largeur de la colonne sans être coupé.
            setString(String.format("%,.2f / %,.2f CHF", progress.spent, progress.limit));
            if (percent >= 100) {
                setForeground(Theme.DANGER);
            } else if (percent >= 80) {
                setForeground(Theme.WARNING);
            } else {
                setForeground(Theme.SUCCESS);
            }
            return this;
        }
    }

    /**
     * Modèle de table maison : contrairement à DefaultTableModel, les
     * valeurs ne sont pas stockées cellule par cellule mais calculées à la
     * volée dans {@link #getValueAt} à partir de la liste de Budget
     * (nécessaire pour la colonne "Progression", qui vient d'un calcul dans
     * AccountService, et pour la colonne "Éditer" qui n'existe pas dans le
     * modèle Budget).
     */
    private class BudgetTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Catégorie", "Limite totale", "Progression", "Éditer"};
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
                case 2 -> Theme.formatMoney(b.getTotalLimit());
                case 3 -> new Progress(
                        context.getAccountService().depensesPourCategorie(account, b.getCategoryBudget()),
                        b.getTotalLimit());
                case 4 -> "Éditer";
                default -> "";
            };
        }
    }
}
