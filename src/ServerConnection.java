import java.io.*;
import java.net.*;

public class ServerConnection implements Runnable {
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;

    public ServerConnection(Socket s) throws IOException {
        server = s;
        in = new BufferedReader(new InputStreamReader(server.getInputStream()));
        out = new PrintWriter(server.getOutputStream(), true);
    }
    @Override
    public void run() {
        try {
            while (true) {
                String serverResponse = in.readLine();
                if (serverResponse == null) break;
                System.out.println("Server says " + serverResponse);
            }
        } catch (IOException e) {
                throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
