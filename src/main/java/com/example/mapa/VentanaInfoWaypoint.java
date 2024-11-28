package main.java.com.example.mapa;

import javax.swing.*;
import java.awt.*;

public class VentanaInfoWaypoint extends JFrame {

    public VentanaInfoWaypoint(Reporte reporte){
        setSize(350, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel jPanel = new JPanel(new BorderLayout());
        JLabel jLabel = new JLabel("Fecha Creacion Waypoint: "+ reporte.getStringHoraCreacion());
        JTextArea jTextArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane(jTextArea);

        jTextArea.setText(reporte.getText());
        jTextArea.setEditable(false);
        jPanel.add(jLabel,BorderLayout.NORTH);
        jPanel.add(jScrollPane,BorderLayout.CENTER);
        add(jPanel);

        setResizable(false);

    }

}
