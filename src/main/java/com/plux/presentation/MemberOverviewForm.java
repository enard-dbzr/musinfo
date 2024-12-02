package com.plux.presentation;

import com.plux.domain.model.Member;
import com.plux.port.api.member.DeleteMemberPort;
import com.plux.port.api.member.GetMemberByIdPort;
import com.plux.port.api.member.SaveMemberPort;
import com.plux.presentation.components.OptionalFormatter;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;

public class MemberOverviewForm extends JFrame {
    private JPanel contentPanel;
    private JTextField dispNameTextField;
    private JFormattedTextField birthdayTextField;
    private JTextField nameTextField;
    private JFormattedTextField countryTextField;
    private JPanel managePanel;
    private JButton editButton;
    private JButton removeButton;
    private JButton saveButton;

    private final Controller controller;
    private Member member;
    private final GetMemberByIdPort getMemberByIdPort;
    private final SaveMemberPort saveMemberPort;
    private final DeleteMemberPort deleteMemberPort;

    public MemberOverviewForm(Controller controller,
                              Member member,
                              GetMemberByIdPort getMemberByIdPort,
                              SaveMemberPort saveMemberPort,
                              DeleteMemberPort deleteMemberPort) {
        this.controller = controller;
        this.member = member;
        this.getMemberByIdPort = getMemberByIdPort;
        this.saveMemberPort = saveMemberPort;
        this.deleteMemberPort = deleteMemberPort;

        setTitle("Информация о человеке");
        setContentPane(contentPanel);
        setLocationRelativeTo(null);

        updateData();

        pack();

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
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMemberPort.deleteMember(controller.userId, member);
                setVisible(false);
            }
        });
    }

    private void createUIComponents() throws ParseException {
        birthdayTextField = new JFormattedTextField(new OptionalFormatter(new DateFormatter(new SimpleDateFormat("dd.MM.yyyy"))));
        countryTextField = new JFormattedTextField(new OptionalFormatter(new MaskFormatter("UU")));
    }

    void setEditing(boolean enable) {
        dispNameTextField.setEditable(enable);
        nameTextField.setEditable(enable);
        birthdayTextField.setEditable(enable);
        countryTextField.setEditable(enable);

        editButton.setVisible(!enable);
        removeButton.setVisible(!enable);
        saveButton.setVisible(enable);
    }

    void save() {
        member.displayName = dispNameTextField.getText();
        member.name = nameTextField.getText();
        member.birthday = (Date) birthdayTextField.getValue();
        member.countryCode = (String) countryTextField.getValue();

        member = saveMemberPort.saveMember(controller.userId, member);

        setEditing(false);
        updateData();
    }

    void updateData() {
        if (member == null) return;

        member = getMemberByIdPort.getMemberById(controller.userId, member.id);

        dispNameTextField.setText(member.displayName);
        nameTextField.setText(member.name);
        birthdayTextField.setValue(member.birthday);
        countryTextField.setValue(member.countryCode);
    }
}
