# Multi-Client-Server-Communication-CLI

## Overview

This Java project is a basic chat application that allows clients connected to a server to communicate in groups. The client-server architecture is used to build the program, with the server managing incoming client connections and coordinating message exchange among connected clients. The main classes are `Client`, `Server`, and `ClientHandler`.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
  - [Clone the Repository](#clone-the-repository)
  - [Navigate to the Project Directory](#navigate-to-the-project-directory)
  - [Compile the Java Files](#compile-the-java-files)
- [Usage](#usage)
  - [Start the Server](#start-the-server)
  - [Start the Client](#start-the-client)
- [Design and Implementation](#design-and-implementation)
  - [Server](#server)
  - [Client](#client)
  - [ClientHandler](#clienthandler)
- [Testing](#testing)
  - [Running Tests](#running-tests)
- [Fault Tolerance](#fault-tolerance)
- [Limitations](#limitations)
- [License](#license)


## Features

- **Group and Private Messaging**: Clients can engage in group chats or send private messages.
- **User Administration**: The first connected client acts as a coordinator with the ability to remove users.
- **Multithreading**: Efficient handling of multiple client connections using Java multithreading.

## Installation

### Clone the Repository
```sh
git clone https://github.com/dhiiiinnn7/Multi-Client-Server-Communication-CLI.git
```

### Navigate to the Project Directory
```sh
cd Multi-Client-Server-Communication-CLI
```

### Compile the Java Files
```sh
javac *.java
```

## Usage

### Start the Server
```sh
java Server
```

### Start the Client
```sh
java Client
```

Follow the prompts to enter your username and start chatting.

## Design and Implementation

The chat application is built using the client-server architecture with three primary classes:

### Server

- Manages client connections and coordinates message exchange.
- Initializes a `ServerSocket` to listen for incoming connections.
- For each connection, creates a `ClientHandler` instance and a new thread for concurrent handling.
- The first connected client is assigned the coordinator role.

### Client

- Represents a user and handles user interface and communication with the server.
- Connects to the server using a `Socket`.
- Uses `BufferedReader` and `BufferedWriter` for reading from and writing to the server.
- Supports sending and receiving messages, both in the main thread and a separate thread for listening to incoming messages.

### ClientHandler

- Manages communication between the server and each individual client.
- Each client connection is managed by a `ClientHandler` instance running in its own thread.
- Handles broadcasting messages, sending private messages, managing the coordinator role, and user commands such as removing clients.

## Testing

JUnit tests are used to validate the key functionalities:

- Unique Username Enforcement
- Server Socket Closure
- Handling Multiple Client Connections
- Server Behavior on Closure
- Client-Server Connection
- Coordinator Role Assignment
- Message Sending
- Message Listening
- Resource Management

### Running Tests

Compile and run the test classes using JUnit to ensure all functionalities work as expected.

## Fault Tolerance

The application handles various fault scenarios such as:

- **Client Disconnections:** Removes the client from the list and reassigns the coordinator if necessary.
- **Error Handling:** Closes resources and continues running on encountering errors.
- **Ensures Unique Usernames:** To prevent user confusion.

## Limitations

- The disconnection process for removed users is not immediate; users must send messages twice to be fully disconnected.
- Improvements needed in the user removal process to ensure seamless disconnection.

## License
This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
