import org.JUnit_Test.ClientHandler;

import org.JUnit_Test.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class ClientHandlerTest {
    // Declare necessary variable for the test
    private ServerSocket serverSocket;
    private Server server;
    private Socket ClientSocket;

    // This block of code is the set up method to initialize variables before each test
    @BeforeEach
    public void SetUp () throws IOException {
        serverSocket = new ServerSocket(0);
        server = new Server(serverSocket);
        ClientSocket = new Socket("localhost", serverSocket.getLocalPort());
    }

    // This code is the tear down method to clean up resources after each test
    @AfterEach
    public void TearDown () {
        server.CloseServerSocket();
    }

    // Test method for testing IsUsernameUnique() method
    @Test
    public void TestIsUsernameUnique() throws IOException, InterruptedException {
        // Starting the server thread
        Thread ServerThread = new Thread(() -> server.beginServer());
        ServerThread.start();

        // This block of the code sends a test username to the server
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ClientSocket.getOutputStream()));
        writer.write("TestUsername-Dhin");
        writer.newLine();
        writer.flush();

        Thread.sleep(1000);

        // This block of code to print the number of clients and their usernames
        System.out.println("handleClients size: " + ClientHandler.handleClients.size());
        for (ClientHandler ch : ClientHandler.handleClients) {
            System.out.println("Client Username: " + ch.clientUsername);
        }

        // This code creates a new ClientHandler with a different username
        ClientHandler clientHandler1 = new ClientHandler(ClientSocket, false, server);
        clientHandler1.clientUsername = "AlternativeUsername";

        // Test if the method correctly identifies a unique username
        assertTrue(ClientHandler.isUsernameUnique("OtherUsername"));

        // This code line is to add the new ClientHandler to the list of clients
        ClientHandler.handleClients.add(clientHandler1);

        // Print the updated list of the clients and their usernames
        System.out.println("handleClients size after adding clientHandler1: " + ClientHandler.handleClients.size());
        for (ClientHandler ch : ClientHandler.handleClients) {
            System.out.println("Client username: " + ch.clientUsername);
        }

        //Test if the method correctly identifies a non-unique username
        assertFalse(ClientHandler.isUsernameUnique(clientHandler1.clientUsername));
    }
}