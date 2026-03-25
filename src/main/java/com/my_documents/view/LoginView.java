package com.my_documents.view;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView(){
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 20);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel labelUsername = new JLabel("Телефон или Email:");
        labelUsername.setForeground(Color.WHITE);
        labelUsername.setBounds(50, 50, 200, 30);
        panel.add(labelUsername);

        loginField = new JTextField();
        loginField.setBounds(50, 80, 300, 30);
        panel.add(loginField);

        JLabel labelPassword = new JLabel("Пароль:");
        labelPassword.setForeground(Color.WHITE);
        labelPassword.setBounds(50, 120, 200, 30);
        panel.add(labelPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 150, 300, 30);
        panel.add(passwordField);

        loginButton = new JButton("Войти");
        loginButton.setBounds(50, 200, 300, 30);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        panel.add(loginButton);

        add(panel);
    }

    public String getLogin() {
        return loginField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public void showPopup(Component parent, String errorText) {
        JDialog popup = new JDialog();
        popup.setTitle("Ошибка");
        popup.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        popup.setSize(300, 200);
        popup.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel(errorText);
        panel.add(label, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Закрыть");
        closeBtn.addActionListener(e -> popup.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        popup.add(panel);
        popup.setVisible(true);
    }
}
