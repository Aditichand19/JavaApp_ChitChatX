// TCPServer.java
import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
        try {
            // 1. Create server socket on port 8080
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server is waiting...");

            // 2. Accept client connection
            Socket socket = serverSocket.accept();
            System.out.println("Client connected!");

            // 3. Input/output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 4. Read message from client and reply
            String msg = in.readLine();
            System.out.println("Client says: " + msg);
            out.println("Hello from Server!");

            // 5. Close
            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
