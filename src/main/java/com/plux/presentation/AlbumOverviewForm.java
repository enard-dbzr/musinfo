package com.plux.presentation;

import com.plux.port.api.album.GetAlbumByIdPort;
import com.plux.port.api.album.GetAlbumTracksPort;
import com.plux.presentation.models.BandListItem;
import com.plux.presentation.models.LabelListItem;
import com.plux.presentation.models.TrackTitleDurationTableModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        bandComboBox.setSelectedItem(new BandListItem(album.band()));
        labelComboBox.setSelectedItem(new LabelListItem(album.label()));
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


