package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ChatServer chatServer;
    private String username;

    public ClientHandler(Socket socket, ChatServer chatServer) {
        this.socket = socket;
        this.chatServer = chatServer;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            username = (String) inputStream.readObject();
            chatServer.broadcast(new Message("Server", username + " has joined the chat."), this);
            System.out.println("A new client " + username + " has connected on port: "  + socket.getLocalPort());

            while (true) {
                Message message = (Message) inputStream.readObject();
                chatServer.broadcast(message, this);
            }

        } catch (IOException | ClassNotFoundException e) {
            chatServer.broadcast(new Message("Server", username + " has left the chat."), this);
            System.out.println("Client " + username + " has disconnected on port:"  + socket.getLocalPort());
            chatServer.removeClient(this);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
