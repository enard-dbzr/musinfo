package com.plux.presentation;

import com.plux.port.api.AuthPort;
import com.plux.port.api.DbError;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LoginForm extends JFrame {
    private JPanel contentPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private final Controller controller;
    private final AuthPort authPort;

    public LoginForm(Controller controller, AuthPort authPort) {
        this.controller = controller;
        this.authPort = authPort;

        setTitle("Авторизация");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPanel);
        setSize(400, 200);
        setLocationRelativeTo(null);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButtonClick();
            }
        });
    }

    private void loginButtonClick() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());

        try {
            var userId = authPort.authenticate(login, password);

            controller.authSuccess(userId);
        } catch (DbError e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}
