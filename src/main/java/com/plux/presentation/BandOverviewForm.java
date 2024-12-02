package com.plux.presentation;

import com.plux.domain.model.*;
import com.plux.port.api.band.*;
import com.plux.presentation.components.ScrollablePanel;
import com.plux.presentation.models.*;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

class BandOverviewForm extends JDialog {
    private JPanel contentPanel;
    private JTextField bandNameTextField;
    private JTextArea descriptionTextArea;
    private JTable membersTable;
    private JList albumsList;
    private JTable contractsTable;
    private JPanel contractsPanel;
    private JButton addMemberButton;
    private JButton removeMemberButton;
    private JButton CreateMemberButton;
    private JPanel membersManagePanel;
    private JPanel descriptionPanel;
    private JPanel membersPanel;
    private JPanel membersHeaderPanel;
    private JPanel albumsPanel;
    private JButton createAlbumButton;
    private JButton addContractButton;
    private JButton deleteContractButton;
    private JPanel contractsHeaderPanel;
    private JPanel albumsManagePanel;
    private JPanel contractsManagePanel;
    private JButton editButton;
    private JButton removeButton;
    private JPanel managePanel;
    private JButton saveButton;
    private JPanel scrollContentPanel;

    private final Controller controller;
    private Band band;
    private final GetBandByIdPort getBandByIdPort;
    private final GetBandMembersPort getBandMembersPort;
    private final GetBandAlbumsPort getBandAlbumsPort;
    private final GetBandContractsPort getBandContractsPort;
    private final SaveBandPort saveBandPort;
    private final GetAllMembersPort getAllMembersPort;
    private final SaveBandMemberPort saveBandMemberPort;
    private final RemoveBandMemberPort removeBandMemberPort;

    private BandMembersTableModel bandMembersTableModel = new BandMembersTableModel();
    private final ContractsTableModel contractsTableModel = new ContractsTableModel();


    public BandOverviewForm(Controller controller,
                            Band band,
                            GetBandByIdPort getBandByIdPort,
                            GetBandMembersPort getBandMembersPort,
                            GetBandAlbumsPort getBandAlbumsPort,
                            GetBandContractsPort getBandContractsPort,
                            SaveBandPort saveBandPort,
                            GetAllMembersPort getAllMembersPort,
                            SaveBandMemberPort saveBandMemberPort,
                            RemoveBandMemberPort removeBandMemberPort) {
        this.controller = controller;
        this.band = band;
        this.getBandByIdPort = getBandByIdPort;
        this.getBandMembersPort = getBandMembersPort;
        this.getBandAlbumsPort = getBandAlbumsPort;
        this.getBandContractsPort = getBandContractsPort;
        this.saveBandPort = saveBandPort;
        this.getAllMembersPort = getAllMembersPort;
        this.saveBandMemberPort = saveBandMemberPort;
        this.removeBandMemberPort = removeBandMemberPort;

        setTitle("Информация о группе");
        setContentPane(contentPanel);
        setLocationRelativeTo(null);

        membersHeaderPanel.add(membersTable.getTableHeader());
        contractsHeaderPanel.add(contractsTable.getTableHeader());
        membersTable.setModel(bandMembersTableModel);
        contractsTable.setModel(contractsTableModel);

        boolean showAdditionalPanels = !controller.user.role().equals(UserRole.GUEST);
        membersPanel.setVisible(showAdditionalPanels);
        contractsPanel.setVisible(showAdditionalPanels);

        managePanel.setVisible(controller.user.role().equals(UserRole.MANAGER));

        try {
            var dateFormatter = new MaskFormatter("##.##.####");
            dateFormatter.setPlaceholderCharacter('_');

            var dateTextField = new JFormattedTextField(dateFormatter);

            membersTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(dateTextField));
            membersTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(dateTextField));

            contractsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(dateTextField));
            contractsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(dateTextField));
        } catch (ParseException _) {}

        updateData();

        pack();

        contractsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    var contract = contractsTableModel.contracts.get(contractsTable.getSelectedRow());
                    controller.viewLabel(contract.label().id());
                }
            }
        });
        albumsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    var albumItem = (AlbumListItem)albumsList.getSelectedValue();

                    controller.viewAlbum(albumItem.album().id());
                }
            }
        });
        membersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    var member = bandMembersTableModel.members.get(membersTable.getSelectedRow());
                    controller.viewMember(member.getId());
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterEditMode();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bandMembersTableModel.addEmptyRow();
            }
        });
        removeMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bandMembersTableModel.deleteRow(membersTable.getSelectedRow());
            }
        });
    }

    void updateData() {
        if (controller.user.role().equals(UserRole.MANAGER)) {
            var members = getAllMembersPort.getAllMembers(controller.userId);

            var membersCombo = new JComboBox<>(members.stream().map(MemberListItem::new).toArray());
            membersTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(membersCombo));
        }

        if (band == null) return;

        band = getBandByIdPort.GetBandById(controller.userId, band.getId());

        bandNameTextField.setText(band.getName());
        descriptionTextArea.setText(band.getDescription());

        var albums = getBandAlbumsPort.getBandAlbums(controller.userId, band);
        albumsList.setListData(albums.stream().map(AlbumListItem::new).toArray());

        if (!controller.user.role().equals(UserRole.GUEST)) {
            var bandMembers = getBandMembersPort.getBandMembers(controller.userId, band);
            bandMembersTableModel.reset(bandMembers);

            var bandContracts = getBandContractsPort.getBandContracts(controller.userId, band);
            contractsTableModel.setContracts(bandContracts);

            membersTable.setModel(bandMembersTableModel);
            contractsTable.setModel(contractsTableModel);
        }
    }

    private void setEditing(boolean enable) {
        bandNameTextField.setEditable(enable);
        descriptionTextArea.setEditable(enable);
        if (membersTable.getModel() instanceof BandMembersTableModel model) {
            model.editable = enable;
        }
        if (contractsTable.getModel() instanceof ContractsTableModel model) {
            model.editable = enable;
        }

        albumsManagePanel.setVisible(enable);
        membersManagePanel.setVisible(enable);
        contractsManagePanel.setVisible(enable);

        editButton.setVisible(!enable);
        removeButton.setVisible(!enable);
        saveButton.setVisible(enable);
    }

    private void setCreating(boolean enable) {
        albumsPanel.setVisible(!enable);
        membersPanel.setVisible(!enable);
        contractsPanel.setVisible(!enable);
    }

    void enterEditMode() {
        setEditing(true);
    }

    void save() {
        if (band == null)
            band = new Band();

        band.setName(bandNameTextField.getText());
        band.setDescription(descriptionTextArea.getText());

        band = saveBandPort.saveBand(controller.userId, band);

        for (var bm : bandMembersTableModel.getModified(band)) {
            saveBandMemberPort.saveBandMember(controller.userId, bm);
        }
        for (var bm : bandMembersTableModel.getRemoved(band)) {
            removeBandMemberPort.removeBandMember(controller.userId, bm);
        }

        setEditing(false);
        updateData();
    }

    private void createUIComponents() {
        scrollContentPanel = new ScrollablePanel();
    }
}


