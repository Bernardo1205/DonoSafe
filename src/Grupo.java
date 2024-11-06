import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Grupo {
    private ArrayList<ClientHandler> clientes = new ArrayList<>();
    private PrintWriter out;
    private String nombre;
    private String origen;
    private String destino;
    private LocalDateTime fecha;

    public Grupo(String username, ClientHandler cliente, PrintWriter out, String nombre, String origen, String destino, LocalDateTime fecha) {
        this.clientes.add(cliente);
        this.out = out;
        this.nombre = nombre;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
    }

    public void sendMessage(String sender, String message) {
        for (ClientHandler clientHandler : clientes) {
            out.println("GROUP " + nombre + ":" + sender + "] " + message);
        }
    }

    public void añadirUsuario(ClientHandler cliente) {
        if (cliente != null) {
            this.clientes.add(cliente);
        }
    }

    public boolean containsCliente(ClientHandler cliente) {
        return this.clientes.contains(cliente);
    }

    public ArrayList<ClientHandler> getClientes() {
        return this.clientes;
    }

    public String getNombre() {
        return this.nombre;
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
}
