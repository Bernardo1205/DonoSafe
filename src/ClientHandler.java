import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private static HashMap<String, Grupo> mapGrupos = new HashMap<>();
    public HashMap<String, ClientHandler> clients;
    private String clientName;

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

            /* Read the client's username */
            clientName = in.readLine();
            clients.put(clientName, this);
            broadcastUserList();

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                if (message.startsWith("[ALERT] ")) { /* Alert message format: [ALERT] content */
                    notifyAlertToAll(message);
                } else if (message.startsWith("@")) { /* Private message format: @targetUser message */
                    String targetUser = message.substring(1, message.indexOf(" "));
                    String privateMessage = message.substring(message.indexOf(" ") + 1);
                    sendMessageToClient(targetUser, "(private): " + privateMessage);
                } else if (message.startsWith("GROUP ")) {
                    String targerGroup = message.substring(message.indexOf(" ")+1, message.indexOf(":")).strip();
                    sendMessageToGroup(targerGroup, message.substring(message.indexOf(targerGroup)+targerGroup.length()));
                } else if (message.startsWith("[NEW_GROUP] ")) { /* New group message format: [NEW_GROUP] groupName:username*/
                    int ind = message.indexOf(":");
                    String groupName = message.substring(12, ind).strip();
                    String groupCreator = message.substring(ind + 1, message.indexOf(" ", ind));
                    String groupOrig = message.substring(message.lastIndexOf(" "), message.lastIndexOf(";"));
                    String groupDest = message.substring(message.lastIndexOf(";")+1);
                    createGroup(groupName, groupCreator, groupOrig, groupDest);
                    notifyAlertToAll(message);
                } else if (message.startsWith("JOIN_GROUP ")) {
                    String userName = message.substring(11, message.indexOf(" TO ")).strip();
                    String groupName = message.substring(message.indexOf(" TO ")+4);
                    joinGroup(groupName, clients.get(userName));
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

    public void notifyAlertToAll(String alertText) {
        for (ClientHandler clientHandler : clients.values()) {
            clientHandler.out.println(alertText);
        }
    }

    public synchronized void createGroup(String groupName, String creator, String orig, String dest) {
        Grupo nuevoGrupo = new Grupo(creator, clients.get(creator), this.out, groupName, orig, dest, LocalDateTime.now());
        mapGrupos.putIfAbsent(groupName, nuevoGrupo);
    }

    public synchronized void joinGroup(String groupName, ClientHandler client) {
        if (!mapGrupos.get(groupName).containsCliente(client)) {
            mapGrupos.get(groupName).añadirUsuario(client);
        }
    }

    public synchronized void sendMessageToGroup(String targetGroup, String message) {
        for (ClientHandler clientHandler : mapGrupos.get(targetGroup).getClientes()) {
            clientHandler.out.println("GROUP " + targetGroup + message);
        }
    }

    private void sendMessageToClient(String targetUser, String message) {
        ClientHandler targetHandler = clients.get(targetUser);
        if (targetHandler != null) {
            targetHandler.out.println(clientName + message);
        } else {
            targetHandler.closeConnection();
            System.out.println("Couldn't find " + targetUser);
        }
    }

    // Method to allow clients visualise other connected users
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
