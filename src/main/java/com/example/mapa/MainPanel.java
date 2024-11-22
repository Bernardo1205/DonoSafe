package main.java.com.example.mapa;

import main.java.com.example.client.Client;
import main.java.com.example.client.ServerConnection;
import org.jxmapviewer.viewer.GeoPosition;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.function.Consumer;

public class MainPanel extends JPanel {

    private boolean esperarClick;
    private String textoWaypoint;
    private String peligrosidad;
    private Map map;
    public Client client;

    private Consumer<MyWaypoint> onWaypointAdded;/* Listener para pasar waypoints a clase app cuando se creen */

    public MainPanel(JFrame jFrame,Client client){
        this.client = client;
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
                    myWaypoint.setUser(client);

                    Set setMywaypoints = map.getSetWaipoints();
                    setMywaypoints.add(myWaypoint);
                    map.añadirWaypoint(setMywaypoints);

                    if (onWaypointAdded != null) {
                        onWaypointAdded.accept(myWaypoint);
                    }

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

    }
    public void setOnWaypointAdded(Consumer<MyWaypoint> listener) {
        this.onWaypointAdded = listener;
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
        this.textoWaypoint = textoWaypoint;}

    public MyWaypoint ponerwaypoint(GeoPosition geoPosition, String text){
        MyWaypoint waypoint = new MyWaypoint(geoPosition);
        waypoint.setText(text);
        return waypoint;
    }




}
