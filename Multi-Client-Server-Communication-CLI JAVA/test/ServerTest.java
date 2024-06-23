import org.JUnit_Test.ClientHandler;
import org.JUnit_Test.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    // Declaring instance variables for the Server and ServerSocket
    private Server server;
    private ServerSocket serverSocket;

    // This block of code will set up the server before each test by creating a ServerSocket and initializing a Server object
    @BeforeEach
    public void SetUp () throws IOException {
        serverSocket = new ServerSocket(0);
        server = new Server(serverSocket);
    }

    // This block of code will close the server socket after each test
    @AfterEach
    public void TearDown () {
        server.CloseServerSocket();
    }

    // This area of the code will test that the server socket is closed after being closed
    @Test
    public void TestServerSocketIsClosed() {
        server.CloseServerSocket();
        assertTrue(serverSocket.isClosed());
    }

    // This area of the code will test that the server can handle multiple client connections
    @Test
    public void TestMultipleClientConnections () throws IOException {
        // Starting the server thread
        Thread ServerThread = new Thread(() -> server.beginServer());
        ServerThread.start();

        // Creating 2 client sockets and verifying that they are connected
        Socket FirstClientSocket = new Socket("localhost", serverSocket.getLocalPort());
        assertTrue(FirstClientSocket.isConnected());

        Socket SecondClientSocket = new Socket("localhost", serverSocket.getLocalPort());
        assertTrue(SecondClientSocket.isConnected());

        // Closing the Client Sockets
        FirstClientSocket.close();
        SecondClientSocket.close();
    }

    // Here, the code will test that server stops accepting client connections after being closed
    @Test
    public void TestServerStopsAcceptingClientsWhenClosed () throws IOException {
        Thread ServerThread = new Thread(() -> server.beginServer());
        ServerThread.start();

        // Creating a client socket and verifying that it is connected
        Socket ClientSocket = new Socket("localhost", serverSocket.getLocalPort());

        // Closing the Client Sockets
        server.CloseServerSocket();

        // Testing that a new client can connect to the after the server socket is closed
        assertThrows(IOException.class, () -> {
            new Socket("localhost", serverSocket.getLocalPort());
        });
    }

    // This block of the code will test that a client can connect to the server
    @Test
    public void TestClientConnection () throws IOException {
        Thread ServerThread = new Thread(() -> server.beginServer());
        ServerThread.start();

        // Creating a client socket and verifying that it is connected
        Socket ClientSocket = new Socket("localhost", serverSocket.getLocalPort());
        assertTrue(ClientSocket.isConnected());

        // Closing the client socket
        ClientSocket.close();
    }

    // This block of codes will test if the first client to connect becomes the coordinator
    @Test
    public void TestFirstClientBecomesCoordinator() throws IOException, InterruptedException {
        Thread ServerThread = new Thread(() -> server.beginServer());
        ServerThread.start();

        // Connect 2 Client Sockets to the Server
        Socket ClientSocket1 =new Socket("localhost", serverSocket.getLocalPort());
        Socket ClientSocket2 =new Socket("localhost", serverSocket.getLocalPort());

        Thread.sleep(500); // Giving the server some time to process the client connection

        // Get the list of ClientHandler objects from the Server
        List<ClientHandler> clientHandlers = server.getClientHandlers();

        // Assert that 2 clients are connected
        assertEquals(2, clientHandlers.size(), "Expected two clients to be connected");

        // Assert that the 1st client is the Coordinator
        assertTrue(clientHandlers.get(0).isCoordinator(), "Expected the 1st client to be the Coordinator");
        // Assert that the 2nd client is the Coordinator
        assertFalse(clientHandlers.get(1).isCoordinator(), "Expected the 2nd client to not be the Coordinator");


        // Closing the client socket
        ClientSocket1.close();
        ClientSocket2.close();
    }
}