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
    private String peligrosidad;
    private String text;
    private LocalDateTime date;
    private Client user;
    private GeoPosition geoPosition;


    @Override
    public GeoPosition getPosition() {
        return geoPosition;
    }
    public MyWaypoint(GeoPosition geoPosition){
       this.date = LocalDateTime.now();
       this.geoPosition = geoPosition;
    }

    public String getText() {
        return text;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public LocalDateTime getDate() {
        return date;
    }
    public String getStringDate(){
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.date.format(formatoHora);
    }

    public String getPeligrosidad() {
        return peligrosidad;
    }

    public void setPeligrosidad(String peligrosidad) {
        this.peligrosidad = peligrosidad;
    }

    @Override
    public String toString() {
        return "MyWaypoint{" +
                "text='" + text + '\'' +
                ", date=" + date +
                ", user=" + user +
                ", geoPosition=" + geoPosition +
                '}';
    }
}
