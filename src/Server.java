import java.io.*;
import java.net.*;
import java.util.HashMap;


public class Server {
    private static final int PORT = 9090;
    private static HashMap<String, ClientHandler> clients = new HashMap<>();

    public Server () throws IOException {
        ServerSocket listener = new ServerSocket(PORT);

        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket client = listener.accept();  // Wait for an incoming client connection
            System.out.println("[SERVER] Connected to client!");

            ClientHandler clientHandler = new ClientHandler(client, clients);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
        }
    }

    public String toString() {
        return "Server running in PORT=" + PORT + "\nNº of clients connected: " + clients.size();
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
