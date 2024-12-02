package com.plux.presentation.models;

import com.plux.domain.model.TrackAuthor;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TrackAuthorsTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Группа", "Тип"};

    final List<TrackAuthor> tracksAuthors;

    public TrackAuthorsTableModel(List<TrackAuthor> tracksAuthors) {
        this.tracksAuthors = tracksAuthors;
    }

    @Override
    public int getRowCount() {
        return tracksAuthors.size();
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
            case 0 -> tracksAuthors.get(rowIndex).band().getName();
            case 1 -> tracksAuthors.get(rowIndex).role();
            default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
        };
    }
}
