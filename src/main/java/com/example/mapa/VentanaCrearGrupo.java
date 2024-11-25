package main.java.com.example.mapa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import main.java.com.example.client.Client;
import org.jxmapviewer.viewer.GeoPosition;

public class VentanaCrearGrupo extends JPanel {
    private Localizacion localizacionOrigen;
    private Localizacion localizacionDestino;
    private Date fecha;
    private Date hora;


    public VentanaCrearGrupo(Client client){
        JLabel lable = new JLabel("Crear nuevo grupo");
        lable.setFont(new Font("Arial",Font.BOLD,20));
        lable.setVerticalAlignment(SwingConstants.CENTER);
        lable.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridLayout(3,1,0,5));
        JPanel centerPanel_origen = new JPanel(new GridLayout(2,1));
        JPanel centerPanel_destino = new JPanel(new GridLayout(2,1));

        JLabel lableOrigen = new JLabel("Origen ");
        lableOrigen.setFont(new Font("Arial",Font.BOLD,14));
        lableOrigen.setHorizontalAlignment(SwingConstants.CENTER);
        lableOrigen.setVerticalAlignment(SwingConstants.CENTER);

        JLabel lableDestino = new JLabel("Destino ");
        lableDestino.setFont(new Font("Arial",Font.BOLD,14));
        lableDestino.setHorizontalAlignment(SwingConstants.CENTER);
        lableDestino.setVerticalAlignment(SwingConstants.CENTER);


        JPanel flowLayoutOrigen = new JPanel(new FlowLayout(FlowLayout.CENTER));
        AdressAutocomplete origen = new AdressAutocomplete();
        origen.setPreferredSize(new Dimension(200,25));
        flowLayoutOrigen.add(origen);

        JPanel flowLayoutDestino = new JPanel(new FlowLayout(FlowLayout.CENTER));
        AdressAutocomplete destino = new AdressAutocomplete();
        destino.setPreferredSize(new Dimension(200,25));
        flowLayoutDestino.add(destino);

        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d:00", i);
        }
        JComboBox groupHour = new JComboBox(hours);
        groupHour.setPreferredSize(new Dimension(200,25));
        JPanel groupHourPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        groupHourPanel.add(groupHour);

        groupHour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hora = new Date();
                hora.setHours(groupHour.getSelectedIndex());
            }
        });


        JButton jButton = new JButton("Crear");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textoOrigen = origen.getText();
                String textoDestino = destino.getText();

                try {
                    localizacionOrigen= filtrarInputUser(origen);
                    localizacionDestino = filtrarInputUser(destino);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                if (localizacionOrigen!= null && localizacionDestino != null &&  groupHour.getSelectedItem() != null){

                        Grupo grupo = new Grupo();
                        grupo.setDestino(localizacionDestino);
                        grupo.setOrigen(localizacionOrigen);
                        String groupInfo = String.join(";", origen.getText(), destino.getText(), (String) groupHour.getSelectedItem());
                        client.sendMessageToServer("[NEW_GROUP] " + client.getUsername() + "|" + groupInfo);

                        System.out.println(groupInfo);

                }
                else {
                    JOptionPane.showMessageDialog(VentanaCrearGrupo.this,"Introduce todos los campos correctamente");

                }
//                origen.setText("");
//                destino.setText("");
            }
        });


        centerPanel_origen.add(lableOrigen);
        centerPanel_origen.add(flowLayoutOrigen);

        centerPanel_destino.add(lableDestino);
        centerPanel_destino.add(flowLayoutDestino);

        centerPanel.add(centerPanel_origen);
        centerPanel.add(centerPanel_destino);
        centerPanel.add(groupHourPanel);


        mainPanel.add(jButton,BorderLayout.SOUTH);
        mainPanel.add(centerPanel,BorderLayout.CENTER);
        mainPanel.add(lable ,BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

    }

    public Localizacion filtrarInputUser(AdressAutocomplete adressAutocomplete) throws IOException {
        String inputAdress = adressAutocomplete.getText();
        if (adressAutocomplete.getMap().containsKey(inputAdress)) {

            Localizacion localizacion = new Localizacion();
            localizacion.setAdress(inputAdress);
            localizacion.setGeoPosition(adressAutocomplete.getMap().get(inputAdress));
            adressAutocomplete.setMap(new HashMap<>());
            System.out.println("Esta en dic");
            return localizacion;

        } else {
            ValidatorLocalizacion validatorLocalizacion = new ValidatorLocalizacion();
            GeoPosition geoPosition = validatorLocalizacion.validateAdress(inputAdress);
            if (geoPosition== null){
                adressAutocomplete.setText("");
                JOptionPane.showMessageDialog(VentanaCrearGrupo.this, "Direccion incorrecta , porfavor introduce otra diferente");
                adressAutocomplete.setMap(new HashMap<>());
                System.out.println("No esta en dic y es null");
                return null;
            }
            Localizacion localizacion = new Localizacion();
            localizacion.setAdress(inputAdress);
            localizacion.setGeoPosition(geoPosition);
            adressAutocomplete.setMap(new HashMap<>());
            System.out.println("No esta en dic y no es null");
            return localizacion;

        }
    }
}
