package main.java.com.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Establece una conexión entre el cliente y el servidor. Dependiendo
 * del comienzo de la cadena de texto recibida, hará lo siguiente:
 *
 * <pre>
 * Formato:                      |  Función:
 * -------------------------------------------------------------------
 * [ALERT] descripción           |  Llama a {@link #client}.showAlert()
 * USERS_LIST: lista_usuarios    |  Actualiza el panel de usuarios
 * usuario(private): mensaje     |  Añade un mensaje al panel del chat
 * GROUP Id:Emisor] mensaje      |  Añade un mensaje al panel del grupo
 * [NEW_GROUP] Id:Creador Info   |  Actualiza la lista de los grupos
 * </pre>
 *
 * @see Client
 * @author daniel.sanchez.r
 */
public class ServerConnection implements Runnable {
    private Socket server;
    private BufferedReader in;
    private Client client;
    private ClientGUI clientGUI;

    /**
     * Crea una conexión entre el cliente y el servidor
     *
     * @param  s        Socket del servidor
     * @param  client   Un cliente
     *
     * @throws IOException
     */
    public ServerConnection(Socket s, Client client) throws IOException {
        this.client = client;
        this.clientGUI = client.getClientGUI();
        this.server = s;
        this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    @Override
    public void run() {
        try {
            /* Bucle en el cual se comprueba si hay nuevos mensajes por parte del servidor y
            * permite llamar a métodos de la clase del cliente en función del tipo de mensaje
            * recibido */
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                System.out.println(serverResponse);
                int ind = serverResponse.indexOf(":");
                if (serverResponse.startsWith("[ALERT] ")) {
                    clientGUI.showAlert(serverResponse);
                } else if (serverResponse.startsWith("USERS_LIST:")) {
                    if (serverResponse.contains("," + client.getUserame() + ",")) {
                        serverResponse = serverResponse.replace("," + client.getUserame() + ",", ",");
                    } else {
                        serverResponse = serverResponse.replaceFirst(",?" + client.getUserame() + ",?", "");
                    }
                    String[] users = serverResponse.substring(11).split(",");
                    if (!users[0].isEmpty()) {
                        clientGUI.updateUsersList(users);
                    }
                } else if (serverResponse.contains("(private)")) {
                    showPrivateMessage(serverResponse);
                } else if (serverResponse.startsWith("GROUP ")) {
                    showGroupMessage(serverResponse);
                } else if (serverResponse.startsWith("[NEW_GROUP] ")) {
                    String groupId = serverResponse.substring(12, ind);
                    String[] groupInfo = serverResponse.substring(serverResponse.lastIndexOf(" ")+1).split(";");
                    client.addGroup(groupId, groupInfo);
                } else {
                    System.out.println("[ERROR] Unknown message format");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * Muestra un mensaje privado entre dos personas
     */
    private void showPrivateMessage(String s) {
        if (!clientGUI.getUserTalkingTo().equals(s.substring(0, s.indexOf("(")))) {
            String user = s.substring(0, s.indexOf("(private)"));
            String msg = s.substring(s.indexOf(":")+2);
            clientGUI.receiveMessage(user, msg);
        } else {
            clientGUI.addPrivateMessage(s.replaceFirst("[(]private[)]", ""));
        }
    }

    /**
     * Muestra un mensaje de grupo
     */
    private void showGroupMessage(String s) {
        String group = s.substring(6, s.indexOf(":"));
        String sender = s.substring(s.indexOf(":")+1, s.indexOf("]"));
        String msg = s.substring(s.indexOf("]")+2);

        if (sender.equals(client.getUserame())) {
            clientGUI.addGroupMessage("You: " + msg);
        } else if (clientGUI.getGrupoActual().equals(group)) {
            clientGUI.addGroupMessage(sender + ": " + msg);
        }
    }

    /**
     * Cierra la conexión
     */
    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (server != null && !server.isClosed()) server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
