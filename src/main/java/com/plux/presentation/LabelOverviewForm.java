package com.plux.presentation;

import com.plux.domain.model.Label;
import com.plux.port.api.label.GetLabelByIdPort;
import com.plux.port.api.label.SaveLabelPort;

import javax.swing.*;
import java.util.function.Consumer;

public class LabelOverviewForm extends JFrame {
    private JTextField labelNameTextField;
    private JTextArea descriptionTextArea;
    private JTextField addressTextField;
    private JTextArea contactInfoTextArea;
    private JPanel contentPanel;
    private JPanel managePanel;
    private JButton editButton;
    private JButton removeButton;
    private JButton saveButton;

    private final Controller controller;
    private Label label;
    private final GetLabelByIdPort getLabelByIdPort;
    private final SaveLabelPort saveLabelPort;

    public Consumer<Label> createLabelEventListener;

    public LabelOverviewForm(Controller controller,
                             Label label,
                             GetLabelByIdPort getLabelByIdPort,
                             SaveLabelPort saveLabelPort) {
        this.controller = controller;
        this.label = label;
        this.getLabelByIdPort = getLabelByIdPort;
        this.saveLabelPort = saveLabelPort;

        setTitle("Информация о лейбле");
        setContentPane(contentPanel);
        setSize(450, 400);
        setLocationRelativeTo(null);

        editButton.addActionListener(e -> setEditing(true));
        saveButton.addActionListener(e -> save());

        updateData();
    }

    void updateData() {
        if (label == null) return;

        label = getLabelByIdPort.getLabelById(controller.userId, label.id);

        labelNameTextField.setText(label.name);
        descriptionTextArea.setText(label.description);
        addressTextField.setText(label.address);
        contactInfoTextArea.setText(label.contactInfo);
    }

    void setEditing(boolean enable) {
        editButton.setVisible(!enable);
        removeButton.setVisible(!enable);
        saveButton.setVisible(enable);

        labelNameTextField.setEditable(enable);
        descriptionTextArea.setEditable(enable);
        addressTextField.setEditable(enable);
        contactInfoTextArea.setEditable(enable);
    }

    void save() {
        if (label == null)
            label = new Label();

        label.name = labelNameTextField.getText();
        label.description = descriptionTextArea.getText();
        label.address = addressTextField.getText();
        label.contactInfo = contactInfoTextArea.getText();

        label = saveLabelPort.saveLabel(controller.userId, label);

        if (createLabelEventListener != null)
            createLabelEventListener.accept(label);

        setEditing(false);
        updateData();
    }
}
