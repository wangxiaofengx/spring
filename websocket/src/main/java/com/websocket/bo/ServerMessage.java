package com.websocket.bo;

public class ServerMessage {

    private String content;

    public ServerMessage() {
    }

    public ServerMessage(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}