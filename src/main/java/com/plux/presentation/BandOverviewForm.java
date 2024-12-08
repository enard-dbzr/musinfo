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
import java.util.function.Consumer;

class BandOverviewForm extends JFrame {
    private JPanel contentPanel;
    private JTextField bandNameTextField;
    private JTextArea descriptionTextArea;
    private JTable membersTable;
    private JList<AlbumListItem> albumsList;
    private JTable contractsTable;
    private JPanel contractsPanel;
    private JButton addMemberButton;
    private JButton removeMemberButton;
    private JButton createMemberButton;
    private JPanel membersManagePanel;
    private JPanel descriptionPanel;
    private JPanel membersPanel;
    private JPanel membersHeaderPanel;
    private JPanel albumsPanel;
    private JButton createAlbumButton;
    private JButton addContractButton;
    private JButton removeContractButton;
    private JPanel contractsHeaderPanel;
    private JPanel albumsManagePanel;
    private JPanel contractsManagePanel;
    private JButton editButton;
    private JButton removeButton;
    private JPanel managePanel;
    private JButton saveButton;
    private JPanel scrollContentPanel;
    private JButton createLabelButton;

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
    private final GetAllLabelsPort getAllLabelsPort;
    private final SaveLabelContractPort saveLabelContractPort;
    private final DeleteLabelContractPort deleteLabelContractPort;

    private BandMembersTableModel bandMembersTableModel = new BandMembersTableModel();
    private final ContractsTableModel contractsTableModel = new ContractsTableModel();

    public Consumer<Band> createModelEventListener;

    public BandOverviewForm(Controller controller,
                            Band band,
                            GetBandByIdPort getBandByIdPort,
                            GetBandMembersPort getBandMembersPort,
                            GetBandAlbumsPort getBandAlbumsPort,
                            GetBandContractsPort getBandContractsPort,
                            SaveBandPort saveBandPort,
                            GetAllMembersPort getAllMembersPort,
                            SaveBandMemberPort saveBandMemberPort,
                            RemoveBandMemberPort removeBandMemberPort,
                            GetAllLabelsPort getAllLabelsPort,
                            SaveLabelContractPort saveLabelContractPort,
                            DeleteLabelContractPort deleteLabelContractPort){
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
        this.getAllLabelsPort = getAllLabelsPort;
        this.saveLabelContractPort = saveLabelContractPort;
        this.deleteLabelContractPort = deleteLabelContractPort;

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
                    controller.viewLabel(contract.getLabel());
                }
            }
        });
        albumsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    var albumItem = (AlbumListItem)albumsList.getSelectedValue();

                    controller.viewAlbum(albumItem.album());
                }
            }
        });
        membersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    var bandMember = bandMembersTableModel.members.get(membersTable.getSelectedRow());
                    controller.viewMember(bandMember.getMember());
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEditing(true);
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        addMemberButton.addActionListener(e -> bandMembersTableModel.addEmptyRow());
        removeMemberButton.addActionListener(e -> bandMembersTableModel.deleteRow(membersTable.getSelectedRow()));
        createMemberButton.addActionListener(e -> {
            var memberForm = controller.viewMember(null);
            memberForm.setEditing(true);
            memberForm.createModelEventListener = member -> {
                memberForm.setVisible(false);
                bandMembersTableModel.addRowWithMember(member);
            };
        });

        addContractButton.addActionListener(e -> contractsTableModel.addEmptyRow());
        removeContractButton.addActionListener(e -> contractsTableModel.deleteRow(contractsTable.getSelectedRow()));
        createLabelButton.addActionListener(e -> {
            var labelForm = controller.viewLabel(null);
            labelForm.setEditing(true);
            labelForm.createLabelEventListener = label -> {
                labelForm.setVisible(false);
                contractsTableModel.addRowWithLabel(label);
            };
        });
    }

    void updateData() {
        if (controller.user.role().equals(UserRole.MANAGER)) {
            var members = getAllMembersPort.getAllMembers(controller.userId);
            var labels = getAllLabelsPort.getAllLabels(controller.userId);

            var membersCombo = new JComboBox<>(members.stream().map(MemberListItem::new).toArray());
            var labelsCombo = new JComboBox<>(labels.stream().map(LabelListItem::new).toArray());

            membersTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(membersCombo));
            contractsTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(labelsCombo));
        }

        if (band == null) return;

        band = getBandByIdPort.GetBandById(controller.userId, band.id);

        bandNameTextField.setText(band.name);
        descriptionTextArea.setText(band.description);

        var albums = getBandAlbumsPort.getBandAlbums(controller.userId, band);
        albumsList.setListData(albums.stream().map(AlbumListItem::new).toArray((AlbumListItem[]::new)));

        if (!controller.user.role().equals(UserRole.GUEST)) {
            var bandMembers = getBandMembersPort.getBandMembers(controller.userId, band);
            bandMembersTableModel.reset(bandMembers);

            var bandContracts = getBandContractsPort.getBandContracts(controller.userId, band);
            contractsTableModel.reset(bandContracts);
        }
    }

    void setEditing(boolean enable) {
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

    void save() {
        if (band == null)
            band = new Band();

        band.name = bandNameTextField.getText();
        band.description = descriptionTextArea.getText();

        band = saveBandPort.saveBand(controller.userId, band);

        if (createModelEventListener != null)
            createModelEventListener.accept(band);

        for (var bm : bandMembersTableModel.getModified(band)) {
            saveBandMemberPort.saveBandMember(controller.userId, bm);
        }
        for (var bm : bandMembersTableModel.getRemoved(band)) {
            removeBandMemberPort.removeBandMember(controller.userId, bm);
        }
        for (var lc : contractsTableModel.getModified(band)) {
            saveLabelContractPort.saveLabelContract(controller.userId, lc);
        }
        for (var lc : contractsTableModel.getRemoved(band)) {
            deleteLabelContractPort.deleteLabelContract(controller.userId, lc);
        }

        setEditing(false);
        updateData();
    }

    private void createUIComponents() {
        scrollContentPanel = new ScrollablePanel();
    }
}


