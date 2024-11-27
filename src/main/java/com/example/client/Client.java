package main.java.com.example.client;

import main.java.com.example.config.ClientConfig;
import main.java.com.example.config.ServerConfig;
import main.java.com.example.mapa.*;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * La clase Client se encarga de renderizar la pantalla del cliente
 * y de conectarse al servidor para luego mandar y recibir mensajes.
 *
 *
 * @see ClientLogin
 * @see ClientGUI
 * @see ServerConnection
 * @author daniel.sanchez.r
 */
public class Client {

    private String userName;
    public App clientGUI;
    public HashMap<String, String[]> grupos = new HashMap<>();
    private static Socket socket;
    private static PrintWriter out;

    private int edad;
    private String sexo;
    private String mail;
    private String password;

    private ArrayList<String> review;
    private LocalDateTime reg_date;
    private String decrp;
    private Set<MyWaypoint> list_waypoints;

    /**
     * Registrar o ubicar al cliente en la base de datos
     */
    public Client() {

        this.reg_date = LocalDateTime.now();
        this.review = new ArrayList<>();
        this.list_waypoints = new HashSet<>();
        new ClientLogin(this);
    }

    /**
     * Tras haberse registrado correctamente, se renderiza la pantalla del
     * cliente y se conecta al servidor
     * @param username
     * @see ServerConnection
     */
    public void openChatWindow(String username) {

        userName = username;

        this.clientGUI = new App(Client.this);

        try {
            socket = new Socket(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT);

            ServerConnection serverConn = new ServerConnection(socket, this);
            out = new PrintWriter(socket.getOutputStream(), true);
            /* Informar al servidor del nombre de usuario para que este pueda manejar el
            * hilo de cada cliente, permitiéndole saber a quien enviar cada mensaje */
            out.println(username);

            new Thread(serverConn).start();

        } catch (ConnectException ex1) {
            System.out.format("Servidor en %s/%s no encontrado", ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT);
            System.exit(0);
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }

    /**
     * Envía un mensaje al servidor mostrando el usuario y el grupo al
     * que quiere acceder.
     *
     * @param groupId
     */
    public void joinGroupChat(String groupId) {
        sendMessageToServer("JOIN_GROUP " + this.userName + " TO " + groupId);
        clientGUI.openPrivateGroup(groupId);
    }

    /**
     * Envía un mensaje al servidor con el mensaje que se quiere enviar
     * al grupo.
     *
     * @param groupId
     * @param message
     */
    public App getClientGUI() {
        return this.clientGUI;
    }

    public void sendMessageToServer(String message) {
        out.println(message);
    }

    public void addGroup(String groupId, String[] groupInfo) {
        grupos.put(groupId, groupInfo);
        clientGUI.updateGroupsList(grupos);
    }

    public String getUserame() {
        return this.userName;
    }

    public HashMap<String, String[]> getGrupos() {
        return grupos;
    }

    public String getDecrp() {
        return decrp;
    }

    public void setDecrp(String decrp) {
        this.decrp = decrp;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
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

    public ArrayList<String> getReview() {
        return review;
    }

    public void setReview(ArrayList<String> review) {
        this.review = review;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        Client.socket = socket;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static void main(String[] args) {
        new Client();
    }
}
