package main.java.com.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

enum TipoChat {
    PRIVADO, GRUPO
}

public class Chat extends JPanel {

    private String chatName;
    private String chatId;

    private JScrollPane scroller;
    private JPanel messagesPanel;
    private TipoChat tipo;

    private App app;

    public Chat(App app, String chatName, TipoChat tipo) {
        this(app, String.copyValueOf(chatName.toCharArray()), chatName, tipo);
    }

    public Chat(App app, String chatId, String chatName, TipoChat tipo) {
        this.app = app;
        this.chatId = chatId;
        this.chatName = chatName;
        this.tipo = tipo;

        setUpGUI();
    }

    private void setUpGUI() {
        /* Método que inicializa la interfaz gráfica */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(app.getMaximumSize());
        setBackground(new Color(255, 255, 255, 200));

        setUpHeader();
        setUpMessages();

        setVisible(true);
    }

    private void setUpHeader() {
        /* Método que inicializa el header del chat */
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setPreferredSize(new Dimension(this.getWidth(), 50));
        header.setBackground(new Color(255, 255, 255, 200));

        /* Botón de volver */
        Box headerBox = Box.createHorizontalBox();
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> {
            clearChat();
            app.showUsersList();
        });
        headerBox.add(backButton);

        /* Nombre del chat */
        JLabel chatNameLabel = new JLabel(this.chatName);
        chatNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerBox.add(Box.createHorizontalGlue());
        headerBox.add(chatNameLabel);
        headerBox.add(Box.createHorizontalGlue());

        /* Botón de opciones */
        JButton optionsButton = new JButton("Opciones");
        optionsButton.addActionListener(e -> {
            /* FIXME Implementar funcionalidad */
            System.out.println("Opciones");
        });
        headerBox.add(optionsButton);
        header.add(headerBox);

        add(header);
    }

    private void setUpMessages() {
        /* Método que inicializa el panel de mensajes */
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(new Color(255, 255, 255, 200));

        scroller = new JScrollPane(messagesPanel);
        scroller.setPreferredSize(new Dimension(400, 500));
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        enableDragToScroll(scroller);

        JTextField messageField = new JTextField();
        messageField.addActionListener(e -> {
            if (!messageField.getText().isEmpty()) {
                switch (tipo) {
                    case PRIVADO -> app.getClient().sendMessageToServer("@" + chatId + " " + messageField.getText());
                    case GRUPO -> app.getClient().sendMessageToServer("GROUP " + chatId + ":" + app.getClient().getUsername() + "]" + messageField.getText());
                }
                addMessage(new Mensaje("You", messageField.getText(), tipo));
                messageField.setText("");
            }
        });

        add(messageField);
        add(scroller);
    }

    public void addMessage(Mensaje mensaje) {
        /* Método que añade un mensaje al chat */
        SwingUtilities.invokeLater(() -> {
            // Verifica si el scroll está en el extremo inferior
            boolean isAtBottom = scroller.getVerticalScrollBar().getValue() +
                    scroller.getVerticalScrollBar().getVisibleAmount() ==
                    scroller.getVerticalScrollBar().getMaximum();
            messagesPanel.add(mensaje);
            messagesPanel.add(Box.createVerticalStrut(5));
            messagesPanel.revalidate();       // Revalidate to refresh the layout
            messagesPanel.repaint();          // Repaint to ensure the new message is displayed
            // Si el scroll estaba abajo, desplazarse automáticamente al final
            if (isAtBottom) {
                SwingUtilities.invokeLater(() -> {
                    scroller.getVerticalScrollBar().setValue(
                            scroller.getVerticalScrollBar().getMaximum());
                });
            }
        });
    }

    private static void enableDragToScroll(JScrollPane scrollPane) {
        JViewport viewport = scrollPane.getViewport();
        // Agregar un MouseAdapter al viewport
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private Point lastDragPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    Point currentPoint = e.getPoint();
                    JViewport viewport = scrollPane.getViewport();

                    // Calcular el desplazamiento
                    int deltaX = lastDragPoint.x - currentPoint.x;
                    int deltaY = lastDragPoint.y - currentPoint.y;

                    // Desplazar el viewport
                    Point viewPosition = viewport.getViewPosition();
                    viewPosition.translate(deltaX, deltaY);
                    viewport.setViewPosition(viewPosition);

                    // Actualizar el punto de referencia
                    lastDragPoint = currentPoint;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastDragPoint = null;
            }
        };

        viewport.addMouseListener(mouseAdapter);
        viewport.addMouseMotionListener(mouseAdapter);
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void clearChat() {
        /* Método que limpia el chat */
        messagesPanel.removeAll();
        revalidate();
        repaint();
    }

//    public static void main(String[] args) {
//        /* Método main para probar el chat */
//        JFrame frame = new JFrame("Chat");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(new Chat("Daniel", TipoChat.PRIVADO));
//        frame.pack();
//        frame.setVisible(true);
//    }
}
