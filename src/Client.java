import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    private static JTextArea alertArea;
    private static JPanel chatSection;
    private JPanel chatPanel;
    private static Socket socket;
    private static PrintWriter out;

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
        frame = new JFrame("Client Application");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(5, 10));

        // Create the main panel with CardLayout to switch between sections
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add the three sections to the main panel
        mainPanel.add(createAlertSection(), "Section 1");
        mainPanel.add(createChatSection(), "Section 2");
        mainPanel.add(createButtonSection(), "Section 3");

        // Add the main panel to the center of the frame
        frame.add(mainPanel, BorderLayout.CENTER);

        // Create the navigation panel with buttons
        JPanel navigationPanel = new JPanel(new GridLayout(1, 3));
        JButton section1Button = new JButton("Alerts");
        JButton section2Button = new JButton("Chats");
        JButton section3Button = new JButton("Button Panel");

        // Add action listeners to switch between sections
        section1Button.addActionListener(e -> cardLayout.show(mainPanel, "Section 1"));
        section2Button.addActionListener(e -> cardLayout.show(mainPanel, "Section 2"));
        section3Button.addActionListener(e -> cardLayout.show(mainPanel, "Section 3"));

        // Add buttons to the navigation panel
        navigationPanel.add(section1Button);
        navigationPanel.add(section2Button);
        navigationPanel.add(section3Button);

        // Add the navigation panel to the bottom of the frame
        frame.add(navigationPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createAlertSection() {
        JPanel alertPanel = new JPanel(new BorderLayout());
        alertArea = new JTextArea();
        alertArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(alertArea);

        JTextField alertField = new JTextField();
        JButton sendAlertButton = new JButton("Send Alert");

        sendAlertButton.addActionListener(e -> {
            String alertMessage = alertField.getText();
            if (!alertMessage.isEmpty()) {
                out.println("[ALERT] " + alertMessage);  // Send alert to the server
                alertField.setText("");
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(alertField, BorderLayout.CENTER);
        inputPanel.add(sendAlertButton, BorderLayout.EAST);

        alertPanel.add(scrollPane, BorderLayout.CENTER);
        alertPanel.add(inputPanel, BorderLayout.SOUTH);

        return alertPanel;
    }

    private JPanel createChatSection() {
        chatSection = new JPanel(new BorderLayout());

        JPanel userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        JScrollPane userListScroll = new JScrollPane(userListPanel);

        chatSection.add(userListScroll, BorderLayout.WEST);

        updateUserListPanel(userListPanel);

        return chatSection;
    }

    private void updateUserListPanel(JPanel userListPanel) {
        userListPanel.removeAll();
        JButton userButton = new JButton("User 1");
        userButton.addActionListener(e -> openPrivateChat("User 1"));
        userListPanel.add(userButton);

        userButton = new JButton("User 2");
        userButton.addActionListener(e -> openPrivateChat("User 2"));
        userListPanel.add(userButton);
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel();
        JButton centeredButton = new JButton("Centered Button");
        buttonPanel.add(centeredButton);
        return buttonPanel;
    }

    private void updateUsersList(String[] users) {
        JPanel userListPanel = (JPanel) ((JScrollPane) ((JPanel) mainPanel.getComponent(1)).getComponent(0)).getViewport().getView();
        userListPanel.removeAll();

        for (String user : users) {
            JButton userButton = new JButton(user);
            userButton.addActionListener(e -> openPrivateChat(user));
            userListPanel.add(userButton);
        }
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    void showAlert(String alert) {
        SwingUtilities.invokeLater(() -> {
            alertArea.append(alert + "\n");
        });
    }

    private void openPrivateChat(String targetUser) {

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chatPanel);
        chatSection.add(scrollPane, BorderLayout.CENTER);

        JLabel title = new JLabel(targetUser);
        chatPanel.add(title, BorderLayout.NORTH);

        JTextField messageField = new JTextField();
        messageField.addActionListener(e -> {
            String message = messageField.getText();
            out.println("@" + targetUser + " " + message);
            addMessage(chatPanel, "You: " + message);
            messageField.setText("");
        });
        chatSection.add(messageField, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void addMessage(JPanel chatPanel, String message) {
        if (chatPanel==null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            chatPanel.add(new JButton(message));
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
        } else {
            System.out.println(message);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
