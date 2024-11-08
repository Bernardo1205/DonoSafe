package main.java.com.example.client;

import main.java.com.example.config.ClientConfig;
import main.java.com.example.config.ServerConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

/**
 * La clase Client se encarga de renderizar la pantalla del cliente
 * y de conectarse al servidor para luego mandar y recibir mensajes.
 *
 * @see ClientLogin
 * @see ServerConnection
 * @author daniel.sanchez.r
 */
public class Client {
    private String grupoActual = "";
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
    public HashMap<String, String[]> grupos = new HashMap<>();
    private static Socket socket;
    private static PrintWriter out;

    /**
     * Registrar o ubicar al cliente en la base de datos
     */
    public Client() {
        new ClientLogin(this);
    }

    /**
     * Tras haberse registrado correctamente, se renderiza la pantalla del
     * cliente y se conecta al servidor
     * @param username
     * @see ServerConnection
     */
    public void openChatWindow(String username) {

        userName = username;

        setupGUI();
        try {
            socket = new Socket(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT);

            ServerConnection serverConn = new ServerConnection(socket, this);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username);

            new Thread(serverConn).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea la ventana principal y añade 3 secciones (alerta, chats, grupos);
     * cada una de ellas, con sus respectivos botones, áreas de texto, etiquetas...
     */
    private void setupGUI() {

        frame = new JFrame("main.java.com.example.client.Client Application");
        frame.setSize(ClientConfig.WIDTH, ClientConfig.HEIGHT);
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
        JButton section3Button = new JButton("Groups");

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

    /**
     * Sección de las alertas:
     * <p>
     *     Permite visualizar y enviar mensajes que informan a todos los
     *     usuarios de posibles amenazas
     * </p>
     * @return El panel de la sección de alertas
     */
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

    /**
     * Sección de los chats:
     * <pre>
     * Contiene un cardLayout para cambiar entre los siguientes paneles:
     *      Panel de usuarios: Muestras todos los usuarios conectados
     *      Panel del chat: Muestra el chat privado con una persona
     *      Panel del chat de grupo: Muestra el chat al cual tienen acceso los miembros del grupo
     * </pre>
     * @return El panel de la sección de chats
     */
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

    /**
     * Sección de los grupos:
     * <p> Contiene los componentes para crear un grupo nuevo junto con el listado de
     * grupos creados
     * @return El panel de la sección de grupos
     */
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
        JTextField groupOrigField = new JTextField(10);
        JTextField groupDestField = new JTextField(10);
        JComboBox groupHour = new JComboBox(hours);
        JButton createGroupButton = new JButton("Create Group");

        groupsListPanel = new JPanel();
        groupsListPanel.setLayout(new BoxLayout(groupsListPanel, BoxLayout.Y_AXIS));

        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupInfo = String.join(";", groupOrigField.getText(),
                        groupDestField.getText(), (String) groupHour.getSelectedItem());
                out.println("[NEW_GROUP] " + userName + " " + groupInfo);
            }
        });
        inputPanel.add(groupHour);
        inputPanel.add(labelOrig);
        inputPanel.add(groupOrigField);
        inputPanel.add(labelDest);
        inputPanel.add(groupDestField);
        inputPanel.add(createGroupButton);
        groupPanel.add(inputPanel, BorderLayout.NORTH);
        groupPanel.add(groupsListPanel, BorderLayout.CENTER);
        return groupPanel;
    }

    public void updateGroupsList(HashMap<String, String[]> groupNames) {
        groupsListPanel.removeAll();

        for (String groupName : groupNames.keySet()) {
            String[] info = groupNames.get(groupName);
            for (String i : info) {
                System.out.println(i);
            }
            JButton groupButton = new JButton(info[0] + "-" + info[1]);
            groupButton.addActionListener(e -> joinGroupChat(groupName));
            groupsListPanel.add(groupButton);
        }
        groupsListPanel.revalidate();
        groupsListPanel.repaint();
    }

    /**
     * Envía un mensaje al servidor mostrando el usuario y el grupo al
     * que quiere acceder.
     *
     * @param groupId
     */
    private void joinGroupChat(String groupId) {
        out.println("JOIN_GROUP " + this.userName + " TO " + groupId);
        openPrivateGroup(groupId);
        chatCardLayout.show(chatSection, "Group Section");
        cardLayout.show(mainPanel, "Section 2");
    }

    /**
     * Actualiza el panel con el listado de usuarios conectados
     * @param users
     */
    public void updateUsersList(String[] users) {
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

    /**
     * Añade una alerta
     * @param alert
     */
    void showAlert(String alert) {
        SwingUtilities.invokeLater(() -> {
            alertArea.append(alert + "\n");
        });
    }

    /**
     * Añade el mensaje a {@link #unreadMessages} y llama a {@link #updateUserList(String, String)}
     *
     * @param fromUser
     * @param message
     */
    public void receiveMessage(String fromUser, String message) {
        unreadMessages.put(fromUser, true);
        updateUserList(fromUser, message);
    }

    /**
     * Cambia la vista del {@link #cardLayout} a la del panel con el
     * listado de usuarios conectados
     */
    private void showUsersList() {
        if (userTalkingTo != null) {userTalkingTo.setText("");};
        if (groupTalkingTo != null) {groupTalkingTo.setText("");};
        chatCardLayout.show(chatSection, "Users Section");
    }

    /**
     * Cambia el color del fondo y el texto de aquel boton que contenga
     * el chat del usuario para hacerle saber que esa persona quiere
     * hablar con él/ella
     * @param user
     * @param message
     */
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

    /**
     * Abre el panel del chat de grupo indicado
     * @param group
     */
    private void openPrivateGroup(String group) {
        groupChatPanel.removeAll();

        String[] groupInfo = grupos.get(group);

        grupoActual = group;

        JPanel headerPanel = new JPanel(new BorderLayout());
        groupTalkingTo = new JLabel(groupInfo[0] + " - " + groupInfo[1]);
        JButton exitChatButton = new JButton("<--");
        exitChatButton.addActionListener(e -> {
            grupoActual = "";
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

    public void addMessage(JPanel chatPanel, String message) {
//        if (chatPanel==null || scrollMessages==null) {
//            return;
//        }
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

    public String getDisplayedChat(int option) {
        try {
            return (option==0) ? this.userTalkingTo.getText() : grupoActual;
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void addGroup(String groupId, String[] groupInfo) {
        grupos.put(groupId, groupInfo);
    }

    public JPanel getChatPanel(int option) {
        return (option==0) ? chatPanel : groupChatPanel;
    }

    public String getUserame() {
        return this.userName;
    }

    public String getGrupoActual() {
        return this.grupoActual;
    }

    public HashMap<String, String[]> getGrupos() {
        return grupos;
    }

    public static void main(String[] args) {
        new Client();
    }
}
