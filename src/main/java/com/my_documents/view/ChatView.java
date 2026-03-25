package com.my_documents.view;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ChatView extends JFrame {

    private JPanel messagesPanel;
    private JTextField messageField;
    private JButton sendButton;
    public DefaultListModel<String> contactListModel;
    public JList<String> contactList;

    public ChatView(String currentUserLogin) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("Button.arc", 20);
            UIManager.put("Component.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Private Chat V1.   @" + currentUserLogin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        //Списое контактов
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        contactList.setFixedCellWidth(150);
        contactList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane contactScrollPane = new JScrollPane(contactList);

        //Переписка
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        messageField = new JTextField();
        messageField.putClientProperty("JTextField.placeholderText", "Введите текст...");
        sendButton = new JButton("Отправить");

        //Ввод текта
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(contactScrollPane, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public String getMessage() {
        return messageField.getText();
    }

    public void clearMessageField() {
        messageField.setText("");
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public void appendMessage(String sender, String message, boolean isPersonalMessage) {
        SwingUtilities.invokeLater(() -> {

            JPanel wrapper = new JPanel();
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

            RoundedPanel bubblePanel = new RoundedPanel(15);
            bubblePanel.setBackground(isPersonalMessage ? new Color(0x08364d) : new Color(0x05202e));
            bubblePanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            bubblePanel.setLayout(new BorderLayout());

            bubblePanel.setOpaque(true);

            JLabel messageLabel = new JLabel(sender + ": " + message);
            messageLabel.setForeground(Color.WHITE);
            messageLabel.setFont(messageLabel.getFont().deriveFont(13f));
            messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

            messageLabel.setIconTextGap(0);
            bubblePanel.add(messageLabel, BorderLayout.CENTER);

            bubblePanel.setMaximumSize(new Dimension(250, 60));

            if (isPersonalMessage) {
                wrapper.add(Box.createHorizontalGlue());
                wrapper.add(bubblePanel);
            } else {
                wrapper.add(bubblePanel);
                wrapper.add(Box.createHorizontalGlue());
            }

            messagesPanel.add(wrapper);
            messagesPanel.add(Box.createVerticalStrut(8));

            messagesPanel.revalidate();
            messagesPanel.repaint();
        });
    }

    public void clearChatArea() {
        SwingUtilities.invokeLater(() -> {
            Component[] components = messagesPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    messagesPanel.remove(component);
                }
            }
            messagesPanel.revalidate();
            messagesPanel.repaint();
        });
    }

    public void showPopup(Component parent, String errorText) {
        InfoPopup popup = new InfoPopup(parent, errorText);
        popup.setVisible(true);
    }

    private class InfoPopup extends JDialog {
        public InfoPopup (Component parent, String errorText) {
            setTitle("Ошибка");
            setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            setSize(300, 200);
            setLocationRelativeTo(parent);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel label = new JLabel(errorText);
            panel.add(label, BorderLayout.CENTER);

            JButton closeBtn = new JButton("Закрыть");
            closeBtn.addActionListener(e -> dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeBtn);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            add(panel);
        }
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false); // Важно: делаем панель прозрачной
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape clip = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                    cornerRadius, cornerRadius);
            g2.setClip(clip);
            if (isOpaque()) {
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
        }
    }
}
