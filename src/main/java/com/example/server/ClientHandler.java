package main.java.com.example.server;

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
    private String clientName;

    /**
     * Crea un hilo para un cliente y le envía la lista de usuarios conectados
     * @param clientSocket
     * @param clients
     */
    public ClientHandler(Socket clientSocket, HashMap<String, ClientHandler> clients) {
        this.client = clientSocket;
        this.clients = clients;
        broadcastUserList();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            /* Recibe el nombre de usuario con el que se referenciará a este hilo en los mensajes */
            clientName = in.readLine();
            clients.put(clientName, this);
            broadcastUserList();

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                int ind = message.indexOf(":");
                if (message.startsWith("[NEW_WAYPOINT] ")){
                    sendMessageToAll(message);
                } else if (message.startsWith("[ALERT] ")) { /* Alert message format: [ALERT] content */
                    sendMessageToAll(message);
                } else if (message.startsWith("@")) { /* Private message format: @targetUser message */
                    String targetUser = message.substring(1, message.indexOf(" "));
                    String privateMessage = message.substring(message.indexOf(" ") + 1);
                    sendMessageToClient(targetUser, this.clientName + "(private): " + privateMessage);
                } else if (message.startsWith("GROUP ")) {
                    String targerGroup = message.substring(message.indexOf(" ")+1, ind).strip();
                    String sender = message.substring(ind+1, message.indexOf("]"));
                    String contenido = message.substring(message.indexOf("]")+2);
                    //TODO guardar en la base de datos

                    mapGrupos.get(targerGroup).sendMessage(sender, contenido);
                } else if (message.startsWith("[NEW_GROUP] ")) { /* New group message format: [NEW_GROUP] groupName:username*/
                    String groupCreator = message.substring(message.indexOf(" ")+1, message.lastIndexOf("|"));
                    String[] groupInfo = message.substring(message.lastIndexOf("|")).split(";");
                    createGroup(groupCreator, groupInfo[0], groupInfo[1],groupInfo[2]);
                } else if (message.startsWith("JOIN_GROUP ")) {
                    String userName = message.substring(11, message.indexOf(" TO ")).strip();
                    String groupId = message.substring(message.indexOf(" TO ") + 4);
                    joinGroup(groupId, clients.get(userName));
                } else {
                    System.out.println("Unknown type" + message);
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
    public void sendMessageToClient(String targetUser, String message) {
        ClientHandler targetHandler = clients.get(targetUser);
        if (targetHandler != null) {
            targetHandler.out.println(message);
        } else {
            targetHandler.closeConnection();
            System.out.println("Couldn't find " + targetUser);
        }
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
