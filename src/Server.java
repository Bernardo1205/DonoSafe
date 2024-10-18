import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to which the client will connect to
 */
public class Server {
    private static final int PORT = 9090;
    private static String[] names = {"Jessica", "Noelia", "Maria"};
    private static String[] adjs = {"the gentle", "the old", "the great"};

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4); // Max number of clients at the same time
    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);

        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket client = listener.accept();  // Wait for an incoming client connection
            System.out.println("[SERVER] Connected to client!");
            ClientHandler clientThread = new ClientHandler(client, clients);
            clients.add(clientThread);

            pool.execute(clientThread);
        }

    }

    public static String getRandomName() {
        String name = names[(int) (Math.random()*names.length)];
        String adj = adjs[(int) (Math.random()* adjs.length)];
        return name + " " + adjs;


    }
}
