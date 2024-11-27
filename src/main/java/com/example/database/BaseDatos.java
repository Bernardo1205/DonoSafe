package models;

import main.java.com.example.client.Client;
import main.java.com.example.mapa.User;
import main.java.com.example.mapa.MyWaypoint;
import main.java.com.example.server.Grupo;

import main.java.com.example.mapa.Map;
import main.java.com.example.server.ClientHandler;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import main.java.com.example.server.ClientHandler;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    public void insertarUsuario(Client cliente) {
        String comandoInsertarUsuario = "INSERT INTO Usuario VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getUserName());
            pstmt.setString(4, cliente.getSexo());
            pstmt.setInt(5, cliente.getEdad());
            pstmt.setString(6, cliente.getPassword());
            pstmt.setArray(7, cliente.getReview());
            LocalDateTime reg_date = cliente.getReg_date();
            String fecha_formateada = reg_date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(8,
                    fecha_formateada);
            pstmt.setString(9, cliente.getMail());
            pstmt.setString(10, cliente.getDecrp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertarWayPoint(MyWaypoint reporte) {
        String comandoInsertarUsuario = "INSERT INTO Reporte VALUES (?,?,?,?,?,?,?)";
        Statement statement = null;
        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(2, reporte.getUser().getId());

            pstmt.setString(3, reporte.getDescripcion());

            LocalDateTime hora_reporte = reporte.getHoraDelReporte();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String hora_formateada = hora_reporte.format(formatter);
            pstmt.setString(4, hora_formateada);


            LocalDateTime fecha_reporte_creacion = reporte.getHoraCreacion();
            String fecha_formateada = fecha_reporte_creacion.format(formatter);
            pstmt.setString(5, fecha_formateada);

            LocalDateTime fecha_reporte_finalizacion = fecha_reporte_creacion.plusHours(1);
            String fecha_finalizacion_formateada = fecha_reporte_finalizacion.format(formatter);
            pstmt.setString(6, fecha_finalizacion_formateada);

            pstmt.setString(7, reporte.getTipoPeligro());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void insertarMensaje(String groupId, int id_usuario, String fecha, String contenido) {
        //int grupoId, String username/id, String mensaje, String fecha

        String comandoInsertarMensaje = "INSERT INTO Mensaje VALUES (?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarMensaje, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, groupId);
            pstmt.setInt(2, id_usuario);
            pstmt.setString(3, fecha);
            pstmt.setString(4, contenido);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertarGrupo(Grupo grupo) throws SQLException {
        String comandoInsertarGrupo = "INSERT INTO Grupo VALUES (?,?,?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarGrupo, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setArray(2, grupo.getClientes());
            pstmt.setString(3, grupo.getDestino());
            pstmt.setString(4, grupo.getOrigen());
            LocalDateTime fecha_creacion = grupo.getFecha();
            String fechaCreacionFormateada = fecha_creacion.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pstmt.setString(5, fechaCreacionFormateada);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void insertarReseña(String contenidoReseña, int calificacion, String fecha, int idReseñador, int idUsuario) {
        String comandoInsertarGrupo = "INSERT INTO Reseña VALUES (?,?,?,?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarGrupo, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(2, contenidoReseña);
            pstmt.setInt(3, idReseñador);
            pstmt.setInt(4, idUsuario);
            pstmt.setString(5, fecha);
            pstmt.setInt(6, calificacion);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String[]> mostarReseñas(int idUsuario) throws SQLException {

        String comandoObtenerReseñas = "SELECT contenido_reseña, calificacion, id_reseñador, fecha FROM Reseña where id_usuario = ?;";
        ArrayList<String[]> listaReseñas = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(comandoObtenerReseñas)) {

            // Establecer el parámetro persona_id en la consulta
            stmt.setInt(1, idUsuario);

            // Ejecutar la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                // Recorrer los resultados y almacenar en un array de arrays de strings
                while (rs.next()) {
                    String fecha = rs.getString("fecha");
                    String calificacion = rs.getString("calificacion");
                    String contenido = rs.getString("contenido");

                    // Agregar al array
                    listaReseñas.add(new String[]{fecha, calificacion, contenido});
                }
            }
        return listaReseñas;
        }
    }

    public

}