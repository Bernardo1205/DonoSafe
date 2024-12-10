package main.java.com.example.client;

import main.java.com.example.config.ClientConfig;
import main.java.com.example.mapa.MainPanel;


import main.java.com.example.mapa.Reporte;

import main.java.com.example.mapa.VentanaAgregarWaypoint;
import main.java.com.example.mapa.VentanaCrearGrupo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class App extends JFrame {

    private String grupoActual = "";
    private boolean esperarClick;
    private String textoWaypoint;

    private VentanaAgregarWaypoint ventanaAgregarWaypoint;
    private MainPanel panelMapa;
    private Client client;

    private CardLayout cardLayout;
    private CardLayout chatCardLayout;
    private JLayeredPane layeredPane;
    private JPanel panelPrincipal;
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

    private DefaultTableModel modeloGrupos;
    private DefaultTableModel modeloUsers;
    private JTable tablaUsers;

    private HashMap<String, Boolean> unreadMessages = new HashMap<>();
    public HashMap<String, String[]> grupos = new HashMap<>();

    public App(Client client){
        setTitle("Mapa");
        setSize(ClientConfig.WIDTH, ClientConfig.HEIGHT);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        this.client = client;


        // Layout principal con tarjetas
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);


        // Añadir paneles de Mapa, Chat y Grupos
        panelPrincipal.add(createMapSection(), "MainPanel");
        panelPrincipal.add(createChatSection(), "ChatSection");
        panelPrincipal.add(createGroupSection(), "GroupSection");


        // Botones de navegación
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));


        JButton jButtonAlerta = new JButton("Mapa");
        JButton jButtonChats = new JButton("Chats");
        JButton jButtonGroups = new JButton("Grupos");


        // Estilo moderno para botones
        styleButton(jButtonAlerta);
        styleButton(jButtonChats);
        styleButton(jButtonGroups);


        // Acciones de los botones
        jButtonAlerta.addActionListener(e -> cardLayout.show(panelPrincipal, "MainPanel"));
        jButtonGroups.addActionListener(e -> cardLayout.show(panelPrincipal, "GroupSection"));
        jButtonChats.addActionListener(e -> cardLayout.show(panelPrincipal, "ChatSection"));


        // Añadir botones al panel
        panelBotones.add(jButtonAlerta);
        panelBotones.add(jButtonChats);
        panelBotones.add(jButtonGroups);


        // Añadir paneles al JFrame
        add(panelBotones, BorderLayout.SOUTH);
        add(panelPrincipal, BorderLayout.CENTER);


        setVisible(true);
    }

    public void cambiarPanel(JPanel panel, String panelName){
        panelPrincipal.add(panel, panelName);
        CardLayout cardLayout = (CardLayout) panelPrincipal.getLayout();
        cardLayout.show(panelPrincipal, panelName);
    }
    private void styleButton(JButton button) {
        // Estilo moderno para los botones
        button.setBackground(new Color(72, 122, 240));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 1));
        button.setFocusPainted(false);


        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(85, 140, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(72, 122, 240));
            }
        });
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
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));

        panelMapa = new MainPanel(this, client);
        panelMapa.setOnWaypointAdded(this::waypointAdded);
        panelMapa.setBounds(0, 0, 800, 600);

        AnimatedMenu menu = new AnimatedMenu(layeredPane);

        JButton bAñadirAlerta = new JButton("Crear Alerta");
        bAñadirAlerta.addActionListener(e -> agregarAlerta());
        bAñadirAlerta.setBounds(10, 60, 120, 40);
        bAñadirAlerta.setBackground(new Color(255, 100, 100));
        bAñadirAlerta.setForeground(Color.WHITE);
        bAñadirAlerta.setFont(new Font("Arial", Font.BOLD, 14));
        bAñadirAlerta.setFocusPainted(false);

        JButton bMostrarMenu = new JButton("Menú");
        bMostrarMenu.setBounds(10, 10, 120, 40);
        bMostrarMenu.setBackground(new Color(255, 100, 100));
        bMostrarMenu.setForeground(Color.WHITE);
        bMostrarMenu.setFont(new Font("Arial", Font.BOLD, 14));
        bMostrarMenu.setFocusPainted(false);

        bMostrarMenu.addActionListener(e -> menu.toggleMenu());

        layeredPane.add(panelMapa, Integer.valueOf(0));
        layeredPane.add(bAñadirAlerta, Integer.valueOf(1));
        layeredPane.add(bMostrarMenu, Integer.valueOf(2));
        layeredPane.add(menu, Integer.valueOf(1));


        mapPanel.add(layeredPane);


        return mapPanel;
    }


    private void waypointAdded(Reporte reporte){
        String s = String.join(",", String.valueOf(reporte.getPosition().getLongitude()), String.valueOf(reporte.getPosition().getLatitude()), ventanaAgregarWaypoint.getPeligrosidad(), reporte.getHoraCreacion().toString(), String.valueOf(reporte.getUser().getId()), reporte.getDescripcion());
        client.sendMessageToServer("[NEW_WAYPOINT] " + s);
    }

    public void updateWaypoints(Reporte reporte){
        Set<Reporte> set = panelMapa.getMap().getSetWaipoints();
        set.add(reporte);
        panelMapa.getMap().añadirWaypoint(set);
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

        JPanel panel = new JPanel(new BorderLayout());
        chatCardLayout = new CardLayout();
        chatSection = new JPanel(chatCardLayout);

        /* Panel que contiene los usuarios conectados */
        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        modeloUsers = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloUsers.setColumnCount(3);

        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                /* Todo pillar de la base de datos usuarios activos y los que no esten activos poner icono rojo habra que crear
                    funcion que compare lista users del server con los de la base de datos para determinar cuales estan activos */

                String id = (String) modeloUsers.getValueAt(row,0);
                if (unreadMessages.containsKey(id)){
                    c.setBackground(Color.ORANGE);
                } else {
                    c.setBackground(Color.WHITE);
                }
                if (column == 2) { //&& true
                    ImageIcon icon = new ImageIcon("src/main/resources/icons/boton.png");
                    Image scaledImage = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                    c.setIcon(new ImageIcon(scaledImage));
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                    c.setVerticalAlignment(SwingConstants.CENTER);
                } else {
                    c.setIcon(null);
                }
//                if (isSelected){
//                    c.setBackground(table.getSelectionBackground());;
//                }
                c.setOpaque(column != 2);
                tablaUsers.setRowHeight(20);

                return c;
            }

        };

        tablaUsers = new JTable(modeloUsers);

        tablaUsers.setTableHeader(null);
        tablaUsers.getColumnModel().getColumn(0).setMinWidth(0);
        tablaUsers.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaUsers.getColumnModel().getColumn(0).setWidth(0);

        tablaUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaUsers.setDefaultRenderer(Object.class, defaultTableCellRenderer);
        tablaUsers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaUsers.rowAtPoint(e.getPoint());
                if (fila!=-1){
                    String idUser = (String) modeloUsers.getValueAt(fila,0);
                    if (unreadMessages.containsKey(idUser)) {
                        unreadMessages.remove(idUser);
                        modeloUsers.setValueAt(idUser, fila, 1);
                        tablaUsers.repaint();
                        tablaUsers.revalidate();
                    }
                    openPrivateChat(idUser);
                    chatCardLayout.show(chatSection, "Chat Section");
                }
            }
        });

        userListPanel.add(new JScrollPane(tablaUsers));

        /* Panel que se muestra al clickar en un usuario para mantener una conversación */
        chatPanel = new JPanel(new BorderLayout());

        /* Panel que se muestra al clickar en un grupo para mantener una conversación */
        groupChatPanel = new JPanel(new BorderLayout());

        chatSection.add(userListPanel, "Users Section");
        chatSection.add(chatPanel, "Chat Section");
        chatSection.add(groupChatPanel, "Group Section");
        chatSection.revalidate();
        chatSection.repaint();

        chatCardLayout.show(chatSection, "Users Section");
        return chatSection;
    }

    private int getRowByValue(String value) {
        for (int i = 0; i < modeloUsers.getRowCount(); i++) {
            if (modeloUsers.getValueAt(i, 0).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Sección de los grupos:
     * <p> Contiene los componentes para crear un grupo nuevo junto con el listado de
     * grupos creados
     * @return El panel de la sección de grupos
     */
    private JPanel createGroupSection() {
        JPanel groupPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridLayout(1,2,0,5));

        groupsListPanel = new JPanel();
        groupsListPanel.setLayout(new BoxLayout(groupsListPanel, BoxLayout.Y_AXIS));

        Vector<String> cabeceraGrupos = new Vector<String>(Arrays.asList("ID", "Origen", "Destino","Hora salida"));

        modeloGrupos = new DefaultTableModel(new Vector<Vector<Object>>(), cabeceraGrupos){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel jLabel =(JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column!=3){
                    jLabel.setToolTipText(modeloGrupos.getValueAt(row, column).toString());
                }
                else{
                    jLabel.setHorizontalAlignment(HORIZONTAL);
                    jLabel.setToolTipText(null);
                }

                return jLabel;
            }
        };

        JTable tablaGrupos = new JTable(modeloGrupos);
        tablaGrupos.setDefaultRenderer(Object.class, defaultTableCellRenderer);

        // Ocultar la columna del ID del grupo
        tablaGrupos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaGrupos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaGrupos.getColumnModel().getColumn(0).setWidth(0);

        tablaGrupos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaGrupos.rowAtPoint(e.getPoint());
                if (fila!=-1){
                    String idGrupo = (String) modeloGrupos.getValueAt(fila,0);
                    client.joinGroupChat(idGrupo);
                    JOptionPane.showMessageDialog(App.this,String.join(" ","Te has unido al grupo con destino: " , tablaGrupos.getValueAt(fila,2).toString()));
                }
            }
        });
        JScrollPane groupsListScroll = new JScrollPane(tablaGrupos);

        groupsListPanel.add(groupsListScroll, BorderLayout.WEST);

        centerPanel.add(groupsListPanel);
        centerPanel.add(new VentanaCrearGrupo(client));
        groupPanel.add(centerPanel, BorderLayout.CENTER);

        return groupPanel;
    }

    public void updateGroupsList(HashMap<String, String[]> groups) {
        modeloGrupos.setRowCount(0);

        for (String groupId : groups.keySet()) {
            String[] info = groups.get(groupId);
            modeloGrupos.addRow(new Object[]{groupId, info[0], info[1], info[2]});
        }
        groupsListPanel.revalidate();
        groupsListPanel.repaint();
    }

    /**
     * Actualiza el panel con el listado de usuarios conectados
     * @param users
     */
    public void updateUsersList(String[] users) {
        modeloUsers.setRowCount(0);
        /* userListPanel.removeAll(); */
        for (String user : users) {
            if (!user.equals(client.getUsername())) {
                modeloUsers.addRow(new Object[]{user, user, ""});
            }
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
        int row = getRowByValue(user);
        if (row != -1) {
            //todo aádir como en la otra columna una celada fantasma para id users
            modeloUsers.setValueAt(String.format(user+"  ||  Unread Message:" + "%-10s" + message, ""), row, 1);

        }
        tablaUsers.revalidate();
        tablaUsers.repaint();
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
        cardLayout.show(panelPrincipal, "ChatSection");
        chatCardLayout.show(chatSection, "Group Section");
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
                client.sendMessageToServer("GROUP " + group + ":" + client.getUsername() + "] " + message);
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

//    public static void main(String[] args) {
//        new App(new Client());
//    }
}
