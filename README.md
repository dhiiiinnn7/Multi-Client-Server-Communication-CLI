# Multi-Client-Server-Communication-CLI

## Overview

This Java project is a basic chat application that allows clients connected to a server to communicate in groups. The client-server architecture is used to build the program, with the server managing incoming client connections and coordinating message exchange among connected clients. The main classes are `Client`, `Server`, and `ClientHandler`.

## Features

- **Group and Private Messaging**: Clients can engage in group chats or send private messages.
- **User Administration**: The first connected client acts as a coordinator with the ability to remove users.
- **Multithreading**: Efficient handling of multiple client connections using Java multithreading.

## Installation

### Clone the Repository
```sh
git clone https://github.com/dhiiiinnn7/Multi-Client-Server-Communication-CLI
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

