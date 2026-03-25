package com.my_documents.presenter;

import com.my_documents.model.ChatClient;
import com.my_documents.model.EchoPacket;
import com.my_documents.model.HiPacket;
import com.my_documents.view.*;

import javax.swing.*;
import java.io.IOException;

public class LoginPresenter {
    private LoginView view;
    private ChatClient client;

    public LoginPresenter(LoginView view, ChatClient client) {
        this.view = view;
        this.client = client;
        bindEvents();
    }

    private void bindEvents() {
        view.getLoginButton().addActionListener(e -> {
            String login = view.getLogin();
            String password = view.getPassword();

            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Login and password are required!");
                return;
            }
            else{

                HiPacket hi = new HiPacket();
                hi.login = login;
                hi.password = password;
                client.sendMessage(hi);
                Object incomingMessage = null;
                String message;
                    while (true){
                        try {
                            incomingMessage = ChatClient.ois.readObject();
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                            break;
                        } catch (ClassNotFoundException ex) {
                            System.out.println(ex.getMessage());
                            break;
                        }
                        if (incomingMessage != null && incomingMessage instanceof EchoPacket
                                && !((EchoPacket)incomingMessage).text.contains("id=")) {
                            message = ((EchoPacket)incomingMessage).text;
                            view.showPopup(view, message);
                            System.out.println(message);
                            break;
                        } else if (incomingMessage != null && incomingMessage instanceof EchoPacket
                                && ((EchoPacket)incomingMessage).text.contains("id=")) {
                            message = ((EchoPacket)incomingMessage).text;
                            int userId;
                            try {
                                String id = message.substring(message.indexOf("=") + 1).trim();
                                userId = Integer.parseInt(id);
                                ChatClient.currentUserId = userId;
                            } catch (NumberFormatException ex) {
                                System.out.println(ex.getMessage());
                                break;
                            }
                            break;
                        }
                    }
            }


            view.dispose();
            new ChatPresenter(new ChatView(login), client, login);
        });
    }
}
