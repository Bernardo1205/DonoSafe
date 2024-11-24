package Mapa;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MyWaypoint implements Waypoint {
    public String descripcion;
    public LocalDateTime fecha_creacion;
    public User user;
    public LocalDateTime hora_reporte;
    public GeoPosition geoPosition;
    public String tipoPeligro;

    public void setFecha_creacion(LocalDateTime fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public void setHora_reporte(LocalDateTime hora_reporte) {
        this.hora_reporte = hora_reporte;
    }

    public void setGeoPosition(GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }

    public void setTipoPeligro(String tipoPeligro) {
        this.tipoPeligro = tipoPeligro;
    }

    public LocalDateTime getHora_reporte() {
        return hora_reporte;
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    public String getTipoPeligro() {
        return tipoPeligro;
    }

    public MyWaypoint(GeoPosition geoPosition){
        this.fecha_creacion = LocalDateTime.now();
        this.geoPosition = geoPosition;
    }
    @Override
    public GeoPosition getPosition() {
        return geoPosition;
    }
    
    public String getDescripcion() {
        return descripcion;
    }

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public void setDescripcion(String text) {
        this.descripcion = text;
    }

    public LocalDateTime getFecha_creacion() {
        return fecha_creacion;
    }
    
    public String getStringDate(){
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.fecha_creacion.format(formatoHora);
    }

    @Override
    public String toString() {
        return "MyWaypoint{" +
                "text='" + descripcion + '\'' +
                ", date=" + fecha_creacion +
                ", user=" + user +
                ", geoPosition=" + geoPosition +
                '}';
    }
    public void infoFrame(){
        JFrame jFrame = new JFrame();




    }
}
