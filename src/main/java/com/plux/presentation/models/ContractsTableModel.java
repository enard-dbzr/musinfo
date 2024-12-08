package com.plux.presentation.models;

import com.plux.domain.model.Band;
import com.plux.domain.model.BandMember;
import com.plux.domain.model.Label;
import com.plux.domain.model.LabelContract;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ContractsTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Лейбл", "Начало", "Конец"};

    public List<ContractTableItem> contracts = new ArrayList<>();

    private final Set<ContractTableItem> modified  = new HashSet<>();
    private final Set<ContractTableItem> removed = new HashSet<>();

    private final Set<Integer> createdRows = new HashSet<>();

    public boolean editable = false;

    public void reset(List<LabelContract> contracts) {
        this.contracts = contracts.stream().map(ContractTableItem::from).collect(Collectors.toList());

        modified.clear();
        removed.clear();
        createdRows.clear();
    }

    public List<LabelContract> getModified(Band band) {
        return modified.stream().
                filter(val -> !removed.contains(val)).
                map(val -> val.constructModel(band)).
                collect(Collectors.toList());
    }

    public List<LabelContract> getRemoved(Band band) {
        return removed.stream().
                filter(val -> val.constructModel(band).id != null).
                map(val -> val.constructModel(band)).
                collect(Collectors.toList());
    }

    @Override
    public int getRowCount() {
        return contracts.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return switch (columnIndex) {
            case 0 -> contracts.get(rowIndex).getLabelName();
            case 1 -> contracts.get(rowIndex).getStart();
            case 2 -> contracts.get(rowIndex).getEnd();
            default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            if (columnIndex == 0 && aValue instanceof LabelListItem labelListItem) {
                contracts.get(rowIndex).setLabel(labelListItem.label());
            } else if (columnIndex == 1) {
                contracts.get(rowIndex).setStart(aValue.toString());
            } else if (columnIndex == 2) {
                if (aValue.toString().equals("__.__.____"))
                    return;

                contracts.get(rowIndex).setEnd(aValue.toString());
            }
            modified.add(contracts.get(rowIndex));
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Введите корректную дату");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editable && columnIndex > 0 || createdRows.contains(rowIndex);
    }

    public int addEmptyRow() {
        var row = new ContractTableItem();
        contracts.add(row);
        modified.add(row);

        var i = contracts.size() - 1;
        fireTableRowsInserted(i, i);
        createdRows.add(i);
        return i;
    }

    public int addRowWithLabel(Label label) {
        var i = addEmptyRow();
        createdRows.remove(i);

        contracts.get(i).setLabel(label);
        return i;
    }

    public void deleteRow(int rowIndex) {
        var row = contracts.get(rowIndex);
        removed.add(row);
        contracts.remove(row);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}
