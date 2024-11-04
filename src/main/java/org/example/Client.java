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

    final static DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Client(String serverAddress, int port, String username) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.username = username;
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, port);
        try
        {
            inputStream  = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        new listenFromServer().start();
    }

    public int connectNew() throws IOException, ClassNotFoundException {
        socket = new Socket(serverAddress, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());

        outputStream.writeObject("NEW_CHAT");

        int newPort = (Integer) inputStream.readObject();
        disconnect();

        socket = new Socket(serverAddress, newPort);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        new listenFromServer().start();

        return newPort;
    }

    private void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        try {
            if(inputStream != null) inputStream.close();
        }
        catch(Exception e) {}
        try {
            if(outputStream != null) outputStream.close();
        }
        catch(Exception e) {}
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 5060;
        String username = "Anonymous";

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to chat Messenger! Please enter your username:");
        username = scanner.nextLine();

        System.out.println("Do you want to (1) start a new chat or (2) join an existing chat? Please enter 1 or 2.");
        String option = scanner.nextLine();

        try {
            Client client = new Client(serverAddress, port, username);
            if (option.equals("1")) {
                port = client.connectNew();
                System.out.println("New chat created on port " + port + ". You are now connected to the chat.");
            } else if (option.equals("2")) {
                System.out.println("Enter the port number of the chat:");
                port = Integer.parseInt(scanner.nextLine());
                client = new Client(serverAddress, port, username);
                client.connect();
            } else {
                System.out.println("Invalid option, please try agian.");
            }
            //try again

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
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        System.out.println("[" + message.getTimestamp().format(formatter) + "] " + message.getUsername() + ": " + message.getText());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Connection closed.");
                }
            }
    }

}

