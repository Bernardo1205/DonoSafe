package main.java.com.example.mapa;

import main.java.com.example.client.Client;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MyWaypoint implements Waypoint {
    private String tipoPeligro;
    private String text;
    private Client user;
    private LocalDateTime horaCreacion;
    private String
    private GeoPosition geoPosition;


    @Override
    public GeoPosition getPosition() {
        return geoPosition;
    }
    public MyWaypoint(GeoPosition geoPosition){
       this.geoPosition = geoPosition;
       this.horaCreacion = LocalDateTime.now();
    }

    public String getText() {
        return text;
    }


    public Client getUser() {
        return user;
    }


    public void setUser(Client user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }



    public String getTipoPeligro() {
        return tipoPeligro;
    }

    public void setPeligrosidad(String peligrosidad) {
        this.tipoPeligro = tipoPeligro;
    }

    @Override
    public String toString() {
        return "MyWaypoint{" +
                "text='" + text + '\'' +
                ", horaCreacion=" + horaCreacion +
                ", user=" + user +
                ", geoPosition=" + geoPosition +
                '}';
    }

    public LocalDateTime getHoraCreacion() {
        return horaCreacion;
    }
    public String getStringHoraCreacion(){
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.horaCreacion.format(formatoHora);
    }

}
