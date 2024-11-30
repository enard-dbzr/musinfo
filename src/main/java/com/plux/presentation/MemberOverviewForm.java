package com.plux.presentation;

import com.plux.port.api.member.GetMemberByIdPort;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MemberOverviewForm extends JFrame {
    private JPanel contentPanel;
    private JTextField dispNameTextField;
    private JFormattedTextField birthdayTextField;
    private JTextField nameTextField;
    private JFormattedTextField countryTextField;

    private final Controller controller;
    private final Integer memberId;
    private final GetMemberByIdPort getMemberByIdPort;

    public MemberOverviewForm(Controller controller, Integer memberId, GetMemberByIdPort getMemberByIdPort) {
        this.controller = controller;
        this.memberId = memberId;
        this.getMemberByIdPort = getMemberByIdPort;

        setTitle("Информация о человеке");
        setContentPane(contentPanel);
        setLocationRelativeTo(null);
        setSize(400, 300);

        updateData();
    }

    private void createUIComponents() throws ParseException {
        birthdayTextField = new JFormattedTextField(new SimpleDateFormat("dd.MM.yyyy"));
        countryTextField = new JFormattedTextField(new MaskFormatter("UU"));
    }

    void updateData() {
        var member = getMemberByIdPort.getMemberById(controller.userId, memberId);

        dispNameTextField.setText(member.displayName());
        nameTextField.setText(member.name());
        birthdayTextField.setValue(member.birthday());
        countryTextField.setValue(member.countryCode());
    }
}
