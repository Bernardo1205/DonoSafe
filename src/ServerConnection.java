import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements Runnable {
    private Socket server;
    private BufferedReader in;
    private Client client;

    public ServerConnection(Socket s, Client client) throws IOException {
        this.client = client;
        this.server = s;
        this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    @Override
    public void run() {
        try {
            // Read new messages received by the server
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                System.out.println(serverResponse);
                if (serverResponse.startsWith("[ALERT] ")) {
                    client.showAlert(serverResponse);
                } else if (!client.getDisplayedChat().equals(serverResponse.substring(0, 1))) {
                    client.showMessage(serverResponse.replaceFirst("private", "DM"));
                } else if (serverResponse.startsWith("GROUP ")) {
                    client.showMessage(serverResponse);
                } else if (serverResponse.startsWith("[NEW_GROUP] ")) {
                    int ind = serverResponse.indexOf(":");
                    client.addGroup(serverResponse.substring(12, ind), serverResponse.substring(serverResponse.indexOf(" "), ind).split(";"));
                    client.updateGroupsList(client.getGroups());
                } else if (serverResponse.startsWith("[OFFLINE] ")) {
                    //
                } else {
                    client.showMessage(serverResponse);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (server != null && !server.isClosed()) server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
