package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private String username;

    private String serverAddress;

    private int port;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public Client(String serverAddress, int port, String username) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.username = username;
    }

    private void connect() {
        try
        {
            socket = new Socket(serverAddress, port);
            inputStream  = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
        new listenFromServer().start();
    }

    private int connectNew() throws IOException, ClassNotFoundException {
        socket = new Socket(serverAddress, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());

        outputStream.writeObject("NEW_CHAT");

        int newPort = (Integer) inputStream.readObject();
        disconnect();

        if (newPort < 0) {
            System.out.println("Failed to create a new chat room. Please try again later.");
        } else {
            socket = new Socket(serverAddress, newPort);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            new listenFromServer().start();
        }
        return newPort;
    }

    private void sendUsername(String username) {
        try {
            outputStream.writeObject(username);
        }
        catch(IOException e) {
            System.err.println("Error sending username to server: " + e.getMessage());
        }
    }

    private void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
        }
        catch(IOException e) {
            System.err.println("Error sending message to server: " + e.getMessage());
        }
    }

    private void disconnect() {
        try {
            if(inputStream != null) inputStream.close();
        }
        catch(IOException e) {
            System.err.println("Error exiting the chat: " + e.getMessage());
        }
        try {
            if(outputStream != null) outputStream.close();
        }
        catch(IOException e) {
            System.err.println("Error exiting the chat: " + e.getMessage());
        }
        try{
            if(socket != null) socket.close();
        }
        catch(IOException e) {
            System.err.println("Error exiting the chat: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        //default
        String serverAddress = "localhost";
        int port = 5060;
        String username = "Anonymous";

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to chatMessenger! Please enter your username:");
        username = scanner.nextLine();

        System.out.println("Do you want to (1) start a new chat or (2) join an existing chat? Please enter 1 or 2.");
        String option = scanner.nextLine();

        try {
            Client client = new Client(serverAddress, port, username);
            if (option.equals("1")) {
                int maxRetries = 5;
                int retries = 0;
                while (retries < maxRetries) {
                    port = client.connectNew();
                    if (port >= 0) {
                        System.out.println("New chat created on port " + port + ". You are now connected to the chat. Please enter 'exit' to leave the chat.");
                        break;
                    }
                    retries++;
                    System.out.println("Retrying to create a new chat (" + retries + "/" + maxRetries + ")");
                }
            } else if (option.equals("2")) {
                System.out.println("Please enter the port number of the chat:");
                port = Integer.parseInt(scanner.nextLine());
                client = new Client(serverAddress, port, username);
                client.connect();
                System.out.println("You are now connected to the chat. Please enter 'exit' to leave the chat.");
            } else {
                System.out.println("Invalid option, please try agian.");
            }
            //try again
            client.sendUsername(username);

            while (true) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("EXIT")) {
                    System.out.println("Exiting chat...");
                    break;
                }
                client.sendMessage(new Message(username, message));
            }
            scanner.close();
            client.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


        private class listenFromServer extends Thread {
            public void run() {
                try {
                    while (true) {
                        Message message = (Message) inputStream.readObject();
                        String formattedTimestamp = message.getTimestamp().format(FORMATTER);
                        if (message.getUsername().equals("Server")) {
                            System.out.println("[" + formattedTimestamp + "] [NOTICE]: " + message.getText());
                        } else {
                            System.out.println("[" + formattedTimestamp + "] " + message.getUsername() + ": " + message.getText());
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Connection closed.");
                }
            }
    }

}

