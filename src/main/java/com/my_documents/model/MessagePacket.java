package com.my_documents.model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Serializable;

public class MessagePacket extends Packet implements Serializable {
    private static final long serialVersionUID = 1L;
    public static String type = "MSG";
    
    public int senderId;
    public int recipientId;
    public String text;

    public String getType() {
        return type;
    }

    public MessagePacket() {
    }

    public MessagePacket(int senderId, int recipientId, String text) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.text = text;
    }

    public void writeBody(PrintWriter writer) throws Exception {
        writer.println(senderId);
        writer.println(recipientId);
        writer.println(text);
        writer.println();
    }

    public void readBody(BufferedReader reader) throws Exception {
        var correspondentIdText = reader.readLine();
        senderId = Integer.parseInt(correspondentIdText);
        
        text = readText(reader);
    }
}