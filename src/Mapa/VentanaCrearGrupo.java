package Mapa;

import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class VentanaCrearGrupo extends JFrame {
    private Localizacion localizacionOrigen;
    private Localizacion localizacionDestino;
    private Date fecha;
    private Date hora;


    public VentanaCrearGrupo(){
        JLabel lable = new JLabel("Crear nuevo grupo");
        lable.setFont(new Font("Arial",Font.BOLD,24));

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridLayout(2,1,5,5));
        JPanel centerPanel_origen = new JPanel(new GridLayout(2,1));
        JPanel centerPanel_destino = new JPanel(new GridLayout(2,1));

        JLabel lableOrigen = new JLabel("Origen: ");
        lable.setFont(new Font("Arial",Font.BOLD,18));
        JLabel lableDestino = new JLabel("Destino: ");
        lable.setFont(new Font("Arial",Font.BOLD,18));

        AdressAutocomplete origen = new AdressAutocomplete();
        AdressAutocomplete destino = new AdressAutocomplete();

        origen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    localizacionOrigen= filtrarInputUser(origen);
                } catch (IOException ex) {

                    throw new RuntimeException(ex);
                }

            }
        });
        destino.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    localizacionDestino = filtrarInputUser(destino);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton jButton = new JButton("Crear");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (localizacionOrigen != null && localizacionDestino != null){
                    Grupo grupo = new Grupo();
                    grupo.setDestino(localizacionDestino);
                    grupo.setOrigen(localizacionOrigen);
                }
                else {
                    JOptionPane.showMessageDialog(VentanaCrearGrupo.this,"Introduce todos los campos correctamente");
                }
            }
        });

        mainPanel.add(jButton,BorderLayout.SOUTH);
        centerPanel_origen.add(lableOrigen);
        centerPanel_origen.add(origen);
        centerPanel_destino.add(lableDestino);
        centerPanel_destino.add(destino);
        centerPanel.add(centerPanel_origen);
        centerPanel.add(centerPanel_destino);


        mainPanel.add(centerPanel,BorderLayout.CENTER);
        mainPanel.add(lable ,BorderLayout.NORTH);
        add(mainPanel);
        setSize(400, 400);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public Localizacion filtrarInputUser(AdressAutocomplete adressAutocomplete) throws IOException {
        String inputAdress = adressAutocomplete.getText();
        if (adressAutocomplete.getMap().containsKey(inputAdress)) {

            Localizacion localizacion = new Localizacion();
            localizacion.setAdress(inputAdress);
            localizacion.setGeoPosition(adressAutocomplete.getMap().get(inputAdress));
            adressAutocomplete.setMap(new HashMap<>());
            return localizacion;

        } else {
            ValidatorLocalizacion validatorLocalizacion = new ValidatorLocalizacion();
            GeoPosition geoPosition = validatorLocalizacion.validateAdress(inputAdress);
            if (geoPosition== null){
                adressAutocomplete.setText("");
                JOptionPane.showMessageDialog(VentanaCrearGrupo.this, "Direccion incorrecta , porfavor introduce otra diferente");
                adressAutocomplete.setMap(new HashMap<>());
                return null;
            }
            Localizacion localizacion = new Localizacion();
            localizacion.setGeoPosition(geoPosition);
            adressAutocomplete.setMap(new HashMap<>());
            return localizacion;

        }
    }

    public static void main(String[] args) {
        VentanaCrearGrupo ventanaCrearGrupo = new VentanaCrearGrupo();
    }
}
