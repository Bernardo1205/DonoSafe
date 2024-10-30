import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private HashMap<String, ClientHandler> clients;
    private String clientName;

    public ClientHandler(Socket clientSocket, HashMap<String, ClientHandler> clients) {
        this.client = clientSocket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            // Read the client's username
            clientName = in.readLine();
            clients.put(clientName, this);
            broadcastUserList();

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                if (message.startsWith("[ALERT] ")) { // Alert message format: [ALERT] content
                    notifyAlertToAll(message);
                } else if (message.startsWith("@")) { // Private message format: @targetUser message
                    String targetUser = message.substring(1, message.indexOf(' '));
                    String privateMessage = message.substring(message.indexOf(' ') + 1);
                    sendMessageToClient(targetUser, privateMessage);
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

    private void notifyAlertToAll(String alertText) {
        for (ClientHandler clientHandler : clients.values()) {
            clientHandler.out.println(alertText);
        }
    }

    private void sendMessageToClient(String targetUser, String message) {
        ClientHandler targetHandler = clients.get(targetUser);
        if (targetHandler != null) {
            targetHandler.out.println(clientName + "(private): " + message);
        } else {
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
