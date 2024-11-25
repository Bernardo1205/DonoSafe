package main.java.com.example.server;

import main.java.com.example.config.ServerConfig;

import java.io.*;
import java.net.*;
import java.util.HashMap;


public class Server {
    private static HashMap<String, ClientHandler> clients = new HashMap<>();

    public Server () throws IOException {
        /* Se inicia el hilo del servidor */
        ServerSocket listener = new ServerSocket();
        listener.bind(new InetSocketAddress(ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT));
        System.out.println(Server.this);

        /* TODO Implementar la base de datos y comenzar la conexión */

        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket client = listener.accept();  /* Espera a conexiones de clientes y las acepta */
            System.out.println("[SERVER] Connected to client at " + client.getInetAddress() + ":" + client.getPort());

            ClientHandler clientHandler = new ClientHandler(client, clients);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
        }
    }

    public String toString() {
        return "main.java.com.example.server.Server running in IP address=" + ServerConfig.SERVER_IP + " and PORT=" + ServerConfig.SERVER_PORT;
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
