package main.java.com.example.server;

import main.java.com.example.config.ServerConfig;
import main.java.com.example.database.BaseDatos;
import main.java.com.example.database.BaseDatos;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;


public class Server {
    private static HashMap<String, ClientHandler> clients = new HashMap<>();
    private static BaseDatos bbdd;

    public Server () throws IOException {
        /* Se inicia el hilo del servidor */
        ServerSocket listener = new ServerSocket();
        listener.bind(new InetSocketAddress(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT));
        System.out.println(Server.this);

        /* Conexión a la base de datos */
        bbdd = new BaseDatos();
        bbdd.ConectarBaseDatos();

        /* Bucle principal que crea instancias de ClientHandler a medida que recicbe conexiones de clientes */
        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket client = listener.accept();  /* Espera a conexiones de clientes y las acepta */
            System.out.println("[SERVER] Connected to client at " + client.getInetAddress() + ":" + client.getPort());

            ClientHandler clientHandler = new ClientHandler(client, clients, bbdd);
            new Thread(clientHandler).start();
        }
    }

    public String toString() {
        return "main.java.com.example.server.Server running in IP address=" + ServerConfig.SERVER_IP + " and PORT=" + ServerConfig.SERVER_PORT;
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
