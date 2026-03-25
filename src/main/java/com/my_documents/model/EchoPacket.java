package com.my_documents.model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Serializable;

public class EchoPacket extends Packet implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String type = "ECHO";
    
    public String text;

    public String getType() {
        return type;
    }

    public void writeBody(PrintWriter writer) throws Exception {
        writer.println(text);
        writer.println();
    }

    public void readBody(BufferedReader reader) throws Exception {
        text = readText(reader);
    }
}