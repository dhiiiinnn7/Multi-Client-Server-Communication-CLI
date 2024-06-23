import org.JUnit_Test.Client;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientTest {

    @Test
    // This is the test for this method to check if the messages are being sent correctly within the chat application
    public void sendMessages () throws IOException {
        // Arrange
        // Set up the input for the test user's messages
        String input = "Hello\nWorld\n";
        // In the next 2 line of code, first line of code will create a BufferedReader to read the test user's messages
        // and the next line will create a mock BufferedWriter for writing messages to the server
        BufferedReader consoleReader = new BufferedReader(new StringReader(input));
        BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        // Mock the Socket object for creating a connection
        Socket socket = mock(Socket.class);
        // Mock the BufferedReader for reading messages from the Server
        BufferedReader bufferedReader = mock(BufferedReader.class);

        // This block of the code will set up the behavior of the mocked objects
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        doNothing().when(bufferedWriter).flush();

        // This line of the code will create a new client object using the mocked objects
        Client client = new Client(socket, "Test_User", consoleReader, bufferedWriter);

        // Act
        // Call the sendMessages method to test sending messages
        client.sendMessages();

        // Assert
        // This block of code will verify that the BufferedWriter methods were called as expected
        verify(bufferedWriter, times(1)).write(anyString());
        verify(bufferedWriter, times(1)).newLine();
        verify(bufferedWriter, times(1)).flush();
    }

    @Test
    public void ListenForMessage () throws IOException {
        // This block of the code will set up the message that will be sent by the server
        String ServerMessages = "SERVER: Welcome to the Server-chat\n" +
                "USER_1: Good Morning\n" +
                "USER_2: Good Morning to you too\n";
        // This line of code is to set up a BufferedReader with an empty input stream for the console input
        BufferedReader consoleReader = new BufferedReader(new StringReader(""));
        // Create a BufferedReader with the server messages
        BufferedReader bufferedReader = new BufferedReader(new StringReader(ServerMessages));
        // Create a mock BufferedWriter for writing messages to the server
        BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        // Mock the Socket object for creating a connection
        Socket socket = mock(Socket.class);

        // This block of the code sets up the behaviour of the mocked objects
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(ServerMessages.getBytes()));

        // Create a new Client object using the mocked objects
        Client client = new Client(socket, "Test_User", consoleReader, bufferedWriter);

        // Create an AtomicInteger to count the number of messages received
        AtomicInteger messageCount = new AtomicInteger(0);

        // This line of the code will call the ListenForMessages method to test receiving messages
        client.ListenForMessage();

        // Count the number of messages received
        bufferedReader.lines().forEach(line -> {
            messageCount.incrementAndGet();
        });

        // Check if the correct number of messages were received
        assertEquals(3, messageCount.get());
    }

    @Test
    void CloseEverything () throws IOException {
        // This block of code will set up the test objects
        BufferedReader consoleReader = new BufferedReader(new StringReader(""));
        BufferedWriter bufferedWriter = mock(BufferedWriter.class);
        Socket socket = mock(Socket.class);
        BufferedReader bufferedReader = mock(BufferedReader.class);

        // Here, these lines of code will set up the behaviour of the mocked objects
        when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        // Create a new Client object using the mocked objects
        Client client = new Client(socket, "Test_User", consoleReader, bufferedWriter);

        // Call the CloseEverything method to test closing resources
        client.CloseEverything(socket, bufferedReader, bufferedWriter);

        // This block of code will verify that the close methods were called on the resources
        verify(bufferedReader).close();
        verify(bufferedWriter).close();
        verify(socket).close();
    }

}// This code contains comments that explain the purpose of each line or block of code in the 'ClientTest' class.


