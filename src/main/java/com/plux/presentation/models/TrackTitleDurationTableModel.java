package com.plux.presentation.models;

import com.plux.domain.model.Track;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TrackTitleDurationTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Название", "Длительность"};

    public final List<Track> tracks;

    public TrackTitleDurationTableModel(List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public int getRowCount() {
        return tracks.size();
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
            case 0 -> tracks.get(rowIndex).title();
            case 1 -> "%d:%02d".formatted(tracks.get(rowIndex).duration().toMinutes(),
                    tracks.get(rowIndex).duration().toSecondsPart());
            default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
        };
    }
}
