package main.java.com.example.mapa;

import javax.swing.*;
import java.awt.*;

public class VentanaInfoWaypoint extends JFrame {

    public VentanaInfoWaypoint(Reporte waypoint){
        JPanel jPanel = new JPanel(new BorderLayout());
        JLabel jLabel = new JLabel("Fecha Creacion Waypoint: "+ waypoint.getStringDate());
        JTextArea jTextArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane(jTextArea);

        jTextArea.setText(waypoint.getDescripcion());
        jTextArea.setEditable(false);
        jPanel.add(jLabel,BorderLayout.NORTH);
        jPanel.add(jScrollPane,BorderLayout.CENTER);
        add(jPanel);
        setSize(350, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

    }

}
