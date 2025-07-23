// TCPClient.java
import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) {
        try {
            // 1. Connect to server on localhost, port 8080
            Socket socket = new Socket("localhost", 8080);

            // 2. Input/output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 3. Send message to server and get reply
            out.println("Hi Server!");
            String reply = in.readLine();
            System.out.println("Server says: " + reply);

            // 4. Close
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
