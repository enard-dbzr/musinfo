package com.plux.presentation.models;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class RowSpecifiedCellEditor extends AbstractCellEditor implements TableCellEditor {
    public DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField());
    public DefaultCellEditor specificCellEditor = new DefaultCellEditor(new JTextField());

    private final Set<Integer> specificRows = new HashSet<>();
    private DefaultCellEditor lastSelected;

    public void selectSpecificRow(Integer rowIndex) {
        specificRows.add(rowIndex);
    }

    public void clear() {
        specificRows.clear();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        lastSelected = specificRows.contains(row) ? specificCellEditor : defaultCellEditor;
        return lastSelected.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return lastSelected.getCellEditorValue();
    }
}
