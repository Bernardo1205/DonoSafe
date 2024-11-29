package main.java.com.example.mapa;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class VentanaInfoWaypoint extends JFrame {

    public VentanaInfoWaypoint(Reporte reporte) {
        setTitle("Información del Waypoint");
        setSize(350, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);



        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font textFont = new Font("Arial", Font.PLAIN, 12);
        Color backgroundColor = new Color(240, 248, 255);
        Color borderColor = new Color(0, 120, 215);
        Color labelColor = Color.DARK_GRAY;


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(borderColor, 2), "Detalles del Waypoint");
        title.setTitleJustification(TitledBorder.CENTER);
        title.setTitleFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.setBorder(title);


        JLabel jLabel = new JLabel("Fecha Creación Waypoint: " + reporte.getStringHoraCreacion());
        jLabel.setFont(labelFont);
        jLabel.setForeground(labelColor);
        mainPanel.add(jLabel, BorderLayout.NORTH);


        JTextArea jTextArea = new JTextArea();
        jTextArea.setText(reporte.getText());
        jTextArea.setFont(textFont);
        jTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        mainPanel.add(jScrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Cerrar");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(borderColor);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        setResizable(false);
        add(mainPanel);


    }
}
