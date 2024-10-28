import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    private static JFrame frame;
    private static JPanel userListPanel;
    private JPanel chatPanel;
    private static Socket socket;
    private static PrintWriter out;
    private HashMap<String, JFrame> openChats = new HashMap<>();

    public Client() {
        new ClientLogin(this);
    }

    public void openChatWindow(String username) {
        setupGUI();
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            ServerConnection serverConn = new ServerConnection(socket, this);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username);
            new Thread(serverConn).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        frame = new JFrame("Chat Client");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        frame.add(new JScrollPane(userListPanel), BorderLayout.WEST);

        frame.setVisible(true);
    }

    public void updateUsersList(String[] users) {
        userListPanel.removeAll();

        for (String user : users) {
            JButton userButton = new JButton(user);
            userButton.addActionListener(e -> openPrivateChat(user));
            userListPanel.add(userButton);
        }
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    private void openPrivateChat(String targetUser) {
        if (openChats.get(targetUser) != null) return;

        JFrame chatFrame = new JFrame("Chat with " + targetUser);
        chatFrame.setSize(300, 400);
        chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chatFrame.setLayout(new BorderLayout());

        chatFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                openChats.remove(targetUser);
                super.windowClosing(e);
            }
        });

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chatPanel);
        chatFrame.add(scrollPane, BorderLayout.CENTER);

        JTextField messageField = new JTextField();
        messageField.addActionListener(e -> {
            String message = messageField.getText();
            out.println("@" + targetUser + " " + message);
            addMessage(chatPanel, "You: " + message);
            messageField.setText("");
        });
        chatFrame.add(messageField, BorderLayout.SOUTH);

        openChats.put(targetUser, chatFrame);
        chatFrame.setVisible(true);
    }

    private void addMessage(JPanel chatPanel, String message) {
        SwingUtilities.invokeLater(() -> {
            chatPanel.add(new JLabel(message));  // Add the new message as a JLabel
            chatPanel.revalidate();       // Revalidate to refresh the layout
            chatPanel.repaint();          // Repaint to ensure the new message is displayed
        });
    }

    public void showMessage(String message) {
        if (message.startsWith("USERS_LIST:")) {
            String[] users = message.substring(11).split(",");
            updateUsersList(users);
        } else if (message.contains("(private)")) {
            addMessage(chatPanel, message.replaceFirst("[(]private[)]", ""));
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Client();
    }
}
