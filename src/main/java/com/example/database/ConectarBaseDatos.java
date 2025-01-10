package main.java.com.example.database;
import java.sql.*;



public class ConectarBaseDatos {

    private Connection con;

    public ConectarBaseDatos() {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: Driver sqlite para JDBC no encontrado");
            return;
        }
        Connection connection = null;

        String url = "jdbc:sqlite:DonoSafe\\src\\db\\basedatos.db";

        try {
            // Crear una conexión de BD
            connection = DriverManager.getConnection("jdbc:sqlite:DonoSafe\\src\\db\\basedatos.db");} catch(SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // Cierre de conexión fallido
                System.err.println(e);
            }
        }
    }

    public Connection getCon() {
        return con;
    }

    public void cerrarCon() {
        try {
            if (con != null) {
                System.out.println("Conexion cerrada");
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

