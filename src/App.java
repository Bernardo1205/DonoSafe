import Mapa.MainPanel;
import Mapa.VentanaAgregarWaypoint;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class App extends JFrame {
    private JPanel panelPrincipal;
    private boolean esperarClick;
    private String textoWaypoint;
    private VentanaAgregarWaypoint ventanaAgregarWaypoint;
    private MainPanel panelMapa;

    private Client client;
    private String grupoActual = "";
    private CardLayout cardLayout;
    private CardLayout chatCardLayout;
    private JPanel mainPanel;
    private JPanel chatPanel;
    private JPanel chatSection;
    private JPanel userListPanel;
    private JPanel groupChatPanel;
    private JPanel groupsListPanel;
    private JPanel alertsArea;
    private JPanel messagesPanel;
    private JScrollPane scrollMessages;
    private JLabel userTalkingTo;
    private JLabel groupTalkingTo;
    private HashMap<String, Boolean> unreadMessages = new HashMap<>();
    public HashMap<String, String[]> grupos = new HashMap<>();



    public App(){
        panelPrincipal = new JPanel(new CardLayout());


        panelPrincipal.add(createMapSection(),"MainPanel");
        panelPrincipal.add(createChatSection(),"ChatSection");
        panelPrincipal.add(createGroupSection(),"GroupSection");


        JPanel panelBotones = new JPanel(new GridLayout(1,3));
        JButton jButton = new JButton("Alerta");
        jButton.addActionListener(e -> {
            /* Todo manejar cliente handler para avisar a los usuarios que hay nueva alerta para ello vamos
                actualizando el hashmap con todos los waypoints de todos los usuarios que se encuentra en la clase Map */
            agregarAlerta();
        });
        panelBotones.add(jButton);


        setLayout(new BorderLayout());
        add(panelBotones,BorderLayout.SOUTH);
        add(panelPrincipal,BorderLayout.CENTER);
        setTitle("Mapa");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public  void  cambairPanel(JPanel panel , String panelName){
        panelPrincipal.add(panel,panelName);
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();
        cardLayout.show(panelPrincipal,panelName);
    }

    /**
     * @param ventanaAgregarWaypoint Ventana que se muestra para agregar un waypoint
     */
    public void agregarAlerta(){
        ventanaAgregarWaypoint = new VentanaAgregarWaypoint(this);
        ventanaAgregarWaypoint.setVisible(true);
        textoWaypoint = ventanaAgregarWaypoint.getTextWaypoint();
        if(textoWaypoint!=null&&!textoWaypoint.isEmpty()){
            esperarClick=true;
            panelMapa.setEsperarClick(esperarClick);
            panelMapa.setTextoWaypoint(textoWaypoint);
            panelMapa.setPeligrosidad(ventanaAgregarWaypoint.getPeligrosidad());
            JOptionPane.showMessageDialog(this,"Haz click en el mapa para colocar el waypoint");
        }
    }
    private JPanel createMapSection() {
        JPanel mapPanel = new JPanel(new BorderLayout());
        panelMapa = new MainPanel(this);
        mapPanel.add(panelMapa, BorderLayout.CENTER);
        return mapPanel;
    }


    /**
     * @return El panel de la sección de alertas
     */
//    private JPanel createAlertSection() {
//
//        /* Panel con las alertas enviadas por los clientes */
//        JPanel alertPanel = new JPanel(new BorderLayout());
//        alertsArea = new JPanel();
//        alertsArea.setLayout(new BoxLayout(alertsArea, BoxLayout.Y_AXIS));
//
//        JScrollPane scrollPane = new JScrollPane(alertsArea);
//
//        JTextField alertField = new JTextField();
//        JButton sendAlertButton = new JButton("Send Alert");
//
//        sendAlertButton.addActionListener(e -> {
//            String alertMessage = alertField.getText();
//            if (!alertMessage.isEmpty()) {
//                client.sendMessageToServer("[ALERT] " + alertMessage);  // Send alert to the server
//                alertField.setText("");
//            }
//        });
//
//        JPanel inputPanel = new JPanel(new BorderLayout());
//        inputPanel.add(alertField, BorderLayout.CENTER);
//        inputPanel.add(sendAlertButton, BorderLayout.EAST);
//
//        alertPanel.add(scrollPane, BorderLayout.CENTER);
//        alertPanel.add(inputPanel, BorderLayout.SOUTH);
//
//        return alertPanel;
//    }

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
        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 10, 5));
        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d:00", i);
        }

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
                if (!groupOrigField.getText().isEmpty() || !groupDestField.getText().isEmpty()) {
                    String groupInfo = String.join(";", groupOrigField.getText(), groupDestField.getText(), (String) groupHour.getSelectedItem());
                    client.sendMessageToServer("[NEW_GROUP] " + client.getUserame() + " " + groupInfo);
                }
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

    public void updateGroupsList(HashMap<String, String[]> groups) {
        groupsListPanel.removeAll();

        for (String groupId : groups.keySet()) {
            String[] info = groups.get(groupId);
            JButton groupButton = new JButton(info[0] + " - " + info[1]);
            groupButton.addActionListener(e -> client.joinGroupChat(groupId));
            groupsListPanel.add(groupButton);
        }
        groupsListPanel.revalidate();
        groupsListPanel.repaint();
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
     * Cambia el color del fondo y el texto de aquel boton que contenga
     * el chat del usuario para hacerle saber que esa persona quiere
     * hablar con él/ella
     * @param user
     * @param message
     */
    public void updateUserList(String user, String message) {
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
     * Añade una alerta
     * @param alert
     */
    void showAlert(String alert) {
        SwingUtilities.invokeLater(() -> {
            alertsArea.add(new JLabel(alert));
            alertsArea.repaint();
            alertsArea.revalidate();
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
     * Abre el panel del chat de grupo indicado
     * @param group
     */
    public void openPrivateGroup(String group) {
        chatCardLayout.show(chatSection, "Group Section");
        cardLayout.show(mainPanel, "Section 2");
        groupChatPanel.removeAll();

        String[] groupInfo = client.getGrupos().get(group);

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
            if (!message.isEmpty()) {
                client.sendMessageToServer("GROUP " + group + ":" + client.getUserame() + "] " + message);
                messageField.setText("");
            }
        });
        groupChatPanel.add(messageField, BorderLayout.SOUTH);
        groupChatPanel.revalidate();
        groupChatPanel.repaint();
    }

    public void openPrivateChat(String targetUser) {

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

        /* Envía un mensaje al servidor indicando a quien va dirigido el mensaje y su contenido */
        JTextField messageField = new JTextField();
        messageField.addActionListener(e -> {
            String message = messageField.getText();
            client.sendMessageToServer("@" + targetUser + " " + message);
            addPrivateMessage("You: " + message);
            messageField.setText("");
        });
        chatPanel.add(messageField, BorderLayout.SOUTH);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    public void addPrivateMessage(String message) {
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

    public void addGroupMessage(String message) {
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

    public String getUserTalkingTo() {
        if (userTalkingTo==null) return "";
        return this.userTalkingTo.getText();
    }

    public JPanel getGroupChatPanel() {
        return groupChatPanel;
    }

    public String getGrupoActual() {
        return this.grupoActual;
    }

    public HashMap<String, String[]> getGrupos() {
        return grupos;
    }
    public static void main(String[] args) {
        new App();
    }
}
