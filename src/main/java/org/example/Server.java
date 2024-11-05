package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private static final int MAIN_PORT = 5060;
    private Map<Integer, ChatServer> chats = new ConcurrentHashMap<>();

    public Server() throws IOException {
        serverSocket = new ServerSocket(MAIN_PORT);
        System.out.println("Server started on port: " + MAIN_PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    public void start() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                //System.out.println("A new client has connected on port: " + MAIN_PORT);
                handleClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        System.out.println("Shutting down server...");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        for (ChatServer chatServer : chats.values()) {
            chatServer.close();
        }
        System.out.println("Server is shutdown.");
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
            if (!chats.containsKey(port)) {
                chats.put(port, chatServer);
            } else return -1;

            new Thread(chatServer::start).start();
            System.out.println("New chat server started on port: " + port);
            return port;
        } catch (IOException e) {
            System.err.println("Error creating chat server: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }


    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
