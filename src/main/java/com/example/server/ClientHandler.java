package main.java.com.example.server;

import main.java.com.example.client.Client;
import main.java.com.example.database.BaseDatos;

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
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private static HashMap<String, Grupo> mapGrupos = new HashMap<>();
    public static HashMap<String, ClientHandler> clients;
    private static BaseDatos bbdd;
    private String clientName;
    private String clientInfo;

    /**
     * Crea un hilo para un cliente y le envía la lista de usuarios conectados
     * @param clientSocket
     * @param clients
     */
    public ClientHandler(Socket clientSocket, HashMap<String, ClientHandler> clients, BaseDatos bbdd) {
        this.client = clientSocket;
        this.clients = clients;
        ClientHandler.bbdd = bbdd;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            String[] userInfo;
            /* Valida el usuario y la contraseña del cliente */
            while (true) {
                userInfo = waitForMessage().split(",");
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
                    /* TODO Enviar solo los datos relevantes */
                    /* TODO Enviar los grupos activos, reportes y alertas recientes...
                    *   para que el cliente esté actualizado para cuando se inicie la App */
                    sendMessageToClient(clientInfo);
                    break;
                }
            }
            clients.put(clientName, this);

            broadcastUserList();

            /* Bucle principal que sirve como comunicación entre este hilo y el del cliente.
            * Cuando recibe un mensaje, lo parsea y decide qué hacer en función de su contenido
            */
            String message;
            while (true) {
                message = waitForMessage();
                System.out.println(message);
                int ind = message.indexOf(":");
                if (message.startsWith("[NEW_WAYPOINT] ")) { /* Waypoint message format: [NEW_WAYPOINT] content */
                    //TODO guardar waypoint en la base de datos
                    sendMessageToAll(message);
                } else if (message.startsWith("[ALERT] ")) { /* Alert message format: [ALERT] content */
                    // TODO guardar alerta en la base de datos
                    sendMessageToAll(message);
                } else if (message.startsWith("@")) { /* Private message format: @targetUser message */
                    String targetUser = message.substring(1, message.indexOf(" "));
                    String privateMessage = message.substring(message.indexOf(" ") + 1);
                    // TODO guardar mensaje privado en la base de datos
                    clients.get(targetUser).sendMessageToClient(this.clientName + "(private): " + privateMessage);
                } else if (message.startsWith("GROUP ")) { /* Group message format: GROUP groupId:sender]message */
                    String targerGroup = message.substring(message.indexOf(" ")+1, ind).strip();
                    String sender = message.substring(ind+1, message.indexOf("]"));
                    String contenido = message.substring(message.indexOf("]")+1);
                    //TODO guardar mensaje grupal en la base de datos

                    mapGrupos.get(targerGroup).sendMessage(sender, contenido);
                } else if (message.startsWith("[NEW_GROUP] ")) { /* New group message format: [NEW_GROUP] groupName:username*/
                    String groupCreator = message.substring(message.indexOf(" ")+1, message.lastIndexOf("|"));
                    String[] groupInfo = message.substring(message.lastIndexOf("|")).split(";");
                    createGroup(groupCreator, groupInfo[0], groupInfo[1],groupInfo[2]);
                } else if (message.startsWith("JOIN_GROUP ")) { /* Join group message format: JOIN_GROUP username TO groupId */
                    String userName = message.substring(11, message.indexOf(" TO ")).strip();
                    String groupId = message.substring(message.indexOf(" TO ") + 4);
                    // TODO añadir usuario al grupo en la base de datos
                    joinGroup(groupId, clients.get(userName));
                } else if (message.startsWith("[Request]")) {
                    // TODO enviar la información solicitada
                } else {
                    System.out.println("[Error Code: 400 (Bad Request)] " + message);
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
        if (serverResponse == null) {
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

        mapGrupos.putIfAbsent(nuevoGrupo.getId(), nuevoGrupo);
        sendMessageToAll("[NEW_GROUP] " + nuevoGrupo.getId() + ":" + creator + "|" + String.join(";", orig, dest,hour));
    }

    /**
     * Añade un cliente al grupo
     */
    private void joinGroup(String groupId, ClientHandler client) {
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
        this.out.println(message);
    }

    public void sendMessageToClient(ClientHandler clientHandler, String message) {
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
