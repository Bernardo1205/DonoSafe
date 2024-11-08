package Mapa;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaAgregarWaypoint extends JDialog {
    private String textWaypoint;
    private String peligrosidad;

    public VentanaAgregarWaypoint(JFrame parent){
        super(parent,"Agregar waypoint",true);


        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel lable = new JLabel("Crear nueva alerta");
        lable.setFont(new Font("Arial",Font.BOLD,24));
        lable.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lable,BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2,1,5,5));

        DefaultListModel defaultListModel = new DefaultListModel<>();
        String[] str ={ "BAJO", "MEDIO", "ALTO"};
        JList<String> listaPeligrosidad = new JList<>(str);
        JScrollPane scrollPeligrosidad = new JScrollPane(listaPeligrosidad);

       listaPeligrosidad.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
           @Override
           public void valueChanged(ListSelectionEvent e) {
                peligrosidad = listaPeligrosidad.getSelectedValue();
           }
       });
        centerPanel.add(scrollPeligrosidad);


        JTextArea textArea = new JTextArea(5,5);
        JScrollPane jScrollPane = new JScrollPane(textArea);
        centerPanel.add(jScrollPane);

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
        mainPanel.add(centerPanel,BorderLayout.CENTER);
        mainPanel.add(jButton,BorderLayout.SOUTH);
        add(mainPanel);
        setSize(400, 400);
        setResizable(false);
    }

    public String getPeligrosidad() {
        return peligrosidad;
    }

    public String getTextWaypoint() {
        return textWaypoint;
    }
}
