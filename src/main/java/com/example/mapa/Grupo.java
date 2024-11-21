package mapa;

import main.java.com.example.mapa.User;

import java.util.HashSet;
import java.util.Set;

public class Grupo {
    private HashSet<User> groupUsers;
    private mapa.Localizacion origen;
    private mapa.Localizacion destino;
    private User creatorGroup;

    public Grupo(){
        this.groupUsers = new HashSet<>();
    }

    public mapa.Localizacion getOrigen() {
        return origen;
    }

    public void setOrigen(mapa.Localizacion origen) {
        this.origen = origen;
    }

    public mapa.Localizacion getDestino() {
        return destino;
    }

    public void setDestino(mapa.Localizacion destino) {
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
