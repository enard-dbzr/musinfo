package com.plux.presentation;

import com.plux.domain.model.Album;
import com.plux.domain.model.AlbumType;
import com.plux.port.api.album.GetAlbumByIdPort;
import com.plux.port.api.album.GetAlbumTracksPort;
import com.plux.port.api.album.SaveAlbumPort;
import com.plux.port.api.band.GetAllLabelsPort;
import com.plux.presentation.components.OptionalFormatter;
import com.plux.presentation.models.LabelListItem;
import com.plux.presentation.models.TrackTitleDurationTableModel;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AlbumOverviewForm  extends JFrame {
    private JTextField titleTextField;
    private JTable tracksTable;
    private JPanel tracksManagePanel;
    private JButton addTrackButton;
    private JButton deleteTrackButton;
    private JComboBox<LabelListItem> labelComboBox;
    private JFormattedTextField releaseDateTextField;
    private JPanel contentPanel;
    private JComboBox<String> typeComboBox;
    private JScrollPane tracksTablePanel;
    private JPanel managePanel;
    private JButton editButton;
    private JButton removeButton;
    private JButton saveButton;
    private JTextField bandTextField;

    private final Controller controller;
    private Album album;
    private final GetAlbumByIdPort getAlbumByIdPort;
    private final GetAlbumTracksPort getAlbumTracksPort;
    private final GetAllLabelsPort getAllLabelsPort;
    private final SaveAlbumPort saveAlbumPort;

    public AlbumOverviewForm(Controller controller,
                             Album album,
                             GetAlbumByIdPort getAlbumByIdPort,
                             GetAlbumTracksPort getAlbumTracksPort,
                             GetAllLabelsPort getAllLabelsPort,
                             SaveAlbumPort saveAlbumPort) {
        this.controller = controller;
        this.album = album;
        this.getAlbumByIdPort = getAlbumByIdPort;
        this.getAlbumTracksPort = getAlbumTracksPort;
        this.getAllLabelsPort = getAllLabelsPort;
        this.saveAlbumPort = saveAlbumPort;

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

        editButton.addActionListener(e -> setEditing(true));
        saveButton.addActionListener(e -> save());
    }

    private void createUIComponents() {
        releaseDateTextField = new JFormattedTextField(new OptionalFormatter(new DateFormatter(new SimpleDateFormat("dd.MM.yyyy"))));
    }

    void setEditing(boolean enable) {
        editButton.setVisible(!enable);
        removeButton.setVisible(!enable);
        saveButton.setVisible(enable);

        titleTextField.setEditable(enable);
        labelComboBox.setEnabled(enable);
        releaseDateTextField.setEditable(enable);
        typeComboBox.setEnabled(enable);
        tracksManagePanel.setVisible(enable);
    }

    void save() {
        if (album == null)
            album = new Album();

        album.title = titleTextField.getText();
        album.releaseDate = (Date) releaseDateTextField.getValue();
        album.albumType = switch (typeComboBox.getSelectedIndex()) {
            case 0 -> AlbumType.SINGLE;
            case 1 -> AlbumType.MINI_ALBUM;
            case 2 -> AlbumType.AlBUM;
            default -> throw new IllegalStateException("Unexpected value: " + typeComboBox.getSelectedIndex());
        };
        if (labelComboBox.getSelectedItem() instanceof LabelListItem labelListItem)
            album.label = labelListItem.label();

        album = saveAlbumPort.saveAlbum(controller.userId, album);

        setEditing(false);
        updateData();
    }

    void updateData() {
        var labels = getAllLabelsPort.getAllLabels(controller.userId);
        labelComboBox.setModel(new DefaultComboBoxModel<>(labels.stream().map(LabelListItem::new).toArray(LabelListItem[]::new)));

        if (album == null)
            album = new Album();

        album = getAlbumByIdPort.getAlbumById(controller.userId, album.id);

        titleTextField.setText(album.title);
        bandTextField.setText(album.band.name);
        labelComboBox.setSelectedItem(new LabelListItem(album.label));
        typeComboBox.setSelectedIndex(switch (album.albumType) {
            case SINGLE -> 0;
            case MINI_ALBUM -> 1;
            case AlBUM -> 2;
        });
        releaseDateTextField.setValue(album.releaseDate);

        var tracks = getAlbumTracksPort.getAlbumTracks(controller.userId, album);
        tracksTable.setModel(new TrackTitleDurationTableModel(tracks));
    }


}


