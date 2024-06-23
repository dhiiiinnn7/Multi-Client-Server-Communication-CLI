package org.JUnit_Test;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

// Class definition for ClientHandler that implements the Runnable interface
public class ClientHandler implements Runnable {
    // Declare a static ArrayList of ClientHandler instances to keep track of all connected clients
    public static ArrayList<ClientHandler> handleClients = new ArrayList<>();
    // Declare a Socket instance to represent the Client's connection to the Server
    private Socket socket;
    // Declare a BufferedReader instance to read data from Client's input stream
    private BufferedReader bufferedReader;
    // Declare a BufferedWriter instance to write data to the Client's output stream
    private BufferedWriter bufferedWriter;
    // Declare a boolean instance to determine whether the client is a Coordinator
    private boolean isCoordinator;
    // A server instance representing the server to which the client is connected
    private Server server;
    // Declare a String instance to store the Client's username
    public String clientUsername;

    // Constructor for initialising the ClientHandler and configuring the necessary resources
    public ClientHandler (Socket socket, boolean isCoordinator, Server server) {
        // try-catch block to handle potential IOExceptions
        try{
            // Assign given Socket instance(representing the Client's connection) to the class's socket variable
            this.socket = socket;
            // Assign the given boolean value (representing whether the Client is a coordinator) to the class's isCoordinator variable
            this.isCoordinator = isCoordinator;
            // Assign the given Server instance to the class's server variable
            this.server = server;

            // These lines of code, get the input stream from the socket and store it in a local variable
            // and create an InputStreamerReader to read data from the input stream
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            // Create a BufferedWriter instance, wrapping an OutputStreamWriter around the socket's output stream and assign it to the class's bufferedWriter variable
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // These lines of code, get the output stream from the socket and store it in a local variable
            // then create an OutputStreamWriter to write data to the output stream
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            // Create a BufferedReader instance, wrapping an InputStreamReader around the socket's Input stream and assign it to the class's bufferedReader variable
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e){
            CloseEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void initializeClient () throws IOException {
        // The wrap up code with "do-while" block is to get a unique username for client means client will not be able to use taken username
        // Keep looping until the Client provides a unique username
        do{
            // Read a line from the Client, representing their chosen username
            this.clientUsername = bufferedReader.readLine();
            // This block of the code send a message to the Client requesting another username if the username is not unique
            if (!isUsernameUnique(this.clientUsername)) {
                bufferedWriter.write("SERVER: Username has been taken already, Please enter another username: ");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            // Keep looping until a unique username is provided
        } while (!isUsernameUnique(this.clientUsername));

        // Print welcome message and add the client to the list of connected clients
        printClientJoined();
        SendConnectedClientList();

        // If the client is a Coordinator, send a message to inform them
        if (isCoordinator) {
            bufferedWriter.write("SERVER: You are the first person to join the Server, so you are the Coordinator now for this Server");
        } else {
            // If the client is not a Coordinator, send a welcome message
            bufferedWriter.write("SERVER: Welcome " + this.clientUsername + " to the Chat!");

            // This block of code is to inform the Client about the current Coordinator
            String currentCoordinator = getCurrentCoordinator();
            if (currentCoordinator != null) {
                bufferedWriter.write(" SERVER: The current Coordinator is " + currentCoordinator);
            }
        }
        // This block of code is to add a new line and flush the BufferedWriter to send a message to the client
        bufferedWriter.newLine();
        bufferedWriter.flush();
        // Add this ClientHandler object to the list of connected Clients
        handleClients.add(this);

        // Broadcast message to all connected clients informing them about the new client
        BroadcastMessage("SERVER: " + clientUsername + " has connected to the chat");
    }

    // Method to handle client messages and commands
    @Override
    public void run () {
        // Variable to store the message received from the client
        String messageFromClient;
        // try-catch block to handle potential IOExceptions
        try{
            initializeClient();
            // Keep reading messages from the Client while the socket is connected
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();
                // This if statements checks if the 'messageFromClient' variable is null
                if (messageFromClient == null) {
                    break;
                }
                // Checks if the message is a Private Message
                if (messageFromClient.startsWith("@")) {
                    // Split the message into tokens using space as a delimiter
                    String[] tokens = messageFromClient.split(" ", 2);
                    // If there are more than 1 token, it is a private message
                    if (tokens.length > 1) {
                        String targetUsername = tokens[0].substring(1);
                        String privateMessage = tokens[1];
                        // Send the private message to the target client
                        SendPrivateMessage(targetUsername, privateMessage);
                    } else {
                        // If not a private message, broadcast it to all clients
                        BroadcastMessage(messageFromClient);
                    }
                }
                // Checks if the message is a command
                else if (messageFromClient.startsWith("/")) {
                    // Handle the command by removing the '/' prefix
                    HandleCommand(messageFromClient.substring(1));
                }
                // Otherwise, Broadcast the message to all clients
                else {
                    BroadcastMessage(messageFromClient);
                }
            }
        } catch (IOException e) {
        }finally {
            // Disconnect the ClientHandler and close all resources associated with the client
            DisconnectClientHandler();
            CloseEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to print client joined message and coordinator status
    public void printClientJoined() {
        // Print the message that the Client has connected to the Server
        System.out.println("A new client " + clientUsername + " has connected to the server");

        // If the Client is a Coordinator, print a message indicating that the Client is the Coordinator
        if (isCoordinator) {
            System.out.println(clientUsername + " is the Coordinator now");
        }
    }

    // Method to check if a username is unique among connected clients
    public static boolean isUsernameUnique (String username) {
        // Iterate through the list of connected clients
        for (ClientHandler clientHandler : handleClients) {
            // If the current Client's username matches teh provided username (case-insensitive) and return false
            if (clientHandler.clientUsername != null && clientHandler.clientUsername.equalsIgnoreCase(username)) {
                return false;
            }
        }
        // If no matching username is found, return true means the username is unique
        return true;
    }

    // Method to broadcast message to all connected clients
    public void BroadcastMessage (String messageToSend) {
        // Create a DateTimeFormatter with the specified pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        // Get the current date and time and format it using the formatter
        String timeStamp = LocalDateTime.now().format(formatter);
        // Add the timestamp and the client's username to the message
        String MessageWithTimestamp = "[" +timeStamp + "]" +clientUsername + ": " + messageToSend;
        // If the message does not start with "SERVER: ", print the broadcasting message
        if (!messageToSend.startsWith("SERVER: ")) {
            System.out.println("Broadcasting message: " + MessageWithTimestamp);
        }
        // Iternate through the list of connected Clients
        for (ClientHandler clientHandler : handleClients) {
            try{
                // If the client's username is not equal to the current client's username
                if (clientHandler.clientUsername != null && !clientHandler.clientUsername.equals(clientUsername)) {
                    // If the message starts with "SERVER: ", write the message with timestamp
                    if (messageToSend.startsWith("SERVER: ")) {
                        clientHandler.bufferedWriter.write("[" + timeStamp+ "]" + messageToSend);
                    } else {
                        // Otherwise, write teh message with timestamp and Client's username
                        clientHandler.bufferedWriter.write("[" + timeStamp + "]" + clientUsername + ": " + messageToSend);
                    }
                    // Add a new line after the message and flush the BufferedWriter to send the message
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                CloseEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    // Method to send a private message to a specified client
    public void SendPrivateMessage(String targetClientUsername,String messageToSend) {
        try{
            // Initialize a targetClient variable as null
            ClientHandler targetClient = null;
            // Iterate through the list of connected Clients
            for (ClientHandler clientHandler : handleClients) {
                // If the Client's username matches the targetClientUsername
                if (clientHandler.clientUsername.equals(targetClientUsername)) {
                    // Set targetClient to the found Client and exit the loop
                    targetClient = clientHandler;
                    break;
                }
            }
            // If the targetClient is not null (exmaple - found in the list of connected Clients)
            if (targetClient != null) {
                // Create a DateTimeFormatter with the specified pattern and get the current date and time and format it using the formatter
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String timeStamp = LocalDateTime.now().format(formatter);

                // Create the private message with the timestamp, sender and message
                String privateMessage = "[" + timeStamp + "] Private message from " + clientUsername + ": " +messageToSend;
                // This bock of the code is to write the private message to the target Client's BufferedWriter
                // then add a newline after the message and flush the BufferedWriter to send the message
                targetClient.bufferedWriter.write(privateMessage);
                targetClient.bufferedWriter.newLine();
                targetClient.bufferedWriter.flush();

                // Create a confirmation message for the sender
                privateMessage = "[" + timeStamp + "] Private message sent to " + targetClientUsername + ": " + messageToSend;
                // This bock of the code is to write the confirmation message to the sender's BufferedWriter
                // then add a newline after the message and flush the BufferedWriter to send the message
                bufferedWriter.write(privateMessage);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } else {
                // This bock of the code is to inform the sender if the target Client is not found
                // then add a newline after the message and flush the BufferedWriter to send the message
                bufferedWriter.write("Client " + targetClientUsername + " has not found in this server");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            CloseEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to pick a new Coordinator if the current Coordinator leaves
    public void PickNewCoordinator() {
        // Check if there are any connected Clients
        if (!handleClients.isEmpty()) {
            // Generate a random index within the range of the handleClients list size
            int newCoordinatorIndex = new Random().nextInt(handleClients.size());
            // Get the client at the randomly generated index
            ClientHandler newCoordinator = handleClients.get(newCoordinatorIndex);
            // Set the isCoordinator flag for the new Coordinator to true
            newCoordinator.isCoordinator = true;
            // Print a message to the Server console indicating the new Coordinator
            System.out.println(newCoordinator.clientUsername + " is the new Coordinator now");
            // Send a message to the connected Clients announcing the new Coordinator
            SendNewCoordinatorMessage(newCoordinator.clientUsername);
        }
    }

    // Returns true if the current client is a Coordinator, false otherwise
    public boolean isCoordinator (){
        return isCoordinator;
    }

    // Declare a private method which returns a String representing the username of the current Coordinator
    private String getCurrentCoordinator () {
        // Starts a for-each loop that iterates through each 'ClientHandler' object in the 'handleClients' ArrayList
        for (ClientHandler client : handleClients) {
            // Checks if the current client has the coordinator role set to true
            if (client.isCoordinator) {
                // If the current client is the Coordinator, return the username of the coordinator
                return client.clientUsername;
            }
        }
        // If the method iterates through all the clients and does not find a coordinator, return null
        return null;
    }

    // Method to send a list of connected Clients to the newly joined client
    private void SendConnectedClientList () {
        // Check if there are any connected Clients
        if (handleClients.size() > 0) {
            // Initialize a StringBuilder to build the list of connected Clients as a string
            StringBuilder clientsList = new StringBuilder("SERVER: Here are all the connected members in this chat: \n");
            // Iterate through all connected Clients
            for (ClientHandler client : handleClients) {
                // Exclude the current Client from the list
                if(client.clientUsername != null && !client.clientUsername.equals(this.clientUsername)) {
                    // Append the Client's username to the clientsList
                    clientsList.append(client.clientUsername).append("\n");
                }
            }
            // Send the list of connected Clients to the current Client
            SendMessage(clientsList.toString());
        }
    }

    // Method to list all clients along with their IP addresses and ports
    private void ListClients() {
        // Initialize a StringBuilder to build the list of connected Clients, their IP addresses and Ports as a string
        StringBuilder clientsList = new StringBuilder("SERVER: List of all the members with their ID, IP Address and Port:\n");
        // Iterate through all connected Clients
        for (ClientHandler client : handleClients) {
            // Checks if the Client is a Coordinator and set teh CoordinatorStatus string accordingly
            String coordinatorStatus = client.isCoordinator ? " (Coordinator)" : "";
            // Append the Client's usernmae, IP address, Port and Coordinator status to the clientsList
            clientsList.append(client.clientUsername)
                    .append(" - IP: ")
                    .append(client.socket.getInetAddress().getHostAddress())
                    .append(" - Port: ")
                    .append(client.socket.getPort())
                    .append(client.socket.getPort())
                    .append(coordinatorStatus)
                    .append("\n");
        }
        // Send the list of connected clients. their IP addresses and Ports to the current Client
        SendMessage(clientsList.toString());
    }

    // Method to send a message announcing the new Coordinator
    private void SendNewCoordinatorMessage (String newCoordinatorUsername) {
        // Initialize to message to inform Clients that a new Coordinator has been chosen
        String message = "SERVER: " + newCoordinatorUsername + " is the new Coordinator now";
        // Iterate through all connected Clients
        for (ClientHandler client : handleClients) {
            try{
                // If the current Client is not the new Coordinator, send them the new Coordinator message
                if (!client.clientUsername.equals(newCoordinatorUsername)) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                } else {
                    // If teh current Client is thew new Coordinator, inform that they are the new Coordinator
                    client.bufferedWriter.write("SERVER: You are the new Coordinator of this server");
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
                // If the current Client is the one who initiated the change, print the message on their console
                if (client.equals(this)) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                CloseEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    // Method to handle various commands sent by Clients
    private void HandleCommand (String command) {
        // Check if the command start with "remove"
        if (command.startsWith("remove")) {
            // Verify if the current client is the Coordinator
            if (!isCoordinator) {
                // If not teh Coordinator, send an unauthorized message
                SendMessage("SERVER: You are not authorised to perform this action");
                return;
            }
            // Split the command into tokens
            String[] tokens = command.split(" ");
            // Check is the correct number of token is present
            if (tokens.length < 2) {
                // If not, send an invalid command format message
                SendMessage("SERVER: Invalid command format. Usage: /remove <username>");
                return;
            }
            // Extract the target username
            String UsernameToRemove = tokens [1];
            // Get the target client object
            ClientHandler clientToRemove = GetClientByUsername(UsernameToRemove);
            // Checks if the target client is found
            if (clientToRemove == null) {
                // If not, send a username not found message
                SendMessage("SERVER: Username not found. Try again with correct username");
                return;
            }
            //Remove the target Client
            RemoveClientCommand(clientToRemove);
        } else if (command.startsWith("ListClients")) {
            // If the command is "ListClients", list all the connected clients
            ListClients();

        }
    }

    // Method to get a Client by username
    private ClientHandler GetClientByUsername (String username) {
        // Iterate through the list of connected Clients
        for (ClientHandler client : handleClients) {
            // Check if the Client's username matches the given username
            if (client.clientUsername.equals(username)) {
                // Return the mathing Client
                return client;
            }
        }
        // Return null if no matching Client is found
        return null;
    }

    // Method ot remove a Client from tha Server by the Coordinator
    public void RemoveClientCommand(ClientHandler client) {
        // Remove the specified Client from the list of connected Clients
        handleClients.remove(client);
        // Creates a message to notify all clients that the client has been removed by the Coordinator
        String message = "SERVER: " + client.clientUsername + " has been removed from the server by " + this.clientUsername + " (Coordinator)";
        // Broadcast the message to all connected clients
        BroadcastMessage(message);
        // Send a message to the removed Client informing them that they were removed by the Coordinator
        client.SendMessage("SERVER: You have been removed by " + this.clientUsername + " (Coordinator)");
        // Close the removed Client's connection
        client.CloseClientConnection();
    }

    // Method to close the Client's connection
    public void CloseClientConnection() {
        // Try to close the client's socket connection
        try{
            // Check if the socket is not null
            if (socket != null) {
                // Close the socket
                socket.close();
            }
        } catch (IOException e) {
            // Print any exception that occurs during the close operation
            e.printStackTrace();
        }
    }

    // Method to send a message tot the current client
    public void SendMessage (String message) {
        // Try to send a message to the Client
        try{
            // This block of the code write the message to BufferedWriter, add a new line to separate messages
            // and flush the BufferedWriter to ensure the message is sent
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            CloseEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to disconnect the current Client handler and announce the disconnection to other clients
    public void DisconnectClientHandler () {
        // Check if the socket is still connected
        if(socket.isConnected()){
            System.out.println(clientUsername + " has disconnected from the Server");
            // Broadcast message to all clients that this Client has disconnected
            BroadcastMessage("SERVER: " + clientUsername + " has disconnected from Server");
            // Remove this Client from the list of connected Clients
            handleClients.remove(this);

            // Check if the disconnected Clients is the Coordinator
            if (isCoordinator) {
                // Print that the Client is the Coordinator
                System.out.println(clientUsername + " is no longer the Coordinator");
                // Assign a new Coordinator among the connected Clients
                PickNewCoordinator();
            }
            // Close all resources related to this client
            CloseEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to close all open resources (socket, reader and writer)
    public void CloseEverything (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        // This 'try-catch' block of code checks if the BufferedReader, BufferedWriter and Socket are not null, then close it
        try{
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // If there is an exception while closing the resources, print the stack trace
            e.printStackTrace();
        }
    }

}
