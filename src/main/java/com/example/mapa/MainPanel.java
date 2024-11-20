package main.java.com.example.mapa;

import org.jxmapviewer.viewer.GeoPosition;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Set;

public class MainPanel extends JPanel {

    private boolean esperarClick;
    private String textoWaypoint;
<<<<<<<< HEAD:src/main/java/com/example/mapa/MainPanel.java
    private String peligrosidad;
    private Map map;
========
    private VentanaAgregarWaypoint ventanaAgregarWaypoint;

    public MainJFrame(){
        JPanel mainPanel = new JPanel(new BorderLayout());

        Map map = new Map();
        mainPanel.add(map,BorderLayout.CENTER);

        ventanaAgregarWaypoint = new VentanaAgregarWaypoint(this);
>>>>>>>> refs/heads/server-client:src/main/java/com/example/mapa/MainJFrame.java


    public MainPanel(JFrame jFrame){
        setLayout(new BorderLayout());

        map = new Map();
        add(map,BorderLayout.CENTER);

        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEsperarClick()){

                    Point point = e.getPoint();
                    GeoPosition geoPosition = map.convertPointToGeoPosition(point);
                    MyWaypoint myWaypoint = ponerwaypoint(geoPosition,textoWaypoint);
                    myWaypoint.setPeligrosidad(getPeligrosidad());

                    Set setMywaypoints = map.getSetWaipoints();
                    setMywaypoints.add(myWaypoint);
                    map.añadirWaypoint(setMywaypoints);

                    setEsperarClick(false);


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

<<<<<<<< HEAD:src/main/java/com/example/mapa/MainPanel.java
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public String getPeligrosidad() {
        return peligrosidad;
    }

    public void setPeligrosidad(String peligrosidad) {
        this.peligrosidad = peligrosidad;
    }

    public boolean isEsperarClick() {
        return esperarClick;
    }

    public void setEsperarClick(boolean esperarClick) {
        this.esperarClick = esperarClick;
    }

    public String getTextoWaypoint() {
        return textoWaypoint;
    }

    public void setTextoWaypoint(String textoWaypoint) {
        this.textoWaypoint = textoWaypoint;
========
        mainPanel.add(panelBotones,BorderLayout.SOUTH);
        add(mainPanel);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
>>>>>>>> refs/heads/server-client:src/main/java/com/example/mapa/MainJFrame.java
    }

    public MyWaypoint ponerwaypoint(GeoPosition geoPosition, String text){
        MyWaypoint waypoint = new MyWaypoint(geoPosition);
        waypoint.setText(text);
        return waypoint;

    }


}
