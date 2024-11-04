package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private  ServerSocket serverSocket;
    private static final int MAIN_PORT = 5060;
    private Map<Integer, ChatServer> chats = new HashMap<>();

    public Server() throws IOException {
        serverSocket = new ServerSocket(MAIN_PORT);
        System.out.println("Server started on port: " + MAIN_PORT);
    }

    public void start() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected on port: " + MAIN_PORT);
                handleClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) throws IOException, ClassNotFoundException {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        String request = (String) inputStream.readObject();

        if ("NEW_CHAT".equals(request)) {
            int newPort = createNewChat();
            outputStream.writeObject(newPort);
        }
    }

    private int createNewChat() {
        try {
            ServerSocket chatServerSocket = new ServerSocket(0);
            int port = chatServerSocket.getLocalPort();
            ChatServer chatServer = new ChatServer(chatServerSocket);
            chats.put(port, chatServer);

            new Thread(chatServer::start).start();
            System.out.println("New chat server started on port: " + port);
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
