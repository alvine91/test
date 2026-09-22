package org.gestioncomptes.ui;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Component;
import java.util.function.IntConsumer;

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
