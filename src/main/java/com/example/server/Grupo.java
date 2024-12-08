package main.java.com.example.server;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

// Hacer funcion getClientesIDs

/**
 * Crea un grupo para establecer una conexión entre usuarios específicos
 * y mandar mensajes privados.
 *
 * <p> Los grupos contienen un identificador único, un listado de clientes,
 * el origen, el destino y la fecha
 *
 * @see ClientHandler
 * @author daniel.sanchez.r
 */
public class Grupo {
    /** Identificador único de cada grupo creado con UUID.randomUUID() */
    private final String id;

    private ArrayList<ClientHandler> clientes = new ArrayList<>();
    private String origen;
    private String destino;
    private LocalDateTime fecha;

    /**
     *
     * @param cliente
     * @param origen
     * @param destino
     * @param fecha
     */
    public Grupo(ClientHandler cliente, String origen, String destino, LocalDateTime fecha) {
        this.añadirUsuario(cliente);

        this.id = UUID.randomUUID().toString();
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
    }

    /**
     * Envía un mensaje a cada miembro del grupo indicando el id del grupo, quién
     * lo mandó y el contenido de dicho mensaje.
     * @param sender
     * @param message
     */
    public void sendMessage(String sender, String message) {
        for (ClientHandler clientHandler : clientes) {
            clientHandler.sendMessageToClient(clientHandler.getClientName(), "GROUP " + this.id + ":" + sender + "] " + message) ;
        }
    }

    /**
     * Añade un usuario al listado de clientes en el grupo y les notifica
     * @param cliente
     */
    public void añadirUsuario(ClientHandler cliente) {
        if (!this.getClientes().contains(cliente)) {
            this.clientes.add(cliente);
            this.sendMessage("[SERVER]", cliente.getClientName() + " joined the chat!");
        }
    }

    /**
     * Devuelve un valor booleano indicando si un cliente ya está en el grupo
     * @param cliente
     */
    public boolean containsUsuario(ClientHandler cliente) {
        return this.clientes.contains(cliente);
    }

    public ArrayList getClientes() {
        return  this.clientes;
    }

    public String getId() {
        return this.id;
    }

    public String getOrigen() {
        return this.origen;
    }

    public String getDestino() {
        return this.destino;
    }

    public String toString() {
        return "Miembros: " + clientes + ", Origen: " + origen + ", Destino: " + destino + ", Fecha: " + fecha;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public ArrayList<Integer> getClientesIds(){

    }
}
