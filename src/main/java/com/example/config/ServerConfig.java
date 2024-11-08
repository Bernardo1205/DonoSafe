package main.java.com.example.config;

/**
 * Configuración del servidor
 */
public final class ServerConfig {
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 9090;

    /**
     * Constructor privado para evitar instanciación
     */
    private ServerConfig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
