package Mapa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String name;
    private String user_name;
    private String sexo;
    private String id;
    private String mail;
    private String password;
    private ArrayList<String> review;
    private LocalDateTime reg_date;
    private String decrp;
    private Set<MyWaypoint> list_waypoints;

    public User(){
        this.reg_date = LocalDateTime.now();
        this.review = new ArrayList<>();
        this.list_waypoints = new HashSet<>();
    }
    private void addReview(String review){
        this.review.add(review);
    }
    private void  addWaypint(MyWaypoint waypoint){
        this.list_waypoints.add(waypoint);
    }

    public ArrayList<String> getReview() {
        return review;
    }

    public Set<MyWaypoint> getList_waypoints() {
        return list_waypoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getReg_date() {
        return reg_date;
    }

    public void setReg_date(LocalDateTime reg_date) {
        this.reg_date = reg_date;
    }

    public String getDecrp() {
        return decrp;
    }

    public void setDecrp(String decrp) {
        this.decrp = decrp;
    }
    /* todo crear funcion con Petterns para verificar que el mail esta bien
        estructurado( se puede hacer esta funcion en el loggin layout) */

}
