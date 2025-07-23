import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        Socket socket = null;                   //	Objects to listen or connect for chat
        BufferedReader inputFromServer = null;      //	To read text input from client/server or user
        PrintWriter outputToServer = null;          //To send text to the other side
        BufferedReader userInput = null;

        try {
            // 1. Connect to server at localhost:1234
            socket = new Socket("localhost", 1234);
            System.out.println("✅ Connected to server!");

            // 2. Setup I/O streams
            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputToServer = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            String messageToServer;
            String messageFromServer;

            // 3. Start chat loop
            while (true) {
                // Send message to server
                System.out.print("🧑‍💻 You: ");
                messageToServer = userInput.readLine();
                outputToServer.println(messageToServer);

                if (messageToServer.equalsIgnoreCase("exit")) {
                    System.out.println("❌ You ended the chat.");
                    break;
                }

                // Receive reply from server
                messageFromServer = inputFromServer.readLine();
                if (messageFromServer == null || messageFromServer.equalsIgnoreCase("exit")) {
                    System.out.println("❌ Server ended the chat.");
                    break;
                }

                System.out.println("👨‍💻 Server: " + messageFromServer);
            }

        } catch (IOException e) {
            System.out.println("⚠ Error: " + e.getMessage());
        } finally {
            try {
                // 4. Close all resources safely
                if (inputFromServer != null) inputFromServer.close();
                if (outputToServer != null) outputToServer.close();
                if (userInput != null) userInput.close();
                if (socket != null) socket.close();
                System.out.println("✅ Client resources closed successfully.");
            } catch (IOException e) {
                System.out.println("⚠ Error while closing resources: " + e.getMessage());
            }
        }
    }
}