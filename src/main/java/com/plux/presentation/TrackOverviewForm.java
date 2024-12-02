package com.plux.presentation;

import com.plux.port.api.track.GetTrackAuthorsPort;
import com.plux.port.api.track.GetTrackByIdPort;
import com.plux.presentation.models.TrackAuthorsTableModel;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

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

