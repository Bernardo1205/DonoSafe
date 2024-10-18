import javax.swing.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "127.0.0.1"; // Ip Adress for your own computer
    private static final int SERVER_PORT = 9090;
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        ServerConnection serverConn = new ServerConnection(socket);
        //BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(serverConn).start();

        while (true) {
            System.out.println(">");
            String command = keyboard.readLine();

            if (command.equals("quit")) break;
            out.println(command);

        }

        socket.close();
        System.exit(0);

    }
}
