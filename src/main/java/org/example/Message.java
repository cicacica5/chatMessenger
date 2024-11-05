package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;


public class Message implements Serializable {
    private String username;
    private String content;
    private LocalDateTime timestamp;

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public String getText() {
        return content;
    }

    public String getUsername() {
        return username;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
