import javax.print.attribute.standard.DateTimeAtCreation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class Client {
    private static final String SERVER_IP = "172.18.32.1"; //127.0.0.1|10.207.61.251|192.168.222.1\172.18.32.1
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
    private JPanel groupsListPanel;
    private JPanel messagesPanel;
    private JScrollPane scrollMessages;
    private JLabel userTalkingTo;
    private JLabel groupTalkingTo;
    private HashMap<String, Boolean> unreadMessages = new HashMap<>();  // Track unread messages per user
    private JPanel chatPanel;
    private JPanel groupChatPanel;
    private String userName;
    public HashMap<String, String[]> groups = new HashMap<>();
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

        /* Create the main panel with CardLayout to switch between sections */
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        /* Add the three sections to the main panel */
        mainPanel.add(createAlertSection(), "Section 1");
        mainPanel.add(createChatSection(), "Section 2");
        mainPanel.add(createGroupSection(), "Section 3");

        cardLayout.show(mainPanel, "Section 2");

        /* Add the main panel to the center of the frame */
        frame.add(mainPanel, BorderLayout.CENTER);

        /* Create the navigation panel with buttons */
        JPanel navigationPanel = new JPanel(new GridLayout(1, 3));
        JButton section1Button = new JButton("Alerts");
        JButton section2Button = new JButton("Chats");
        JButton section3Button = new JButton("Button Panel");

        /* Add action listeners to switch between sections */
        section1Button.addActionListener(e -> cardLayout.show(mainPanel, "Section 1"));
        section2Button.addActionListener(e -> cardLayout.show(mainPanel, "Section 2"));
        section3Button.addActionListener(e -> cardLayout.show(mainPanel, "Section 3"));

        /* Add buttons to the navigation panel */
        navigationPanel.add(section1Button);
        navigationPanel.add(section2Button);
        navigationPanel.add(section3Button);

        /* Add the navigation panel to the bottom of the frame */
        frame.add(navigationPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createAlertSection() {

        /* Panel con las alertas enviadas por los clientes */
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

        /* Panel que contiene los usuarios conectados */
        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        JScrollPane userListScroll = new JScrollPane(userListPanel);

        /* Panel que se muestra al clickar en un usuario para mantener una conversación */
        chatPanel = new JPanel(new BorderLayout());

        /* Panel que se muestra al clickar en un grupo para mantener una conversación */
        groupChatPanel = new JPanel(new BorderLayout());

        chatSection.add(userListScroll, "Users Section");
        chatSection.add(chatPanel, "Chat Section");
        chatSection.add(groupChatPanel, "Group Section");
        chatSection.revalidate();
        chatSection.repaint();

        chatCardLayout.show(chatSection, "Users Section");

        return chatSection;
    }

    private JPanel createGroupSection() {
        JPanel groupPanel = new JPanel(new BorderLayout(10, 4));
        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 20, 10));
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d:00", i);
        }

        /* TODO: Remove groupNameField and add a unique id for each group */

        JLabel labelOrig = new JLabel("Origen");
        JLabel labelDest = new JLabel("Destino");
        JTextField groupNameField = new JTextField(25);
        JTextField groupOrigField = new JTextField(10);
        JTextField groupDestField = new JTextField(10);
        JComboBox groupHour = new JComboBox(hours);
        JButton centeredButton = new JButton("Create Group");

        groupsListPanel = new JPanel();
        groupsListPanel.setLayout(new BoxLayout(groupsListPanel, BoxLayout.Y_AXIS));

        centeredButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = groupNameField.getText();
                String groupOrig = groupOrigField.getText();
                String groupDest = groupDestField.getText();

                JButton bGroup = new JButton(groupName);
                bGroup.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openPrivateGroup(groupName);
                        chatCardLayout.show(chatSection, "Group Section");
                        cardLayout.show(mainPanel, "Section 2");
                    }
                });
                groupNameField.setText("");
                String groupInfo = String.join(";", groupOrig, groupDest, (String) groupHour.getSelectedItem());
                out.println("[NEW_GROUP] " + groupName + ":" + userName + " " + groupInfo);
            }
        });
        inputPanel.add(groupNameField);
        inputPanel.add(groupHour);
        inputPanel.add(labelOrig);
        inputPanel.add(groupOrigField);
        inputPanel.add(labelDest);
        inputPanel.add(groupDestField);
        inputPanel.add(centeredButton);
        groupPanel.add(inputPanel, BorderLayout.NORTH);
        groupPanel.add(groupsListPanel, BorderLayout.CENTER);
        return groupPanel;
    }

    public void updateGroupsList(HashMap<String, String[]> groupNames) {
        groupsListPanel.removeAll();
        for (String groupName : groupNames.keySet()) {
            String[] info = groupNames.get(groupName);
            JButton groupButton = new JButton(info[0] + "-" + info[1]);
            groupButton.addActionListener(e -> joinGroupChat(groupName));
            groupsListPanel.add(groupButton);
        }
        groupsListPanel.revalidate();
        groupsListPanel.repaint();
    }

    private void joinGroupChat(String groupName) {
        out.println("JOIN_GROUP " + this.userName + " TO " + groupName);
        openPrivateGroup(groupName);
        chatCardLayout.show(chatSection, "Group Section");
        cardLayout.show(mainPanel, "Section 2");
    }

    private void updateUsersList(String[] users) {
        userListPanel.removeAll();

        for (String user : users) {
            JButton userButton = new JButton(user);
            userButton.setName(user);
            if (unreadMessages.containsKey(user)) {userButton.setBackground(Color.ORANGE);}
            userButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userButton.setBackground(Color.WHITE);
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
        unreadMessages.put(fromUser, true);
        updateUserList(fromUser, message);
    }

    private void showUsersList() {
        if (userTalkingTo != null) {userTalkingTo.setText("");};
        if (groupTalkingTo != null) {groupTalkingTo.setText("");};
        chatCardLayout.show(chatSection, "Users Section");
    }

    private void updateUserList(String user, String message) {
        if (userListPanel.getComponents() != null) {
            for (Component b : userListPanel.getComponents()) {
                if (b.getName().equals(user)) {
                    JButton button = (JButton) b;
                    button.setBackground(Color.ORANGE);
                    button.setText(String.format(button.getName() + "%-20s" + message, ""));
                }
            }
        }
    }

    private void openPrivateGroup(String group) {
        groupChatPanel.removeAll();

        System.out.println(groups);
        JPanel headerPanel = new JPanel(new BorderLayout());
        groupTalkingTo = new JLabel(group);
        JButton exitChatButton = new JButton("<--");
        exitChatButton.addActionListener(e -> {
            showUsersList();
        });
        headerPanel.add(exitChatButton, BorderLayout.WEST);
        headerPanel.add(groupTalkingTo, BorderLayout.CENTER);
        groupChatPanel.add(headerPanel, BorderLayout.NORTH);

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        scrollMessages = new JScrollPane(messagesPanel);
        groupChatPanel.add(scrollMessages, BorderLayout.CENTER);

        JTextField messageField = new JTextField();
        messageField.addActionListener(e -> {
            String message = messageField.getText();
            out.println("GROUP " + group + ":" + userName + "] " + message);
            messageField.setText("");
        });
        groupChatPanel.add(messageField, BorderLayout.SOUTH);
        groupChatPanel.revalidate();
        groupChatPanel.repaint();
    }

    private void openPrivateChat(String targetUser) {

        chatPanel.removeAll();

        JPanel headerPanel = new JPanel(new BorderLayout());
        userTalkingTo = new JLabel(targetUser);
        JButton exitChatButton = new JButton("<--");
        exitChatButton.addActionListener(e -> {
            showUsersList();
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
        if (chatPanel==null || scrollMessages==null) {
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

    public void addGroup(String groupName, String[] groupInfo) {
        groups.put(groupName, groupInfo);
    }

    public HashMap<String, String[]> getGroups() {
        return groups;
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
                updateUsersList(users);
            }
        } else if (message.contains("(private)")) {
            addMessage(chatPanel, message.replaceFirst("[(]private[)]", ""));
        } else if (message.contains("(DM)")) {
            String user = message.substring(0, message.indexOf("(DM)"));
            String msg = message.substring(message.indexOf("(DM):")+5);
            receiveMessage(user, msg);
        } else if (message.startsWith("GROUP ")) {
            String group = message.substring(6, message.indexOf(":"));
            String sender = message.substring(message.indexOf(":")+1, message.indexOf("]"));
            String msg = message.substring(message.indexOf("]")+2);
            if (sender.equals(this.userName)) {
                addMessage(groupChatPanel, "You: " + msg);
            } else if (groupTalkingTo != null) {
                if (groupTalkingTo.getText().equals(group)) {
                    addMessage(groupChatPanel, sender + ": " + msg);
                }
            }
        } else if (message.startsWith("[NEW_GROUP] ")) {
            int ind = message.indexOf(":");
            groups.put(message.substring(message.indexOf(" ")+1, ind), message.substring(message.indexOf(" ", ind)).split(";"));
            updateGroupsList(getGroups());
        } else {
            System.out.println("WOW" + message);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
