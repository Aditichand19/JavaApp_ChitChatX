// ✅ UPDATED GroupChatServer.java with Encryption/Decryption
import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class GroupChatServerEncrypt {
    private static final int PORT = 1234;
    private static final String LOG_FILE = "chatlog.txt";
    private static final String SECRET_KEY = "1234567890123456"; // 16-char AES key
    private static final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("\uD83D\uDCE1 Server started on port " + PORT + ". Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\u2705 New client connected: " + clientSocket);

                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            System.out.println("\u26A0 Server error: " + e.getMessage());
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

                out.println(encrypt("Enter your name:"));
                clientName = decrypt(in.readLine());
                broadcast("\uD83D\uDD14 " + clientName + " joined the chat.");

                String encryptedMsg;
                while ((encryptedMsg = in.readLine()) != null) {
                    String message = decrypt(encryptedMsg);
                    if (message.equalsIgnoreCase("exit")) break;
                    broadcast(clientName + ": " + message);
                }

            } catch (IOException e) {
                System.out.println("\u274C Client error: " + e.getMessage());
            } finally {
                try {
                    clients.remove(this);
                    broadcast("\uD83D\uDEAA " + clientName + " left the chat.");
                    if (out != null) out.close();
                    if (in != null) in.close();
                    if (socket != null && !socket.isClosed()) socket.close();
                    System.out.println("\u274E Client disconnected: " + clientName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            System.out.println("[LOG] " + message);

            try (FileWriter fw = new FileWriter(LOG_FILE, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter writer = new PrintWriter(bw)) {
                writer.println(message);
            } catch (IOException e) {
                System.out.println("\u26A0 Error writing to file: " + e.getMessage());
            }

            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client.out != null) {
                        client.out.println(encrypt(message));
                    }
                }
            }
        }
    }

    // AES encryption
    public static String encrypt(String strToEncrypt) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return strToEncrypt;
        }
    }

    public static String decrypt(String strToDecrypt) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(strToDecrypt);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            return strToDecrypt;
        }
    }
}
