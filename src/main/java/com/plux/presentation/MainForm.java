package com.plux.presentation;

import com.plux.domain.model.Band;
import com.plux.domain.model.UserRole;
import com.plux.port.api.SearchBandsPort;
import com.plux.presentation.models.BandListItem;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

class MainForm extends JFrame {
    private JPanel contentPanel;
    private JTextField searchGroupsTextField;
    private JList bandsList;
    private JButton logoutButton;
    private JLabel userLabel;
    private JButton addButton;

    private final Controller controller;
    private final SearchBandsPort searchBandsPort;

    private List<Band> bands;

    public MainForm(Controller controller, SearchBandsPort searchBandsPort) {
        this.controller = controller;
        this.searchBandsPort = searchBandsPort;

        setTitle("Группы");
        setContentPane(contentPanel);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userLabel.setText("%s (%s)".formatted(controller.user.name(), controller.user.role()));
        addButton.setVisible(controller.user.role().equals(UserRole.MANAGER));

        SearchBands();

        searchGroupsTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SearchBands();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SearchBands();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SearchBands();
            }
        });

        bandsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    if (bandsList.getSelectedValue() instanceof BandListItem bandItem) {
                        controller.viewBand(bandItem.band());
                    }
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                var form = controller.viewBand(null);

                form.setEditing(true);
            }
        });
    }

    private void SearchBands() {
        bands = searchBandsPort.Search(controller.userId, searchGroupsTextField.getText());
        bandsList.setListData(bands.stream().map(BandListItem::new).toArray());
    }
}
