package main.java.com.example.config;

/**
 * Configuración del cliente
 */
public final class ClientConfig {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    /**
     * Constructor privado para evitar instanciación
     */
    private ClientConfig() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}