// TCPServer.java (Iterative)
import java.io.*;
import java.net.*;

public class TCPIterativeServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Iterative Server is running...");

            while (true) {
                Socket socket = serverSocket.accept(); // waits for a client
                System.out.println("Client connected!");

                // Handle one client at a time
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                String msg = in.readLine();
                System.out.println("Client says: " + msg);
                out.println("Hello from Iterative Server!");

                socket.close(); // close this client before next
                System.out.println("Client disconnected. Waiting for next...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
