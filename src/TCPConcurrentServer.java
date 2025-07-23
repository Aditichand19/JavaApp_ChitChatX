// TCPServer.java (Concurrent using Threads)
import java.io.*;
import java.net.*;

public class TCPConcurrentServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Concurrent Server is running...");

            while (true) {
                Socket socket = serverSocket.accept(); // accepts multiple clients
                System.out.println("Client connected!");

                // Start a new thread for each client
                new ClientHandler(socket).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Thread to handle each client
class ClientHandler extends Thread {
    private Socket socket;

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String msg = in.readLine();
            System.out.println("Client says: " + msg);
            out.println("Hello from Concurrent Server!");

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
