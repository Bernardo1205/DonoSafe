package main.java.com.example.mapa;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.concurrent.Flow;

public class VentanaAgregarWaypoint extends JDialog {
    private String textWaypoint;
    private String peligrosidad;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;

    public VentanaAgregarWaypoint(JFrame parent){
        super(parent,null,true);
        /* Colocar la ventana emergente en el centro de la ventana principal */
        setLocation(parent.getX()+WIDTH/2, parent.getY()+HEIGHT/3);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel lable = new JLabel("Crear nueva alerta");
        lable.setFont(new Font("Arial",Font.BOLD,24));
        lable.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lable,BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        DefaultListModel defaultListModel = new DefaultListModel<>();
        String[] str ={ "BAJO", "MEDIO", "ALTO"};
        JList<String> listaPeligrosidad = new JList<>(str);
        listaPeligrosidad.setVisibleRowCount(1);
        listaPeligrosidad.setPreferredSize(new Dimension(200, 30));
        listaPeligrosidad.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        JScrollPane scrollPane = new JScrollPane(listaPeligrosidad);
        scrollPane.setBorder(new TitledBorder("Tipo de peligrosidad"));

        listaPeligrosidad.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER); // Centrar texto
                // Ajustar ancho dinámico
                int totalWidth = list.getWidth();
                int itemWidth = totalWidth / list.getModel().getSize();
                label.setPreferredSize(new Dimension(itemWidth, list.getFixedCellHeight() > 0 ? list.getFixedCellHeight() : 30));
                return label;
            }
        });

        listaPeligrosidad.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                peligrosidad = listaPeligrosidad.getSelectedValue();
            }
        });
        centerPanel.add(scrollPane, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(5,5);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane jScrollPane = new JScrollPane(textArea);
        jScrollPane.setBorder(new TitledBorder("Descripción de lo ocurrido"));
        centerPanel.add(jScrollPane, BorderLayout.CENTER);

        JButton jButton = new JButton("Aceptar");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textWaypoint = textArea.getText().trim();
                if(!textWaypoint.isEmpty()){
                    dispose();
                    textArea.setText("");
                } else {
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
