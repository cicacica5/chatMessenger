# Chat Messenger

This is a simple chat messenger that works in a local network using sockets. On its start the console App will ask either the user want to start a new chat or join some already existing. To join an existing chat the user needs to enter the port number of the chat, which is given to user when creating a new chat.

## How to run the app
1. Configure the server address and the port number. 

Make sure the default port number in the Client main method is the same as the MAIN_PORT in the Server class.

2. Start the server.

3. Start the client.
You could run multiple Client instances at the same time to simultae multiple users chatting.
