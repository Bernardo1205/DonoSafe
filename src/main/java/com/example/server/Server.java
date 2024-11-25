package main.java.com.example.server;

import main.java.com.example.config.ServerConfig;

import java.io.*;
import java.net.*;
import java.util.HashMap;


public class Server {
    private static HashMap<String, ClientHandler> clients = new HashMap<>();
    private static models.BaseDatos bbdd;

    public Server () throws IOException {
        ServerSocket listener = new ServerSocket(ServerConfig.SERVER_PORT);
        bbdd = new models.BaseDatos();

        while (true) {
            System.out.println("[SERVER] Waiting for client connection...");
            Socket client = listener.accept();  // Wait for an incoming client connection
            System.out.println("[SERVER] Connected to client!");

            //TODO añadir la bbdd al clienthandler e implementar los métodos
            ClientHandler clientHandler = new ClientHandler(client, clients);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
        }
    }

    public String toString() {
        return "main.java.com.example.server.Server running in PORT=" + ServerConfig.SERVER_PORT + "\nNº of clients connected: " + clients.size();
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
