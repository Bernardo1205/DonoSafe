package Mapa;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MyWaypoint implements Waypoint {
    private String text;
    private LocalDateTime date;
    private User user;
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

    public User getUser() {
        return user;
    }


    public void setUser(User user) {
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

    @Override
    public String toString() {
        return "MyWaypoint{" +
                "text='" + text + '\'' +
                ", date=" + date +
                ", user=" + user +
                ", geoPosition=" + geoPosition +
                '}';
    }
    public void infoFrame(){
        JFrame jFrame = new JFrame();




    }
}
