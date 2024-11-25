package main.java.com.example.mapa;

import main.java.com.example.client.Client;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reporte implements Waypoint {

    private String peligrosidad;
    private String descripcion;
    private LocalDateTime date;
    private Client user;
    private GeoPosition geoPosition;


    @Override
    public GeoPosition getPosition() {
        return geoPosition;
    }

    public Reporte(GeoPosition geoPosition){
       this.date = LocalDateTime.now();
       this.geoPosition = geoPosition;
    }

    public String getDescripcion() {
        return descripcion;
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

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
        return "Reporte{" +
                "text='" + descripcion + '\'' +
                ", date=" + date +
                ", user=" + user +
                ", geoPosition=" + geoPosition +
                '}';
    }
}
