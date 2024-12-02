package com.plux.presentation.models;

import com.plux.domain.model.Band;
import com.plux.domain.model.BandMember;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class BandMembersTableModel extends AbstractTableModel {
    private final static String[] columnNames = {"Имя", "Роль", "Начало", "Конец"};
    private final static DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.of("ru"));

    public boolean editable = false;
    public List<BandMember> members = new ArrayList<>();

    private final Set<BandMember> modified = new HashSet<>();
    private final Set<BandMember> removed = new HashSet<>();

    private final Set<Integer> createdRows = new HashSet<>();

    public void reset(List<BandMember> members) {
        this.members = members;

        this.modified.clear();
        this.removed.clear();
        this.createdRows.clear();
    }

    public List<BandMember> getModified(Band band) {
        for (var bm : modified) {
            bm.setBand(band);
        }
        return modified.stream().filter(bm -> !removed.contains(bm)).collect(Collectors.toList());
    }

    public List<BandMember> getRemoved(Band band) {
        for (var bm : removed) {
            bm.setBand(band);
        }
        return removed.stream().filter(bm -> bm.getId() != null).collect(Collectors.toList());
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
        var ed = members.get(rowIndex).getEndDate();
        var border = new GregorianCalendar(3000, Calendar.JANUARY, 1);
        return switch (columnIndex) {
            case 0 -> members.get(rowIndex).getMember() == null ? "" : members.get(rowIndex).getMember().displayName();
            case 1 -> members.get(rowIndex).getRole();
            case 2 -> df.format(members.get(rowIndex).getStartDate());
            case 3 -> ed.equals(border.getTime()) ? null : df.format(ed);
            default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editable && columnIndex > 0 || createdRows.contains(rowIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0 && aValue instanceof MemberListItem memberListItem) {
            members.get(rowIndex).setMember(memberListItem.member());
        } else if (columnIndex == 1) {
            members.get(rowIndex).setRole(aValue.toString());
        } else if (columnIndex == 2) {
            try {
                members.get(rowIndex).setStartDate(df.parse(aValue.toString()));
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Введите корректную дату");
            }
        } else if (columnIndex == 3) {
            if (aValue.toString().equals("__.__.____"))
                return;

            try {
                members.get(rowIndex).setEndDate(df.parse(aValue.toString()));
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null, "Введите корректную дату");
            }
        }
        modified.add(members.get(rowIndex));
    }

    public void addEmptyRow() {
        var member = new BandMember();
        members.add(member);
        modified.add(member);

        var i = members.size() - 1;
        fireTableRowsInserted(i, i);
        createdRows.add(i);
    }

    public void deleteRow(int row) {
        var member = members.get(row);
        removed.add(member);
        members.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
