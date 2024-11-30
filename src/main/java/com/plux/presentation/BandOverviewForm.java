package com.plux.presentation;

import com.plux.domain.model.Album;
import com.plux.domain.model.BandMember;
import com.plux.domain.model.LabelContract;
import com.plux.domain.model.UserRole;
import com.plux.port.api.band.GetBandAlbumsPort;
import com.plux.port.api.band.GetBandContractsPort;
import com.plux.port.api.band.GetBandMembersPort;
import com.plux.port.api.band.GetBandByIdPort;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.*;

class BandOverviewForm extends JDialog {
    private JPanel contentPanel;
    private JTextField bandNameTextField;
    private JTextArea descriptionTextArea;
    private JTable membersTable;
    private JList albumsList;
    private JTable contractsTable;
    private JPanel contractsPanel;
    private JButton addMemberButton;
    private JButton deleteMemberButton;
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

    private final Controller controller;
    private final Integer bandId;
    private final GetBandByIdPort getBandByIdPort;
    private final GetBandMembersPort getBandMembersPort;
    private final GetBandAlbumsPort getBandAlbumsPort;
    private final GetBandContractsPort getBandContractsPort;

    public BandOverviewForm(Controller controller,
                            Integer bandId,
                            GetBandByIdPort getBandByIdPort,
                            GetBandMembersPort getBandMembersPort,
                            GetBandAlbumsPort getBandAlbumsPort,
                            GetBandContractsPort getBandContractsPort) {
        this.controller = controller;
        this.bandId = bandId;
        this.getBandByIdPort = getBandByIdPort;
        this.getBandMembersPort = getBandMembersPort;
        this.getBandAlbumsPort = getBandAlbumsPort;
        this.getBandContractsPort = getBandContractsPort;

        setTitle("Информация о группе");
        setContentPane(contentPanel);
        setSize(640, 480);
        setLocationRelativeTo(null);

        membersHeaderPanel.add(membersTable.getTableHeader());
        contractsHeaderPanel.add(contractsTable.getTableHeader());

        boolean showAdditionalPanels = !controller.user.role().equals(UserRole.GUEST);
        membersPanel.setVisible(showAdditionalPanels);
        contractsPanel.setVisible(showAdditionalPanels);

        boolean showManagePanels = controller.user.role().equals(UserRole.MANAGER);
        albumsManagePanel.setVisible(showManagePanels);
        membersManagePanel.setVisible(showManagePanels);
        contractsManagePanel.setVisible(showManagePanels);


        updateData();
        contractsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getClickCount() == 2) {
                    var model = (ContractsTableModel)contractsTable.getModel();

                    var contract = model.contracts.get(contractsTable.getSelectedRow());
                    controller.viewLabel(contract.label().id());
                }
            }
        });
    }

    private void updateData() {
        var band = getBandByIdPort.GetBandById(controller.userId, bandId);

        bandNameTextField.setText(band.name());
        descriptionTextArea.setText(band.description());

        var albums = getBandAlbumsPort.getBandAlbums(controller.userId, band);
        albumsList.setListData(albums.stream().map(AlbumListItem::new).toArray());

        if (!controller.user.role().equals(UserRole.GUEST)) {
            var bandMembers = getBandMembersPort.getBandMembers(controller.userId, band);
            membersTable.setModel(new BandMembersTableModel(bandMembers));

            var bandContracts = getBandContractsPort.getBandContracts(controller.userId, band);
            contractsTable.setModel(new ContractsTableModel(bandContracts));
        }

    }
}


record AlbumListItem(Album album) {
    @Override
    public String toString() {
        return album.title();
    }
}


class BandMembersTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Имя", "Роль", "Начало", "Конец"};
    private final static DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.of("ru"));
    private final List<BandMember> members;

    public BandMembersTableModel(List<BandMember> members) {
        this.members = members;
    }

    @Override
    public int getRowCount() {
        return members.size();
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
        var ed = members.get(rowIndex).endDate();
        var border = new GregorianCalendar(3000, Calendar.JANUARY, 1);
        return switch (columnIndex) {
            case 0 -> members.get(rowIndex).member().displayName();
            case 1 -> members.get(rowIndex).role();
            case 2 -> df.format(members.get(rowIndex).startDate());
            case 3 -> ed.equals(border.getTime()) ? "-" : df.format(ed);
            default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
//        return columnIndex > 0;
        return false;
    }

//    @Override
//    public void setValueAt(Object value, int rowIndex, int columnIndex) {
//        data.get(rowIndex)[columnIndex] = value;
//        fireTableCellUpdated(rowIndex, columnIndex);
//    }
//
//    public void addEmptyRow() {
//        data.add(new Object[]{"", "", new Date(), new Date()});
//        fireTableRowsInserted(data.size() - 1, data.size() - 1);
//    }
}


class ContractsTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Лейбл", "Начало", "Конец"};
    private final static DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.of("ru"));

    final List<LabelContract> contracts;

    public ContractsTableModel(List<LabelContract> contracts) {
        this.contracts = contracts;
    }

    @Override
    public int getRowCount() {
        return contracts.size();
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
        var endDate = contracts.get(rowIndex).endDate();

        var border = new GregorianCalendar(3000, Calendar.JANUARY, 1);

        return switch (columnIndex) {
            case 0 -> contracts.get(rowIndex).label().name();
            case 1 -> df.format(contracts.get(rowIndex).startDate());
            case 2 -> endDate.equals(border.getTime()) ? "-" : df.format(endDate);
            default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 0;
    }
}