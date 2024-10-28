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
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                client.showMessage(serverResponse);
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
