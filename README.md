# Multi-Client-Server-Communication-CLI

Overview

This Java project is a basic chat application that allows clients connected to a server to communicate in groups. The client-server architecture is used to build the program, with the server managing incoming client connections and coordinating message exchange among connected clients. The main classes are Client, Server, and ClientHandler.

Features

Group and Private Messaging: Clients can engage in group chats or send private messages.

User Administration: The first connected client acts as a coordinator with the ability to remove users.

Multithreading: Efficient handling of multiple client connections using Java multithreading.

Installation

Clone the repository:

Copy code - git clone https://github.com/dhiiiinnn7/Multi-Client-Server-Communication-CLI

Navigate to the project directory:

Copy code - cd Multi-Client-Server-Communication-CLI JAVA

Compile the Java files:

Copy code - javac *.java

Usage

Start the server:

Copy code -  java Server

Start the client:

Copy code - java Client

Follow the prompts to enter your username and start chatting.

Design and Implementation
The chat application is built using the client-server architecture with three primary classes:

Server: Manages client connections and coordinates message exchange.

Client: Represents a user and handles user interface and communication with the server.

ClientHandler: Manages communication between the server and each individual client.

Server

Initializes a ServerSocket to listen for incoming connections.

For each connection, creates a ClientHandler instance and a new thread for concurrent handling.

The first connected client is assigned the coordinator role.

Client

Connects to the server using a Socket.

Uses BufferedReader and BufferedWriter for reading from and writing to the server.

Supports sending and receiving messages, both in the main thread and a separate thread for listening to incoming messages.

ClientHandler

Each client connection is managed by a ClientHandler instance running in its own thread.

Handles broadcasting messages, sending private messages, managing the coordinator role, and user commands such as removing clients.

Testing

JUnit tests are used to validate the key functionalities:

1. Unique Username Enforcement.
2. Server Socket Closure.
3. Handling Multiple Client Connections.
4. Server Behavior on Closure.
5. Client-Server Connection.
6. Coordinator Role Assignment.
7. Message Sending.
8. Message Listening.
9. Resource Management.

Running Tests

Compile and run the test classes using JUnit to ensure all functionalities work as expected.

Fault Tolerance

The application handles various fault scenarios such as:

Client disconnections: Removes the client from the list and reassigns the coordinator if necessary.

Error handling: Closes resources and continues running on encountering errors.

Ensures unique usernames to prevent user confusion.

Limitations

The disconnection process for removed users is not immediate; users must send messages twice to be fully disconnected.

Improvements needed in the user removal process to ensure seamless disconnection.

License

This project is licensed under the MIT License - see the LICENSE file for details.

