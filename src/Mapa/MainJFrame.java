package Mapa;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class MainJFrame extends JFrame {
    private boolean esperarClick;
    private String textoWaypoint;
    private VentanaAgregarWaypoint ventanaAgregarWaypoint;


    public MainJFrame(){
        JFrame frame = new JFrame("DonoSafe");
        JPanel mainPanel = new JPanel(new BorderLayout());

        Map map = new Map();
        mainPanel.add(map,BorderLayout.CENTER);

        ventanaAgregarWaypoint = new VentanaAgregarWaypoint(this);


        JButton añadirWaypoint = new JButton("+");

        añadirWaypoint.addActionListener(e -> {

            ventanaAgregarWaypoint.setVisible(true);
            textoWaypoint = ventanaAgregarWaypoint.getTextWaypoint();

            if(textoWaypoint!=null&&!textoWaypoint.isEmpty()){
                esperarClick=true;
                JOptionPane.showMessageDialog(this,"Haz click en el mapa para colocar el waypoint");

            }

        });
        JPanel panelBotones = new JPanel(new GridLayout(1,4));
        panelBotones.add(añadirWaypoint);


        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (esperarClick){

                    Point point = e.getPoint();
                    GeoPosition geoPosition = map.convertPointToGeoPosition(point);
                    MyWaypoint myWaypoint = ponerwaypoint(geoPosition,textoWaypoint);
                    myWaypoint.setPeligrosidad(ventanaAgregarWaypoint.getPeligrosidad());

                    Set setMywaypoints = map.getSetWaipoints();
                    setMywaypoints.add(myWaypoint);
                    map.añadirWaypoint(setMywaypoints);

                    esperarClick = false;

                }

            }
        });

        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point2D point2D =null;
                for (MyWaypoint waypoint:map.getSetWaipoints()){
                    point2D = map.getTileFactory().geoToPixel(waypoint.getPosition(),map.getZoom());
                    Rectangle rect = map.getViewportBounds();
                    Point converted_pont2D = new Point((int) point2D.getX()-rect.x,
                            (int)point2D.getY() -rect.y);
                    if (converted_pont2D.distance(e.getPoint())<10){
                        VentanaInfoWaypoint ventanaInfoWaypoint = new VentanaInfoWaypoint(waypoint);
                        ventanaInfoWaypoint.setVisible(true);
                    }


                }
            }
        });

        mainPanel.add(new AdressAutocomplete(),BorderLayout.NORTH);

        mainPanel.add(panelBotones,BorderLayout.SOUTH);
        frame.add(mainPanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public MyWaypoint ponerwaypoint(GeoPosition geoPosition, String text){
        MyWaypoint waypoint = new MyWaypoint(geoPosition);
        waypoint.setText(text);
        return waypoint;

    }

    public static void main(String[] args) {
        MainJFrame mainJFrame = new MainJFrame();
    }
}
