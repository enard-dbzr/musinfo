package com.plux.presentation;

import com.plux.domain.model.Member;
import com.plux.port.api.member.GetMemberByIdPort;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public MemberOverviewForm(Controller controller, Member member, GetMemberByIdPort getMemberByIdPort) {
        this.controller = controller;
        this.member = member;
        this.getMemberByIdPort = getMemberByIdPort;

        setTitle("Информация о человеке");
        setContentPane(contentPanel);
        setLocationRelativeTo(null);
//        setSize(400, 300);

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
    }

    private void createUIComponents() throws ParseException {
        birthdayTextField = new JFormattedTextField(new SimpleDateFormat("dd.MM.yyyy"));
        var countryFormatter = new MaskFormatter("UU");
        countryFormatter.setPlaceholderCharacter('_');
        countryTextField = new JFormattedTextField(countryFormatter);
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

    }

    void updateData() {
        member = getMemberByIdPort.getMemberById(controller.userId, member.id);

        dispNameTextField.setText(member.displayName);
        nameTextField.setText(member.name);
        birthdayTextField.setValue(member.birthday);
        countryTextField.setValue(member.countryCode);
    }
}
