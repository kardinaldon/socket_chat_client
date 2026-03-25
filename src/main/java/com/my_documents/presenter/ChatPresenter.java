package com.my_documents.presenter;

import com.my_documents.model.*;
import com.my_documents.view.ChatView;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatPresenter {
    private ChatView view;
    private ChatClient client;
    private ArrayList<ListPacket.CorrespondentItem> items;
    private ConcurrentHashMap<Integer, List<MessagePacket>> chatHistory;
    private int currentRecipientId;
    private String currentRecipientLogin;
    private int currentUserId;
    private String currentUserLogin;

    public ChatPresenter(ChatView view, ChatClient client, String currentUserLogin) {
        this.view = view;
        this.client = client;
        this.chatHistory = new ConcurrentHashMap<>();
        this.currentRecipientId = 0;
        this.currentUserId = 0;
        this.currentUserLogin = currentUserLogin;
        this.currentRecipientLogin = "";
        runWorker();
        updateCorrespondentList();
        bindEvents();
        show();
    }

    private void bindEvents() {
        view.getSendButton().addActionListener(e -> {
            String message = view.getMessage();
            if (!message.isEmpty()) {
                if(ChatClient.currentUserId != 0){
                    MessagePacket mp = new MessagePacket(currentUserId, currentRecipientId, message);
                    client.sendMessage(mp);
                    view.clearMessageField();
                    view.appendMessage(currentUserLogin, message, true);
                    if (!chatHistory.containsKey((mp.recipientId))) {
                        chatHistory.put(mp.recipientId, List.of(mp));
                    } else if (chatHistory.containsKey((mp.recipientId))) {
                        List<MessagePacket> messagePackets = new ArrayList<>();
                        messagePackets.addAll(chatHistory.get(mp.recipientId));
                        messagePackets.add(mp);
                        chatHistory.put(mp.recipientId, messagePackets);
                    }
                }
                else {
                    view.showPopup(view, "Вы не можете отправлять сообщения в этот чат");
                    System.out.println("user id = 0");
                }
            }
        });

        view.contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedItem = view.contactList.getSelectedValue();
                Optional<ListPacket.CorrespondentItem> optionalCor = items.stream().filter(corObj -> corObj.login.equals(selectedItem)).findFirst();
                if(optionalCor.isPresent()){
                    currentRecipientId = optionalCor.get().id;
                    currentRecipientLogin = optionalCor.get().login;
                    view.clearChatArea();
                    if(chatHistory.containsKey(currentRecipientId)){
                        List<MessagePacket> messagePackets = chatHistory.get(currentRecipientId);
                        messagePackets.forEach(mp -> {
                            view.appendMessage(mp.senderId == currentUserId ? currentUserLogin : optionalCor.get().login, mp.text, mp.senderId == currentUserId ? true : false);
                        });
                    }

                }
                else{
                    view.showPopup(view, "Выбранный пользователь не зарегистрирован на сервере");
                }
            }
        });
    }

    private void runWorker(){
        try {
            SwingWorker<Void, Object> worker = new SwingWorker<Void, Object>() {
                @Override
                protected Void doInBackground() throws Exception {
                        Object incomingMessage;
                        while ((incomingMessage = ChatClient.ois.readObject()) != null) {
                            publish(incomingMessage);
                        }
                        return null;
                }

                @Override
                protected void process(java.util.List<Object> chunks) {
                    for (Object chunk : chunks) {
                        if (chunk instanceof MessagePacket) {
                            MessagePacket mp = (MessagePacket) chunk;
                            if (!chatHistory.containsKey((mp.senderId))) {
                                chatHistory.put(mp.senderId, List.of(mp));
                            } else if (chatHistory.containsKey((mp.senderId))) {
                                List<MessagePacket> messagePackets = new ArrayList<>(chatHistory.size() + 1);
                                messagePackets.addAll(chatHistory.get(mp.senderId));
                                messagePackets.addLast(mp);
                                chatHistory.computeIfPresent(mp.senderId, (key, value) -> messagePackets);
                            }
                            if (mp.senderId == currentRecipientId | mp.recipientId == currentRecipientId) {
                                Optional<ListPacket.CorrespondentItem> optionalCor = items.stream().filter(corObj -> corObj.id == mp.senderId).findFirst();
                                if (optionalCor.isPresent()) {
                                    view.appendMessage(currentRecipientLogin, mp.text, false);
                                }
                            }
                        } else if (chunk instanceof EchoPacket
                                && !((EchoPacket) chunk).text.equals("successfully") && !((EchoPacket) chunk).text.contains("id=")) {

                            view.showPopup(view, ((EchoPacket) chunk).text);
                        } else if (chunk instanceof ListPacket) {
                            ListPacket correspondents = (ListPacket) chunk;
                            items = correspondents.items;
                            DefaultListModel<String> contactListModel = view.contactListModel;
                            if (contactListModel.isEmpty()) {
                                for (ListPacket.CorrespondentItem item : items) {
                                    if (!currentUserLogin.equals(item.login)) {
                                        contactListModel.add(contactListModel.size(), item.login);
                                    } else if (currentUserLogin.equals(item.login) && currentUserId == 0) {
                                        currentUserId = item.id;
                                    }
                                }
                            } else {
                                List<Integer> indicesToRemove = new ArrayList<>();
                                for (int i = 0; i < contactListModel.getSize(); i++) {
                                    String item = contactListModel.getElementAt(i);
                                    if (items.stream()
                                            .noneMatch(obj -> obj.login.equals(item))) {
                                        indicesToRemove.add(i);
                                    }
                                }
                                for (int i = indicesToRemove.size() - 1; i >= 0; i--) {
                                    contactListModel.remove(indicesToRemove.get(i));
                                }
                            }
                        }
                    }}

                @Override
                protected void done() {
                    client.sendMessage(new ByePacket());
                }
            };
            worker.execute();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void updateCorrespondentList(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            client.sendMessage(new ListPacket());
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void show() {
        view.setVisible(true);
    }
}
