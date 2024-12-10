package main.java.com.example.client;

import main.java.com.example.config.ServerConfig;
import main.java.com.example.mapa.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * La clase Client se encarga de renderizar la pantalla del cliente
 * y de conectarse al servidor para luego mandar y recibir mensajes.
 *
 * @see App
 * @see ClientLogin
 * @see ClientRegister
 * @see ServerConnection
 * @author daniel.sanchez.r
 */
public class Client {

    private String nombre;

    private String username;
    public App clientGUI;
    public HashMap<String, String[]> grupos = new HashMap<>();

    private int id;

    private String[] userInfo; // [id, nombre, edad, sexo, mail, password]
    private int edad;
    private String sexo;
    private String mail;
    private String password;

    private LocalDateTime horaDelReporte;
    private ArrayList<String> review;
    private LocalDateTime reg_date;
    private String decrp;
    private Set<Reporte> list_waypoints;

    private ServerConnection serverConn;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Registrar o ubicar al cliente en la base de datos
     */
    public Client() {

        this.reg_date = LocalDateTime.now();
        this.review = new ArrayList<>();
        this.list_waypoints = new HashSet<>();

        try {
            socket = new Socket(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT);
            System.out.println(String.format("Started client at %s", socket.getLocalSocketAddress()));

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (ConnectException ex1) {
            System.out.format("Servidor en %s:%s no encontrado", ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT);
            System.exit(0);
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }

        new ClientLogin(Client.this);
    }

    /**
     * Tras haberse registrado correctamente, se renderiza la pantalla del
     * cliente y se conecta al servidor
     * @param username
     * @see ServerConnection
     */
    public void openChatWindow(String[] clientInfo) {

        /* 'clientInfo' contiene la información relevante al cliente dentro de la base de datos
        * del servidor.
        * Formato del Array: id,nombre,nombre_usuario,sexo,edad,contraseña,reseña,fecha_registro,email,descripción */
        this.id = Integer.parseInt(clientInfo[0]);
        this.username = clientInfo[2];
        this.userInfo = clientInfo;

        this.clientGUI = new App(Client.this);

        ServerConnection serverConn = new ServerConnection(socket, this, in);
        new Thread(serverConn).start();

    }

    /**
     * Envía un mensaje al servidor mostrando el usuario y el grupo al
     * que quiere acceder.
     *
     * @param groupId
     */
    public void joinGroupChat(String groupId) {
        sendMessageToServer("JOIN_GROUP " + this.username + " TO " + groupId);
        clientGUI.openPrivateGroup(groupId);
    }

    /**
     * Función recursiva que espera a recibir un mensaje del servidor
     *
     * @return  Mensaje recibido
     *
     * @throws IOException
     */
    public String waitForMessage() throws IOException {
        String serverResponse = in.readLine();
        if (serverResponse == null) {
            return waitForMessage();
        } else {
            return serverResponse;
        }
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

    public String getNombre() {
        return nombre;
    }

    public void sendMessageToServer(String message) {
        out.println(message);
    }

    public void addGroup(String groupId, String[] groupInfo) {
        grupos.put(groupId, groupInfo);
        clientGUI.updateGroupsList(grupos);
    }

    /* Getters y setters */
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getUsername() {
        return this.username;
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

    public Array getReview() {
        return (Array) review;
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



    public static void main(String[] args) {
        new Client();
    }
}
