import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class Client {

    private static final String SERVER_IP = "10.207.61.251";
    private static final int SERVER_PORT = 9090;
    private static final int WIDTH = 350;
    private static final int HEIGHT = 500;
    private JFrame frame;
    private CardLayout cardLayout;
    private CardLayout chatCardLayout;
    private JPanel mainPanel;
    private JTextArea alertArea;
    private JPanel chatSection;
    private JPanel userListPanel;
    private JPanel messagesPanel;
    private JScrollPane scrollMessages;
    private JLabel userTalkingTo;
    private HashMap<String, Boolean> unreadMessages = new HashMap<>();  // Track unread messages per user
    private JPanel chatPanel;
    private String userName;
    private static Socket socket;
    private static PrintWriter out;

    public Client() {
        new ClientLogin(this);
    }

    public void openChatWindow(String username) {
        userName = username;
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

        cardLayout.show(mainPanel, "Section 2");

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
        // Panel con las alertas enviadas por los clientes
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

        chatCardLayout = new CardLayout();
        chatSection = new JPanel(chatCardLayout);

        // Panel que contiene los usuarios conectados
        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        JScrollPane userListScroll = new JScrollPane(userListPanel);

        // Panel que se muestra al clickar en un usuario para establecer una conversación
        chatPanel = new JPanel(new BorderLayout());

        chatSection.add(userListScroll, "Users Section");
        chatSection.add(chatPanel, "Chat Section");
        chatSection.revalidate();
        chatSection.repaint();

        chatCardLayout.show(chatSection, "Users Section");

        return chatSection;
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
            if (unreadMessages.containsKey(user)) {userButton.setBackground(Color.RED);}
            userButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openPrivateChat(user);
                    chatCardLayout.show(chatSection, "Chat Section");
                }
            });
            userListPanel.add(userButton);
        }
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    // Añade una alerta
    void showAlert(String alert) {
        SwingUtilities.invokeLater(() -> {
            alertArea.append(alert + "\n");
        });
    }

    // Método para notificar al usuario que tiene mensajes no leídos
    public void receiveMessage(String fromUser, String message) {
        if (!unreadMessages.containsKey(fromUser)) {
            unreadMessages.put(fromUser, true);
            updateUserList(fromUser);
        }
    }

    private void updateUserList(String user) {
        JLabel userLabel = new JLabel(user);
        userLabel.setOpaque(true);
        userLabel.setBackground(unreadMessages.getOrDefault(user, false) ? Color.ORANGE : Color.WHITE);
        userLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                unreadMessages.put(user, false);
                userLabel.setBackground(Color.WHITE);  // Reset color on open
                openPrivateChat(user);
                chatCardLayout.show(chatSection, "Chat Section");
            }
        });

        userListPanel.add(userLabel);
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    private void openPrivateChat(String targetUser) {

        chatPanel.removeAll();

        JPanel headerPanel = new JPanel(new BorderLayout());
        userTalkingTo = new JLabel(targetUser);
        JButton exitChatButton = new JButton("<--");
        exitChatButton.addActionListener(e -> {
            chatCardLayout.show(chatSection, "Users Section");
        });
        headerPanel.add(exitChatButton, BorderLayout.WEST);
        headerPanel.add(userTalkingTo, BorderLayout.CENTER);

        chatPanel.add(headerPanel, BorderLayout.NORTH);

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        scrollMessages = new JScrollPane(messagesPanel);
        chatPanel.add(scrollMessages, BorderLayout.CENTER);

        JTextField messageField = new JTextField();
        messageField.addActionListener(e -> {
            String message = messageField.getText();
            out.println("@" + targetUser + " " + message);
            addMessage(chatPanel, "You: " + message);
            messageField.setText("");
        });
        chatPanel.add(messageField, BorderLayout.SOUTH);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    private void addMessage(JPanel chatPanel, String message) {
        if (chatPanel==null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            // Verifica si el scroll está en el extremo inferior
            boolean isAtBottom = scrollMessages.getVerticalScrollBar().getValue() +
                    scrollMessages.getVerticalScrollBar().getVisibleAmount() ==
                    scrollMessages.getVerticalScrollBar().getMaximum();
            messagesPanel.add(new JLabel(message));
            messagesPanel.revalidate();       // Revalidate to refresh the layout
            messagesPanel.repaint();          // Repaint to ensure the new message is displayed
            // Si el scroll estaba abajo, desplazarse automáticamente al final
            if (isAtBottom) {
                SwingUtilities.invokeLater(() -> {
                    scrollMessages.getVerticalScrollBar().setValue(
                            scrollMessages.getVerticalScrollBar().getMaximum());
                });
            }
        });
    }

    public String getDisplayedChat() {
        try {
            String s = this.userTalkingTo.getText();
            return s;
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void showMessage(String message) {
        if (message.startsWith("USERS_LIST:")) {
            if (message.contains("," + userName + ",")) {
                message = message.replace("," + userName + ",", ",");
            } else {
                message = message.replaceFirst(",?" + userName + ",?", "");
            }
            String[] users = message.substring(11).split(",");
            if (!users[0].isEmpty()) {
                System.out.println(users[0]);
                System.out.println(Arrays.toString(users));
                updateUsersList(users);
            }
        } else if (message.contains("(private)")) {
            addMessage(chatPanel, message.replaceFirst("[(]private[)]", ""));
        } else if (message.contains("(DM)")) {
            String user = message.substring(0, message.indexOf("(DM)"));
            String msg = message.substring(message.indexOf("(DM):")+5);
            receiveMessage(user, msg);
        }
        else {
            System.out.println("WOW" + message);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
