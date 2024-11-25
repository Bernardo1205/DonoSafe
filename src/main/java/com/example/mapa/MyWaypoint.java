package main.java.com.example.mapa;

import main.java.com.example.client.Client;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyWaypoint implements Waypoint {
    private String tipoPeligro;
    private String descripcion;
    private LocalDateTime hora_reporte;
    private LocalDateTime hora_creacion;
    private Client user;
    private GeoPosition geoPosition;

    public void setHora_creacion(LocalDateTime hora_creacion) {
        this.hora_creacion = hora_creacion;
    }

    public void setGeoPosition(GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }

    public LocalDateTime getHora_creacion() {
        return hora_creacion;
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }




    @Override
    public GeoPosition getPosition() {
        return geoPosition;
    }
    public MyWaypoint(GeoPosition geoPosition){
       this.hora_reporte = LocalDateTime.now();
       this.geoPosition = geoPosition;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setHora_reporte(LocalDateTime hora_reporte) {
        this.hora_reporte = hora_reporte;
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

    public LocalDateTime getHora_reporte() {
        return hora_reporte;
    }
    public String getStringDate(){
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.hora_reporte.format(formatoHora);
    }
    public void setTipoPeligro(String tipoPeligro) {
        this.tipoPeligro = tipoPeligro;
    }

    public String getTipoPeligro() {
        return tipoPeligro;
    }
    public String getPeligrosidad() {
        return tipoPeligro;
    }

    public void setPeligrosidad(String peligrosidad) {
        this.tipoPeligro = peligrosidad;
    }

    @Override
    public String toString() {
        return "MyWaypoint{" +
                "text='" + descripcion + '\'' +
                ", date=" + hora_reporte +
                ", user=" + user +
                ", geoPosition=" + geoPosition +
                '}';
    }
}
