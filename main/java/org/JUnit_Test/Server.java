package org.JUnit_Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// Class definition for Server
public class Server {
    // Declare a ServerSocket for the server to listen for incoming connections
    private ServerSocket serverSocket;
    //Declare a boolean to track whether a connected client is the first client
    private boolean firstClient = true;
    // This line of code declares a private list of ClientHandler objects to store connected clients
    private List<ClientHandler> clientHandlers = new ArrayList<>();

    // This block of code defines a public getter method to return the list of connected ClientHandler objects
    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    // Constructor for initialising the Server's ServerSocket and set up a shutdown hook
    public Server (ServerSocket serverSocket) {
        // This line of the code sets the provided serverSocket as the Server's ServerSocket
        this.serverSocket = serverSocket;

        // These lines of code add a shutdown hook to close the Server socket when the program terminates
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // The CloseServerSocket will be called when the thread runs
            CloseServerSocket();
        }));
    }

    // Method to start the Server and accept the Client connections
    public void beginServer() {
        try{
            // These 2 lines of code continuously accept client connections while the Server socket is open
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                // Here the 2 lines of code determine if the connected Client is a Coordinator (first client)
                boolean isCoordinator = firstClient;
                firstClient = false;

                // This block of code creates a new client handler and start it in a separate thread
                ClientHandler clientHandler = new ClientHandler (socket, isCoordinator, this);

                // Add the client handler to the list of client handlers
                clientHandlers.add(clientHandler);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
            // Catch any IOException that might occur during the closing process
        }catch (IOException e){
        }finally {
            // Close the Server socket when the Server stops accepting connections
            CloseServerSocket();
        }
    }

    // This is method to close the server socket
    public void CloseServerSocket() {
        // This wraps up the code in try-catch block to handle potential IOExceptions
        try{
            // Checks if the serverSocket is not null
            if (serverSocket != null) {
                // If ServerSocket is not null, close the serverSocket
                serverSocket.close();
            }
        }catch (IOException e){
            // If an exception occurs, print the stack trace for debugging purposes
            e.printStackTrace();
        }
    }

    // This is the Main method to run the Server application
    public static void main(String[] args) throws IOException {
        // This 2 lines of code creates a new server socket and initialize the server with it
        ServerSocket serverSocket = new ServerSocket(8080);
        Server server = new Server(serverSocket);
        // This line of code starts the Server and begin accepting Client connections
        server.beginServer();
    }
}

