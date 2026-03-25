package com.my_documents;


import com.formdev.flatlaf.FlatDarkLaf;
import com.my_documents.model.ChatClient;
import com.my_documents.presenter.LoginPresenter;
import com.my_documents.view.LoginView;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("Start client");
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();
            ChatClient client = new ChatClient();
            LoginView loginView = new LoginView();
            new LoginPresenter(loginView, client);
            loginView.setVisible(true);
        });
    }
}
