package models;

import java.sql.*;

public class BaseDatos {

    private Connection con = null;

    // Conectar a la base de datos
    public void ConectarBaseDatos() {
        try {
            Class.forName("org.sqlite.JDBC"); // Cargar el controlador
            System.out.println("Driver cargado con éxito.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: Driver SQLite para JDBC no encontrado");
            return;
        }

        String url = "jdbc:sqlite:C:/Users/lucas.stille/basedatos.db";

        try {
            con = DriverManager.getConnection(url); // Asignar a 'con'
            System.out.println("Conexión establecida exitosamente.");
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    // Obtener la conexión
    public Connection getCon() {
        return con;
    }

    // Cerrar la conexión
    public void cerrarCon() {
        try {
            if (con != null) {
                con.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    // Método principal para probar la conexión
    public static void main(String[] args) {
        BaseDatos basedatos = new BaseDatos();
        basedatos.ConectarBaseDatos();
        Connection connection = basedatos.getCon();
        if (connection != null) {
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS prueba(id_prueba INTEGER PRIMARY KEY, nombre_prueba TEXT)");
                System.out.println("Tabla creada o ya existente.");
            } catch (SQLException e) {
                System.err.println("Error al crear la tabla prueba: " + e.getMessage());
            } finally {
                basedatos.cerrarCon();
            }
        }
    }
}
