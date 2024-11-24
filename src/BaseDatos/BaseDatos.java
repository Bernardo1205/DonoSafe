package models;

import Mapa.User;
import Mapa.MyWaypoint;
import Mapa.Map;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseDatos{

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

    public void insertarUsuario(User usuario){
        String comandoInsertarUsuario = "INSERT INTO Usuario VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario,Statement.RETURN_GENERATED_KEYS)){
            pstmt.setString(2, usuario.getName());
            pstmt.setString(3, usuario.getUser_name());
            pstmt.setString(4, usuario.getSexo());
            pstmt.setInt(5, usuario.getEdad());
            pstmt.setString(6, usuario.getPassword());
            pstmt.setArray(7, usuario.getReview());
            LocalDateTime reg_date = usuario.getReg_date();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fecha_formateada = reg_date.format(formatter);
            pstmt.setString(8,
                    fecha_formateada);
            pstmt.setString(9, usuario.getMail());
            pstmt.setString(10, usuario.getDecrp());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void insertarWayPoint(MyWaypoint reporte)  {
        String comandoInsertarUsuario = "INSERT INTO Reporte VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario,Statement.RETURN_GENERATED_KEYS)){

            pstmt.setInt(2, reporte.getUser().getId());

            pstmt.setString(3, reporte.getDescripcion());

            LocalDateTime hora_reporte = reporte.getHora_reporte();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String hora_formateada = hora_reporte.format(formatter);
            pstmt.setString(4, hora_formateada);


            LocalDateTime fecha_reporte_creacion = reporte.getFecha_creacion();
            String fecha_formateada = fecha_reporte_creacion.format(formatter);
            pstmt.setString(5, fecha_formateada);

            LocalDateTime fecha_reporte_finalizacion = fecha_reporte_creacion.plusDays(2);
            String fecha_finalizacion_formateada = fecha_reporte_finalizacion.format(formatter);
            pstmt.setString(6,fecha_finalizacion_formateada);

            pstmt.setString(7, reporte.getTipoPeligro());


        } catch (SQLException e) {
            throw new RuntimeException(e);
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

