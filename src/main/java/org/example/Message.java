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

    String getText() {
        return content;
    }

    String getUsername() {
        return username;
    }
    LocalDateTime getTimestamp() {
        return timestamp;
    }
}
