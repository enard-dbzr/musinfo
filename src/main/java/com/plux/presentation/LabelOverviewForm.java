package com.plux.presentation;

import com.plux.port.api.label.GetLabelByIdPort;

import javax.swing.*;
import java.awt.*;

public class LabelOverviewForm extends JFrame {
    private JTextField labelNameTextField;
    private JTextArea descriptionTextArea;
    private JTextField addressTextField;
    private JTextArea contactInfoTextArea;
    private JPanel contentPanel;

    private final Controller controller;
    private final Integer labelId;
    private final GetLabelByIdPort getLabelByIdPort;

    public LabelOverviewForm(Controller controller,
                             Integer labelId,
                             GetLabelByIdPort getLabelByIdPort) {
        this.controller = controller;
        this.labelId = labelId;
        this.getLabelByIdPort = getLabelByIdPort;

        setTitle("Информация о лейбле");
        setContentPane(contentPanel);
        setSize(450, 400);
        setLocationRelativeTo(null);

        updateData();
    }

    void updateData() {
        var label = getLabelByIdPort.getLabelById(controller.userId, labelId);

        labelNameTextField.setText(label.name());
        descriptionTextArea.setText(label.description());
        addressTextField.setText(label.address());
        contactInfoTextArea.setText(label.contactInfo());
    }
}
