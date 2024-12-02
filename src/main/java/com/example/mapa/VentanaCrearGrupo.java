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
        Color backgroundColor = new Color(240, 248, 255); // Azul claro
        Color buttonColor = new Color(0, 120, 215); // Azul oscuro
        Color buttonTextColor = Color.WHITE;


        // Panel principal
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);


        // Título
        JLabel titleLabel = new JLabel("Crear Nuevo Grupo");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);


        // Panel central
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.setBackground(backgroundColor);


        // Origen
        JPanel origenPanel = new JPanel(new FlowLayout());
        origenPanel.setBackground(backgroundColor);
        JLabel origenLabel = new JLabel("Origen:");
        origenLabel.setFont(new Font("Arial", Font.BOLD, 14));
        origenPanel.add(origenLabel, BorderLayout.NORTH);
        AdressAutocomplete origenField = new AdressAutocomplete();
        origenField.setPreferredSize(new Dimension(200, 30));
        origenPanel.add(origenField, BorderLayout.CENTER);
        centerPanel.add(origenPanel);


        // Destino
        JPanel destinoPanel = new JPanel(new FlowLayout());
        destinoPanel.setBackground(backgroundColor);
        JLabel destinoLabel = new JLabel("Destino:");
        destinoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        destinoPanel.add(destinoLabel, BorderLayout.NORTH);
        AdressAutocomplete destinoField = new AdressAutocomplete();
        destinoField.setPreferredSize(new Dimension(200, 30));
        destinoPanel.add(destinoField, BorderLayout.CENTER);
        centerPanel.add(destinoPanel);


        // Hora
        JPanel horaPanel = new JPanel(new FlowLayout());
        horaPanel.setBackground(backgroundColor);
        JLabel horaLabel = new JLabel("Hora:");
        horaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        horaPanel.add(horaLabel, BorderLayout.NORTH);


        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d:00", i);
        }
        JComboBox<String> horaComboBox = new JComboBox<>(hours);
        horaComboBox.setPreferredSize(new Dimension(200, 30));
        horaComboBox.addActionListener(e -> {
            hora = new Date();
            hora.setHours(horaComboBox.getSelectedIndex());
        });
        horaPanel.add(horaComboBox, BorderLayout.CENTER);
        centerPanel.add(horaPanel);


        mainPanel.add(centerPanel, BorderLayout.CENTER);



        JButton crearButton = new JButton("Crear");
        crearButton.setFont(new Font("Arial", Font.BOLD, 14));
        crearButton.setBackground(buttonColor);
        crearButton.setForeground(buttonTextColor);
        crearButton.setFocusPainted(false);
        crearButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));


        crearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String textoOrigen = origenField.getText();
                String textoDestino = destinoField.getText();


                try {
                    localizacionOrigen = filtrarInputUser(origenField);
                    localizacionDestino = filtrarInputUser(destinoField);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }


                if (localizacionOrigen != null && localizacionDestino != null && horaComboBox.getSelectedItem() != null) {
                    Grupo grupo = new Grupo();
                    grupo.setDestino(localizacionDestino);
                    grupo.setOrigen(localizacionOrigen);
                    String groupInfo = String.join(";", textoOrigen, textoDestino, (String) horaComboBox.getSelectedItem());
                    client.sendMessageToServer("[NEW_GROUP] " + client.getUsername() + "|" + groupInfo);
                    origenField.setText("");
                    destinoField.setText("");
                } else {
                    JOptionPane.showMessageDialog(VentanaCrearGrupo.this, "Introduce todos los campos correctamente", "Error", JOptionPane.ERROR_MESSAGE);
                }
                //origen.setText("");
                //destino.setText("");
            }
        });


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(crearButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
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
