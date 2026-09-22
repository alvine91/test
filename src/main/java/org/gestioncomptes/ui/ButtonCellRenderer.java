package org.gestioncomptes.ui;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * Affiche un JButton dans une cellule de JTable (colonne "Éditer" de
 * BudgetPage). Un {@link TableCellRenderer} ne fait que dessiner la
 * cellule : il ne réagit pas aux clics, c'est le rôle de
 * {@link ButtonCellEditor}, utilisé en parallèle sur la même colonne.
 */
class ButtonCellRenderer extends JButton implements TableCellRenderer {

    ButtonCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        setText(value == null ? "" : value.toString());
        return this;
    }
}
