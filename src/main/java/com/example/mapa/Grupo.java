package main.java.com.example.mapa;

import main.java.com.example.mapa.User;

import java.util.HashSet;
import java.util.Set;

public class Grupo {
    private HashSet<User> groupUsers;
    private Localizacion origen;
    private Localizacion destino;
    private User creatorGroup;

    public Grupo(){
        this.groupUsers = new HashSet<>();
    }

    public Localizacion getOrigen() {
        return origen;
    }

    public void setOrigen(Localizacion origen) {
        this.origen = origen;
    }

    public Localizacion getDestino() {
        return destino;
    }

    public void setDestino(Localizacion destino) {
        this.destino = destino;
    }

    public void addUser(User user){
        groupUsers.add(user);
    }

    public User getCreatorGroup() {
        return creatorGroup;
    }

    public void setCreatorGroup(User creatorGroup) {
        this.creatorGroup = creatorGroup;
    }

    public HashSet<User> getGroupUsers() {
        return groupUsers;
    }
}
