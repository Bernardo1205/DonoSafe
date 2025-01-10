package main.java.com.example.server;

import main.java.com.example.database.BaseDatos;
import main.java.com.example.mapa.Reporte;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Maneja el hilo de un cliente, recibe mensajes de este y los parsea
 * para elegir qué acción hacer en cada momento. En los mensajes es
 * probable que aparezca el nombre de un usuario haciendo referencia
 * a un hilo. Para saber a qué cliente está dirigido dicho mensaje, es
 * necesario almacenar el nombre de usuario de cada cliente en la variable
 * clients, esto se hará nada más el cliente inicie sesión - es decir,
 * cuando se introduzcan los datos correctos se enviará el nombre del
 * usuario recién registrado al servidor y este se encargará de actualizar
 * el nombre de usuario con el que se referenciará a cada hilo.
 *
 * @see Server
 * @see main.java.com.example.client.Client
 */
public class ClientHandler implements Runnable {
    private static BaseDatos bbdd;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private static HashMap<String, Grupo> mapGrupos = new HashMap<>();
    public static HashMap<String, ClientHandler> clients;
    private String clientName;
    private String clientInfo;

    /**
     * Crea un hilo para un cliente y le envía la lista de usuarios conectados
     * @param clientSocket
     * @param clients
     */
    public ClientHandler(Socket clientSocket, HashMap<String, ClientHandler> clients , BaseDatos bbdd) {
        this.client = clientSocket;
        this.clients = clients;
        ClientHandler.bbdd = bbdd;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            String[] line;
            String[] userInfo;


            /* Bucle que valida las credenciales del usuario */
            while (true) {
                line = waitForMessage().split(":");
                if (line[0].equals("Login")) {
                    userInfo = line[1].split(",");
                    /* Si el usuario ya está conectado */
                    if (clients.containsKey(userInfo[0])) {
                        sendMessageToClient("Code: 409 (Conflict)");
                    }
                    clientInfo = bbdd.getUsuario(userInfo[0], userInfo[1]);
                    if (clientInfo == null) {
                        sendMessageToClient("Code: 401 (Unauthorized)");
                    } else {
                        this.clientName = userInfo[0];
                        sendMessageToClient("Code: 200 (OK)");
                        /* TODO Enviar los grupos activos, reportes y alertas recientes...
                         *   para que el cliente esté actualizado para cuando se inicie la App */
                        sendMessageToClient(clientInfo);
                        break;
                    }
                } else if (line[0].equals("Register")) {
                    userInfo = line[1].split(",");
                    bbdd.insertarUsuario(userInfo[0], userInfo[1], userInfo[2], userInfo[3]);
                }
            }
            clients.put(clientName, this);
            broadcastUserList();

            String message;
            while (true) {
                message = waitForMessage();
                System.out.println(message);
                int ind = message.indexOf(":");
                if (message.startsWith("[NEW_WAYPOINT] ")){
                    sendMessageToAll(message);
                    String[] info_waypoints = message.substring(message.indexOf(" ")).split(",");
                    Double latitud = Double.parseDouble(info_waypoints[1]);
                    Double longitud = Double.parseDouble(info_waypoints[0]);
                    info_waypoints[3] = info_waypoints[3].replace("T", " ").substring(0, info_waypoints[3].indexOf(".")-3);
                    bbdd.insertarReporte(longitud , latitud , info_waypoints[2],info_waypoints[3],Integer.parseInt(info_waypoints[4]) ,info_waypoints[5]);
                } else if (message.startsWith("[ALERT] ")) { /* Alert message format: [ALERT] content */
                    sendMessageToAll(message);
                } else if (message.startsWith("@")) { /* Private message format: @targetUser message */
                    String targetUser = message.substring(1, message.indexOf(" "));
                    String privateMessage = message.substring(message.indexOf(" ") + 1);
                    clients.get(targetUser).sendMessageToClient(this.clientName + "(private): " + privateMessage);
                } else if (message.startsWith("GROUP ")) {
                    String targerGroup = message.substring(message.indexOf(" ")+1, ind).strip();
                    String sender = message.substring(ind+1, message.indexOf("]"));
                    String contenido = message.substring(message.indexOf("]")+2);
                    //TODO guardar en la base de datos

                    mapGrupos.get(targerGroup).sendMessage(sender, contenido);
                } else if (message.startsWith("[NEW_GROUP] ")) { /* New group message format: [NEW_GROUP] groupName:username*/

                    String groupCreator = message.substring(message.indexOf("[NEW_GROUP] ")+"[NEW_GROUP] ".length() , message.lastIndexOf("|"));

                    /* groupInfo contiene el origen, el destino y la hora del grupo */
                    String[] groupInfo = message.substring(message.lastIndexOf("|")+1).split(";");
                    for (String s : groupInfo) {
                        System.out.println(s);
                    }
                    createGroup(groupCreator, groupInfo[0], groupInfo[1],groupInfo[2]);
                } else if (message.startsWith("JOIN_GROUP ")) {
                    //Guardar en la base de datos
                    String name = message.substring(message.indexOf("JOIN_GROUP ") + "JOIN_GROUP ".length(), message.indexOf(" TO ")).strip();
//                    int idParseado = Integer.parseInt(id);
                    String groupId = message.substring(message.indexOf(" TO ") + 4);
                    System.out.println(name);
                    System.out.println(groupId);
                    joinGroup(groupId, clients.get(name));
//                    bbdd.añadirUsuario(groupId, id);

                } else {
                    System.out.println("Unknown type " + message);
                }
            }
        } catch (SocketException e1) {
            //
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * Función recursiva que espera a recibir un mensaje del cliente
     *
     * @return  Mensaje recibido
     *
     * @throws IOException
     */
    private String waitForMessage() throws IOException {
        String serverResponse = in.readLine();
        if (serverResponse.isEmpty()) {
            return waitForMessage();
        } else {
            return serverResponse;
        }
    }

    /**
     * Manda un mensaje a todos los clientes conectados
     */
    public void sendMessageToAll(String mensaje) {
        for (ClientHandler clientHandler : clients.values()) {
            clientHandler.out.println(mensaje);
        }
    }

    /**
     * Crea un grupo y notifica a todos los clientes conectados
     */
    private void createGroup(String creator, String orig, String dest,String hour) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(hour, timeFormatter);

        // Obtener la fecha actual
        LocalDate currentDate = LocalDate.now();

        // Combinar la fecha actual con el tiempo parseado para obtener LocalDateTime
        LocalDateTime dateTime = LocalDateTime.of(currentDate, time);
        Grupo nuevoGrupo = new Grupo(clients.get(creator), orig, dest, dateTime);

        mapGrupos.put(nuevoGrupo.getId(), nuevoGrupo);

        sendMessageToAll("[NEW_GROUP_TOALL] " + nuevoGrupo.getId() + "|" + String.join(";", orig, dest,hour));
        bbdd.insertarGrupo(nuevoGrupo.getId(), dest, orig, hour,LocalDate.now().toString());

    }

    /**
     * Añade un cliente al grupo
     */
    private void joinGroup(String groupId, ClientHandler client) {
        Grupo grupo = mapGrupos.get(groupId);
        if (grupo == null) {
            System.err.println("Error: Grupo con ID " + groupId + " no encontrado.");
            return;
        }
        if (!mapGrupos.get(groupId).containsUsuario(client)) {
            mapGrupos.get(groupId).añadirUsuario(client);
        }
    }

    public String getClientName() {
        return this.clientName;
    }

    /**
     * Manda un mensaje privado a un cliente en específico
     */
    public void sendMessageToClient(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void sendMessageToClient2(ClientHandler clientHandler, String message) {
        if (clientHandler != null) {
            clientHandler.out.println(message);
        }
    }

    /* Manda la lista de clientes conectados a cada cliente */
    private void broadcastUserList() {
        for (ClientHandler clientHandler : clients.values()) {
            clientHandler.out.println("USERS_LIST:" + String.join(",", clients.keySet()));
        }
    }

    private void closeConnection() {
        try {
            clients.remove(clientName);
            broadcastUserList();
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null) client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
