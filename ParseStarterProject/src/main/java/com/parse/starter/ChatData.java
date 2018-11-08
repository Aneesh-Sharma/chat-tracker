package com.parse.starter;

public class ChatData {

    private String sender;
    private String messageText;

    public ChatData() {
        // empty constructor
    }

    public String getName() {
        return sender;
    }

    public void setName(String name) {
        sender = name;
    }

    public String getMessage() {
        return messageText;
    }

    public void setMessage(String message) {
        messageText = message;
    }
}
