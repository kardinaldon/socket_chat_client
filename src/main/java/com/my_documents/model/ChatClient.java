package com.my_documents.model;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private static Socket socket;
    private static ObjectOutputStream oos;
    public static ObjectInputStream ois;
    public static int currentUserId;

    static{
        try {
            socket = new Socket("localhost", 10001);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            currentUserId = 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(Packet packet) {
        try {
            oos.writeObject(packet);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
