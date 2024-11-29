package main.java.com.example.mapa;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class VentanaAgregarWaypoint extends JDialog {
    private String textWaypoint;
    private String peligrosidad;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;


    public VentanaAgregarWaypoint(JFrame parent) {
        super(parent, null, true);
        // Centrar la ventana emergente en la ventana principal
        setLocation(parent.getX() + WIDTH / 2, parent.getY() + HEIGHT / 3);



        Color backgroundColor = new Color(240, 248, 255); // Azul claro
        Color buttonColor = new Color(0, 120, 215); // Azul intenso
        Color buttonTextColor = Color.WHITE;



        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);



        JLabel titleLabel = new JLabel("Crear Nueva Alerta");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);


        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(backgroundColor);


        String[] nivelesPeligrosidad = {"BAJO", "MEDIO", "ALTO"};
        JList<String> listaPeligrosidad = new JList<>(nivelesPeligrosidad);
        listaPeligrosidad.setFont(new Font("Arial", Font.PLAIN, 14));
        listaPeligrosidad.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPeligrosidad.setVisibleRowCount(3);
        listaPeligrosidad.setFixedCellHeight(30);


        listaPeligrosidad.setBackground(Color.WHITE);
        listaPeligrosidad.setForeground(Color.BLACK);
        listaPeligrosidad.setSelectionBackground(new Color(173, 216, 230));
        listaPeligrosidad.setSelectionForeground(Color.BLACK);


        listaPeligrosidad.addListSelectionListener(e -> peligrosidad = listaPeligrosidad.getSelectedValue());


        JScrollPane scrollPane = new JScrollPane(listaPeligrosidad);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                "Tipo de Peligrosidad",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.BLACK));
        centerPanel.add(scrollPane, BorderLayout.NORTH);


        JTextArea textArea = new JTextArea(5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));


        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150)),
                "Descripción de lo Ocurrido",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.BLACK));
        centerPanel.add(textScrollPane, BorderLayout.CENTER);


        mainPanel.add(centerPanel, BorderLayout.CENTER);


        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.setFont(new Font("Arial", Font.BOLD, 14));
        aceptarButton.setBackground(buttonColor);
        aceptarButton.setForeground(buttonTextColor);
        aceptarButton.setFocusPainted(false);
        aceptarButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textWaypoint = textArea.getText().trim();
                if (textWaypoint.isEmpty()) {
                    JOptionPane.showMessageDialog(VentanaAgregarWaypoint.this,
                            "Introduce el texto para crear Waypoint",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    dispose();
                }
            }
        });


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(aceptarButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        setTitle("Agregar Waypoint");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
    }


    public String getPeligrosidad() {
        return peligrosidad;
    }


    public String getTextWaypoint() {
        return textWaypoint;
    }


}
