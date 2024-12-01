package com.plux.presentation;

import com.plux.domain.model.Track;
import com.plux.domain.model.TrackAuthor;
import com.plux.port.api.track.GetTrackAuthorsPort;
import com.plux.port.api.track.GetTrackByIdPort;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.util.List;

public class TrackOverviewForm extends JFrame {
    private JPanel contentPanel;
    private JTextField titleTextField;
    private JFormattedTextField durationTextField;
    private JButton addAuthorButton;
    private JButton deleteAuthorButton;
    private JTable authorsTable;

    private final Controller controller;
    private final Integer trackId;
    private final GetTrackByIdPort getTrackByIdPort;
    private final GetTrackAuthorsPort getTrackAuthorsPort;

    public TrackOverviewForm(Controller controller,
                             Integer trackId,
                             GetTrackByIdPort getTrackByIdPort,
                             GetTrackAuthorsPort getTrackAuthorsPort) {
        this.controller = controller;
        this.trackId = trackId;
        this.getTrackByIdPort = getTrackByIdPort;
        this.getTrackAuthorsPort = getTrackAuthorsPort;

        setTitle("Информация о треке");
        setContentPane(contentPanel);
        setLocationRelativeTo(null);

        updateData();

        pack();
    }

    void updateData() {
        var track = getTrackByIdPort.getTrackById(controller.userId, trackId);

        titleTextField.setText(track.title());
        durationTextField.setValue("%02d:%02d".formatted(track.duration().toMinutesPart(),
                track.duration().toSecondsPart()));

        var trackAuthors = getTrackAuthorsPort.getTrackAuthors(controller.userId, track);
        authorsTable.setModel(new TrackAuthorsTableModel(trackAuthors));
    }

    private void createUIComponents() throws ParseException {
        var durationFormatter = new MaskFormatter("##:##");
        durationFormatter.setPlaceholderCharacter('_');
        durationTextField = new JFormattedTextField(durationFormatter);
    }
}

class TrackAuthorsTableModel extends AbstractTableModel {
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
            case 0 -> tracksAuthors.get(rowIndex).band().name();
            case 1 -> tracksAuthors.get(rowIndex).role();
            default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
        };
    }
}
