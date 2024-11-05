# Chat Messenger

This is a simple chat messenger that works in a local network using sockets. Any message in a chat will be broadcast to other users in the same chat, along with the metadata (timestamp and username).

## User Guide

On its start the console App will ask the user to enter their username. Then the user chooses to either start a new chat or join an existing chat. To join an existing chat the user needs to enter the port number of the chat, which is given when a new chat is created. To leave the chat, enter 'exit.' 

## Prerequisites

JDK 8 or above

Maven

## Getting Started

1. Configure the server address and the port number. 

Check if you need to change the server address and the port number. Make sure the default port number in the Client main method is the same as the MAIN_PORT in the Server class.

2. Complie the application

In the root directory, compile all Java files:

`javac -d out src/org/example/*.java`

3. Start the server.

`java -cp out org.example.Server`

The server will print the port it is listening on.

4. Start the client.

Start the client in a new terminal:

`java -cp out org.example.Client`

You could run multiple Client instances at the same time to simulate multiple users chatting.

5. Follow the instructions given by the prompt
