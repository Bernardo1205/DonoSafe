package main.java.com.example.client;

import main.java.com.example.mapa.MainPanel;


import main.java.com.example.mapa.Reporte;

import main.java.com.example.mapa.VentanaAgregarWaypoint;
import main.java.com.example.mapa.VentanaCrearGrupo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
        setSize(800, 600);
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


        JButton bAñadirAlerta = new JButton("Crear Alerta");
        bAñadirAlerta.addActionListener(e -> agregarAlerta());
        bAñadirAlerta.setBounds(10, 10, 120, 40);
        bAñadirAlerta.setBackground(new Color(255, 100, 100));
        bAñadirAlerta.setForeground(Color.WHITE);
        bAñadirAlerta.setFont(new Font("Arial", Font.BOLD, 14));
        bAñadirAlerta.setFocusPainted(false);


        layeredPane.add(panelMapa, Integer.valueOf(0));
        layeredPane.add(bAñadirAlerta, Integer.valueOf(1));


        mapPanel.add(layeredPane);


        return mapPanel;
    }


    private void waypointAdded(Reporte reporte){
        String s = String.join(",", String.valueOf(reporte.getPosition().getLongitude()), String.valueOf(reporte.getPosition().getLatitude()), ventanaAgregarWaypoint.getPeligrosidad(), reporte.getHoraCreacion().toString(), reporte.getUser().getUsername(), reporte.getDescripcion());
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
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Espaciado entre paneles
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Márgenes externos

        // Panel para la lista de grupos
        groupsListPanel = new JPanel();
        groupsListPanel.setLayout(new BoxLayout(groupsListPanel, BoxLayout.Y_AXIS));
        groupsListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Vector<String> cabeceraGrupos = new Vector<>(Arrays.asList("ID", "Origen", "Destino", "Hora salida"));
        modeloGrupos = new DefaultTableModel(new Vector<>(), cabeceraGrupos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tablaGrupos = new JTable(modeloGrupos);
        tablaGrupos.setFillsViewportHeight(true); // Extiende la tabla para ocupar el panel
        tablaGrupos.setRowHeight(25); // Altura de las filas
        tablaGrupos.setFont(new Font("Arial", Font.PLAIN, 14)); // Fuente

        // Configurar el renderer para la tabla
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel jLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                jLabel.setHorizontalAlignment(SwingConstants.CENTER);
                jLabel.setToolTipText(column != 3 ? value.toString() : null);
                return jLabel;
            }
        };

        tablaGrupos.setDefaultRenderer(Object.class, defaultRenderer);

        // Estilo de cabecera de la tabla
        JTableHeader header = tablaGrupos.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(70, 130, 180)); // Azul suave
        header.setForeground(Color.WHITE);

        // Ocultar la columna de ID
        tablaGrupos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaGrupos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaGrupos.getColumnModel().getColumn(0).setWidth(0);

        // Scroll para la tabla
        JScrollPane groupsListScroll = new JScrollPane(tablaGrupos);
        groupsListScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Añadir evento para unirse a grupo
        tablaGrupos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaGrupos.rowAtPoint(e.getPoint());
                if (fila != -1) {
                    String idGrupo = (String) modeloGrupos.getValueAt(fila, 0);
                    client.joinGroupChat(idGrupo);
                    JOptionPane.showMessageDialog(App.this, "Te has unido al grupo con destino: " + modeloGrupos.getValueAt(fila, 2));
                }
            }
        });

        groupsListPanel.add(groupsListScroll);
        centerPanel.add(groupsListPanel);
        centerPanel.add(new VentanaCrearGrupo(client)); // Panel para crear grupo

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

        // Configurar el panel de cabecera
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180)); // Color de fondo de cabecera
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        groupTalkingTo = new JLabel("Grupo desde "+groupInfo[0] + " hasta " + groupInfo[1]);
        groupTalkingTo.setForeground(Color.WHITE);
        groupTalkingTo.setFont(new Font("Arial", Font.BOLD, 16));

        // Botón de salida con estilo
        JButton exitChatButton = new JButton("\u2190"); // Flecha hacia atrás
        exitChatButton.setFocusPainted(false);
        exitChatButton.setBackground(new Color(220, 53, 69)); // Rojo para botón de salida
        exitChatButton.setForeground(Color.WHITE);
        exitChatButton.addActionListener(e -> {
            grupoActual = "";
            showUsersList();
        });

        headerPanel.add(exitChatButton, BorderLayout.WEST);
        headerPanel.add(groupTalkingTo, BorderLayout.CENTER);
        groupChatPanel.add(headerPanel, BorderLayout.NORTH);

        // Configurar panel de mensajes
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollMessages = new JScrollPane(messagesPanel);
        scrollMessages.setBorder(null); // Sin borde adicional
        groupChatPanel.add(scrollMessages, BorderLayout.CENTER);

        // Campo de texto con estilo y placeholder
        JTextField messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                messageField.getBorder(),
                new EmptyBorder(8, 8, 8, 8) // Espaciado interno
        ));

        messageField.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                // Formatear el mensaje correctamente sin corchetes
                String formattedMessage = "GROUP " + group + ": " + client.getUsername() + ": " + message;

                // Enviar el mensaje al servidor
                client.sendMessageToServer(formattedMessage);

                // Agregar el mensaje solo si no será recibido de nuevo
                addGroupMessage("You: " + message);

                messageField.setText("");
            }
        });


        groupChatPanel.add(messageField, BorderLayout.SOUTH);

        // Actualizar la interfaz
        groupChatPanel.revalidate();
        groupChatPanel.repaint();
    }

    public void openPrivateChat(String targetUser) {
        chatPanel.removeAll();

        // Configurar panel de cabecera
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(60, 90, 150)); // Color de fondo personalizado
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Añadir espacio interno

        userTalkingTo = new JLabel(targetUser);
        userTalkingTo.setForeground(Color.WHITE); // Color del texto
        userTalkingTo.setFont(new Font("Arial", Font.BOLD, 16)); // Cambiar la fuente

        // Botón de salida con icono
        JButton exitChatButton = new JButton("\u2190"); // Flecha hacia atrás
        exitChatButton.setFocusPainted(false);
        exitChatButton.setBackground(new Color(220, 53, 69)); // Color rojo para salida
        exitChatButton.setForeground(Color.WHITE);
        exitChatButton.addActionListener(e -> showUsersList());

        headerPanel.add(exitChatButton, BorderLayout.WEST);
        headerPanel.add(userTalkingTo, BorderLayout.CENTER);

        chatPanel.add(headerPanel, BorderLayout.NORTH);

        // Configurar panel de mensajes con scroll
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Espacio entre mensajes
        scrollMessages = new JScrollPane(messagesPanel);
        scrollMessages.setBorder(null); // Sin borde para un look más limpio
        chatPanel.add(scrollMessages, BorderLayout.CENTER);

        // Campo de texto con placeholder
        JTextField messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                messageField.getBorder(),
                new EmptyBorder(8, 8, 8, 8) // Padding interno
        ));

        // Escuchar y enviar mensajes
        messageField.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                client.sendMessageToServer("@" + targetUser + " " + message);
                addPrivateMessage("You: " + message);
                messageField.setText("");
            }
        });

        chatPanel.add(messageField, BorderLayout.SOUTH);

        // Actualizar panel
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