package com.plux.presentation;

import com.plux.domain.model.Band;
import com.plux.domain.model.Label;
import com.plux.domain.model.Track;
import com.plux.port.api.album.GetAlbumByIdPort;
import com.plux.port.api.album.GetAlbumTracksPort;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AlbumOverviewForm  extends JFrame {
    private JTextField titleTextField;
    private JTable tracksTable;
    private JPanel tracksManagePanel;
    private JButton addTrackButton;
    private JButton deleteTrackButton;
    private JComboBox bandComboBox;
    private JComboBox labelComboBox;
    private JTextField releaseDateTextField;
    private JPanel contentPanel;
    private JComboBox typeComboBox;
    private JScrollPane tracksTablePanel;

    private final Controller controller;
    private final Integer albumId;
    private final GetAlbumByIdPort getAlbumByIdPort;
    private final GetAlbumTracksPort getAlbumTracksPort;

    public AlbumOverviewForm(Controller controller,
                             Integer albumId,
                             GetAlbumByIdPort getAlbumByIdPort,
                             GetAlbumTracksPort getAlbumTracksPort) {
        this.controller = controller;
        this.albumId = albumId;
        this.getAlbumByIdPort = getAlbumByIdPort;
        this.getAlbumTracksPort = getAlbumTracksPort;

        setTitle("Информация об альбоме");
        setContentPane(contentPanel);
        setLocationRelativeTo(null);

        updateData();

        pack();

        tracksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    var model = (TrackTitleDurationTableModel) tracksTable.getModel();
                    controller.viewTrack(model.tracks.get(tracksTable.getSelectedRow()).id());
                }
            }
        });
    }

    void updateData() {
        var album = getAlbumByIdPort.getAlbumById(controller.userId, albumId);

        titleTextField.setText(album.title());
        bandComboBox.setSelectedItem(new BandComboItem(album.band()));
        labelComboBox.setSelectedItem(new LabelComboItem(album.label()));
        typeComboBox.setSelectedIndex(switch (album.albumType()) {
            case SINGLE -> 0;
            case MINI_ALBUM -> 1;
            case AlBUM -> 2;
        });
        releaseDateTextField.setText(album.releaseDate().toString());

        var tracks = getAlbumTracksPort.getAlbumTracks(controller.userId, album);
        tracksTable.setModel(new TrackTitleDurationTableModel(tracks));
    }
}

record BandComboItem(Band band) {
    @Override
    public String toString() {
        return band.name();
    }
}

record LabelComboItem(Label label) {
    @Override
    public String toString() {
        return label.name();
    }
}


class TrackTitleDurationTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Название", "Длительность"};

    final List<Track> tracks;

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