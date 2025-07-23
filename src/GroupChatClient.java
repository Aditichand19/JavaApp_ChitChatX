import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class GroupChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JTextArea chatArea;
    private JTextField inputField;
    private String userName;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GroupChatClient().createGUI());
    }

    private void createGUI() {
        JFrame frame = new JFrame("ChitChatX");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 550);
        frame.setLocationRelativeTo(null);

        // Gradient background
        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 25, 47), 0, getHeight(), new Color(25, 90, 130));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Header panel with logo and title
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);

        try {
            ImageIcon logoIcon = new ImageIcon(GroupChatClient.class.getResource("logo2.png"));
            Image scaledLogo = logoIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(scaledLogo));
            header.add(logo);
        } catch (Exception e) {
            System.out.println("Logo not found.");
        }

        JLabel title = new JLabel("ChitChatX");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title);

        // New Client Button
        JButton newClientBtn = new JButton("New Client");
        newClientBtn.setBackground(new Color(30, 150, 200));
        newClientBtn.setForeground(Color.WHITE);
        newClientBtn.setFocusPainted(false);
        newClientBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newClientBtn.addActionListener(e -> new Thread(() -> new GroupChatClient().createGUI()).start());
        header.add(Box.createHorizontalStrut(20));
        header.add(newClientBtn);

        mainPanel.add(header, BorderLayout.NORTH);

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setForeground(Color.WHITE);
        chatArea.setBackground(new Color(15, 35, 65));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Input and send button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setOpaque(false);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setBackground(new Color(30, 50, 80));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton sendButton = new JButton("Send");
        sendButton.setFocusPainted(false);
        sendButton.setBackground(new Color(0, 120, 215));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);

        // Ask for name
        userName = JOptionPane.showInputDialog(frame, "Enter your name:", "Username", JOptionPane.PLAIN_MESSAGE);
        if (userName == null || userName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Start connection
        startConnection();

        // Message send logic
        ActionListener sendAction = e -> {
            String msg = inputField.getText();
            if (!msg.trim().isEmpty()) {
                out.println(msg);
                inputField.setText("");
                if (msg.equalsIgnoreCase("exit")) {
                    closeConnection();
                    System.exit(0);
                }
            }
        };

        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
    }

    private void startConnection() {
        try {
            socket = new Socket("localhost", 1234);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(userName);

            Thread receiveThread = new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        chatArea.append(msg + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("\n❌ Disconnected from server.\n");
                }
            });

            receiveThread.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "⚠ Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}