import java.io.*;
import java.net.*;

public class ClientSide {
    public static void main(String[] args) throws IOException {
        String serverIp = "193.146.227.234";

        int serverPort = 9090;
        Socket s = new Socket(serverIp, serverPort);
        // receiver = makeReaderFor(s.getInputStream());
        // sender = makeWriterFor(s.getOutputStream());
        BufferedReader receiver = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter sender = new PrintWriter(s.getOutputStream(), true);

    }
}
