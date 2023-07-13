package com.twb.pokergame.data.message.server;

public class ServerMessage {
    private String type;
    private Object content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ServerMessage{" +
                "type='" + type + '\'' +
                ", content=" + content +
                '}';
    }
}
