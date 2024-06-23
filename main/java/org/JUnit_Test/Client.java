package org.JUnit_Test;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// Class definition for Client
public class Client {
    // Representing the connection between the client and the server
    private Socket socket;
    // To read input data from the Server
    private BufferedReader bufferedReader;
    // To write output to the Server
    private BufferedWriter bufferedWriter;
    //Representing the username of user using this chat which will be sent to the server when the client connects
    private String username;
    // Reads input from console
    private BufferedReader consoleReader;

    // Constructor for initialising the Client's socket, reader, writer and username
    public Client (Socket socket, String username, BufferedReader consoleReader, BufferedWriter bufferedWriter) {
        // This wraps up the code in try-catch block to handle potential IOExceptions
        try{
            // Assign the socket passed as an argument to the instance variable
            this.socket = socket;
            // To read input data from the Server, which allows the client to read messages received from the server
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // To write output to the Server which allows the client to send messages to the server
            this.bufferedWriter = bufferedWriter;
            //Assign the username passed as an argument to the instance variable
            this.username = username;
            this.consoleReader = consoleReader;
        } catch (IOException e) {
            // If there is an exception, close all resources associated with the Client class
            CloseEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to send messages to the Server
    public void sendMessages() {
        try{
            // Send username to the Server
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            // Create a scanner to read user input
            Scanner scanner = new Scanner(System.in);

            // This block of codes, continuously read and send messages to the server while the socket is connected
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            CloseEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to listen for messages from the Server
    public void ListenForMessage() {
        // Create a new thread to run in parallel with the main thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupchat;

                // These lines of code, continuously read messages from the Server and print them
                try{
                    while ((messageFromGroupchat = bufferedReader.readLine()) != null){
                        System.out.println(messageFromGroupchat);

                        // These 3 lines of codes close the connection, if the user has been removed from the Chat
                        if (messageFromGroupchat.startsWith("SERVER: You have been removed from Server by the Coordinator")) {
                            CloseEverything(socket, bufferedReader, bufferedWriter);
                            break;
                        }
                    }
                } catch (IOException e){
                    CloseEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    // Method to close all open resources (socket, reader and writer)
    public void CloseEverything (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    // This is the Main method to run the Client application
    public static void main(String[] args) throws IOException {
        // This block of codes read the username from the user
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String username = scanner.nextLine();

        // These 2 lines of code create a new socket and initialize the client with it
        Socket socket = new Socket("localhost", 8080);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Client client = new Client(socket, username, new BufferedReader(new InputStreamReader(System.in)), bufferedWriter);

        // Here the application starts listening for messages and sends message sin parallel
        client.ListenForMessage();
        client.sendMessages();
    }
}

