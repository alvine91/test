package org.gestioncomptes.ui;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.function.IntConsumer;

/**
 * Rend une cellule de JTable cliquable comme un vrai bouton (colonne
 * "Éditer" de BudgetPage). Swing ne propose pas nativement de bouton
 * cliquable dans une cellule : il faut un {@link TableCellEditor} qui,
 * dès qu'on clique sur la cellule, affiche ce même JButton et déclenche
 * {@code onClick} avec le numéro de ligne. {@code fireEditingStopped()}
 * referme immédiatement l'édition pour que le clic se comporte comme un
 * clic de bouton normal plutôt que comme un mode édition.
 */
class ButtonCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final JButton button = new JButton();
    private final IntConsumer onClick;
    private int currentRow;

    ButtonCellEditor(IntConsumer onClick) {
        this.onClick = onClick;
        button.addActionListener(e -> {
            fireEditingStopped();
            onClick.accept(currentRow);
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                   int column) {
        currentRow = row;
        button.setText(value == null ? "" : value.toString());
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return button.getText();
    }
}
