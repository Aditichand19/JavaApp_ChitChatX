// ✅ GroupChatServer.java (Message Logging + File History)
import java.io.*;
import java.net.*;
import java.util.*;

public class GroupChatServer {
    private static final int PORT = 1234;
    private static final String LOG_FILE = "chatlog2.txt";   //chats stored in"chatlog2.txt"
    private static final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("📡 Server started on port " + PORT + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ New client connected: " + clientSocket);

                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            }

        } catch (BindException e) {
            System.out.println("❌ Port " + PORT + " is already in use. Please stop other server instances.");
        } catch (IOException e) {
            System.out.println("⚠ Server error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter your name:");
                clientName = in.readLine();
                broadcast("🔔 " + clientName + " joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                    broadcast(clientName + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("❌ Client error: " + e.getMessage());
            } finally {
                try {
                    clients.remove(this);
                    broadcast("🚪 " + clientName + " left the chat.");
                    if (out != null) out.close();
                    if (in != null) in.close();
                    if (socket != null && !socket.isClosed()) socket.close();
                    System.out.println("❎ Client disconnected: " + clientName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            //Log message on server terminal
            System.out.println("[LOG] " + message);

            // 📁 Save message history to chatlog2.txt
            try (FileWriter fw = new FileWriter(LOG_FILE, true);        //chats stored in"chatlog2.txt", true means append(add) mode—new msgs go at the end.
                 BufferedWriter bw = new BufferedWriter(fw);            //Adds buffer btw code & file
                 PrintWriter writer = new PrintWriter(bw)) {            //Gives access to easy methods like println() [to write msg]
                writer.println(message);
            } catch (IOException e) {
                System.out.println("⚠ Error writing to file: " + e.getMessage());
            }

            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client.out != null) {
                        client.out.println(message);
                    }
                }
            }
        }
    }
}