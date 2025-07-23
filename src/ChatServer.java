import java.io.*;
import java.net.*;

public class ChatServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        BufferedReader inputFromClient = null;
        PrintWriter outputToClient = null;
        BufferedReader userInput = null;

        try {
            // 1. Start server on port 1234
            serverSocket = new ServerSocket(1234);
            System.out.println("👂 Server is waiting for a client to connect...");

            // 2. Accept the client connection
            socket = serverSocket.accept();         //server waits & blocks here until client connects.
            System.out.println("✅ Client connected!");

            // 3. Setup I/O streams
            inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));       //to read what client says
            outputToClient = new PrintWriter(socket.getOutputStream(), true);               //to send your reply to client
            userInput = new BufferedReader(new InputStreamReader(System.in));           //to take your input from keyboard(server side)

            String messageFromClient;
            String messageToClient;

            // 4. Start chat loop
            while (true) {                          //keep running until exit
                // Receive message from client
                messageFromClient = inputFromClient.readLine();

                //safety check: If client says "exit" or disconnects,stop loop
                if (messageFromClient == null || messageFromClient.equalsIgnoreCase("exit")) {
                    System.out.println("❌ Client ended the chat.");
                    break;
                }

                System.out.println("👤 Client: " + messageFromClient);       //Display received msg

                // Send reply back to client
                System.out.print("🧑‍💻 You: ");
                messageToClient = userInput.readLine();
                outputToClient.println(messageToClient);

                //If you say "exit", it ends the chat
                if (messageToClient.equalsIgnoreCase("exit")) {
                    System.out.println("❌ You ended the chat.");
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("⚠ Error occurred: " + e.getMessage());

        } finally {         //finally block is used after try & catch, it contains code that must be executed even if error happens above
            try {
                // 5. Close all resources safely:Ensures port 1234 is free for next time
                if (inputFromClient != null) inputFromClient.close();
                if (outputToClient != null) outputToClient.close();
                if (userInput != null) userInput.close();
                if (socket != null) socket.close();
                if (serverSocket != null) serverSocket.close();
                System.out.println("✅ Server resources closed successfully.");
            } catch (IOException e) {
                System.out.println("⚠ Error while closing resources: " + e.getMessage());
            }
        }
    }
}