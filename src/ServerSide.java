import java.io.*;
import java.net.*;

public class ServerSide {
    public static void main(String[] args) throws IOException {
        int port = 9090;  // Changeable port
        ServerSocket incoming = new ServerSocket(port);
        Socket client = incoming.accept();  // Wait for an incoming client connection
        // receiver = makeReaderFor(client.getInputStream());
        // sender = makeWriterFor(client.getOutputStream());
    }
}
