package com.plux.presentation.models;

import com.plux.domain.model.LabelContract;

import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.util.*;

public class ContractsTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Лейбл", "Начало", "Конец"};
    private final static DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.of("ru"));

    public List<LabelContract> contracts = new ArrayList<>();

    public boolean editable = false;

    public void setContracts(List<LabelContract> contracts) {
        this.contracts.clear();
        this.contracts = contracts;
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
        var endDate = contracts.get(rowIndex).endDate();

        var border = new GregorianCalendar(3000, Calendar.JANUARY, 1);

        return switch (columnIndex) {
            case 0 -> contracts.get(rowIndex).label().name();
            case 1 -> df.format(contracts.get(rowIndex).startDate());
            case 2 -> endDate.equals(border.getTime()) ? null : df.format(endDate);
            default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editable && columnIndex > 0;
    }
}
