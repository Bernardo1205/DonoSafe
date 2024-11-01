package Mapa;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaAgregarWaypoint extends JDialog {
    private String textWaypoint;

    public VentanaAgregarWaypoint(JFrame parent){
        super(parent,"Agregar waypoint",true);
        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel lable = new JLabel("Agregar Texto");
        lable.setFont(new Font("Arial",Font.BOLD,24));
        lable.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lable,BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(5,5);
        JScrollPane jScrollPane = new JScrollPane(textArea);
        mainPanel.add(jScrollPane,BorderLayout.CENTER);

        JButton jButton = new JButton("Aceptar");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textWaypoint = textArea.getText().trim();
                if(!textWaypoint.isEmpty()){
                    dispose();
                    textArea.setText("");
                }
                else{
                    JOptionPane.showMessageDialog(VentanaAgregarWaypoint.this,"Introduce el texto para crear Waypoint");
                }

            }
        });

        mainPanel.add(jButton,BorderLayout.SOUTH);
        add(mainPanel);
        setSize(400, 400);
        setResizable(false);
    }

    public String getTextWaypoint() {
        return textWaypoint;
    }
}
