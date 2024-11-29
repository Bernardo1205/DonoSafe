package main.java.com.example.mapa;

import main.java.com.example.client.Client;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Alerta emitida por los usuarios para avisar de un posible peligro
 */
public class Reporte implements Waypoint {
    private String tipoPeligro;
    private Client user;
    private LocalDateTime horaCreacion;
    private String descripcion;

    private LocalDateTime horaDelReporte;
    private GeoPosition geoPosition;

    @Override
    public GeoPosition getPosition() {
        return geoPosition;
    }

    public Reporte(GeoPosition geoPosition){
       this.geoPosition = geoPosition;
       this.horaCreacion = LocalDateTime.now();
    }


    public void setUser(Client user) {
        this.user = user;
    }

    public Client getUser() {
        return user;
    }

    public void setDate(LocalDateTime date) {
        this.horaDelReporte = date;
    }

    public LocalDateTime getDate() {return horaDelReporte;}

    public void setPeligrosidad(String peligrosidad) {
        this.tipoPeligro = peligrosidad;
    }

    public String getTipoPeligro() {
        return this.tipoPeligro;
    }

    public LocalDateTime getHoraCreacion() {
        return horaCreacion;
    }

    public String getStringHoraCreacion(){
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.horaCreacion.format(formatoHora);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getHoraDelReporte() {
        return horaDelReporte;
    }

    public void setDescripcion(String text) {
        this.descripcion = text;
    }
    @Override
    public String toString() {
        return "Reporte{" +
                ", horaCreacion=" + horaCreacion +
                ", user=" + user +
                ", geoPosition=" + geoPosition +
                '}';
    }
}