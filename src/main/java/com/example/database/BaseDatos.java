package main.java.com.example.database;

import main.java.com.example.client.Client;
import main.java.com.example.mapa.Reporte;
import main.java.com.example.server.Grupo;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

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

        String url = "jdbc:sqlite:src/db/basedatos.db";

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

    public void insertarUsuario(String nombre, String username, String sexo, String edad, String contraseña, String reseñas, String fecha_registro, String email, String descripcion) {
        String comandoInsertarUsuario = "INSERT INTO Usuario VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(2, nombre);
            pstmt.setString(3, username);
            pstmt.setString(4, sexo);
            int edadParseada = Integer.parseInt(edad);
            pstmt.setInt(5, edadParseada);
            pstmt.setString(6,contraseña);
//            pstmt.setArray(7, reseñas);
            pstmt.setString(7, reseñas);
            pstmt.setString(8,fecha_registro);
            pstmt.setString(9, email);
            pstmt.setString(10, descripcion);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertarWayPoint(Reporte reporte) {
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
            pstmt.setArray(2, (Array) grupo.getClientes());
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

    public String getUsuarios() {
        String comandoObtenerUsuarios = "SELECT * FROM Usuario";
        try (Statement stmt = con.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(comandoObtenerUsuarios)) {
                while (rs.next()) {
                    String info = String.join(",",
                            String.valueOf(rs.getInt("id_usuario")),
                            rs.getString("nombre"),
                            rs.getString("nombre_usuario"),
                            rs.getString("sexo"),
                            String.valueOf(rs.getInt("edad")),
                            rs.getString("contraseña"),
                            rs.getString("reseña"),
                            rs.getString("fecha_registro"),
                            rs.getString("reseña"),
                            rs.getString("email"),
                            rs.getString("descripcion"));
                    System.out.println(info);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

//    public boolean getAuth(String username, String password) {
//        String comandoObtenerUsuario = String.format("SELECT * FROM Usuario WHERE nombre_usuario = \"%s\" AND contraseña = \"%s\"", username, password);
//        try (Statement stmt = con.createStatement()) {
//            try (ResultSet rs = stmt.executeQuery(comandoObtenerUsuario)) {
//                return rs.next();
//            }
//        } catch (SQLException e) {
//            return false;
//        }
//
//    }

    /**
     * Devuelve la información del usuario
     * @param userInfo Array con el nombre de usuario y la contraseña
     * @return
     */
    public String getUsuario(String username, String password) {
        String comandoObtenerUsuario = String.format("SELECT * FROM Usuario WHERE nombre_usuario = \"%s\" AND contraseña = \"%s\"", username, password);
        try (Statement stmt = con.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(comandoObtenerUsuario)) {
                if (rs.next()) {
                    String info = String.join(",",
                            String.valueOf(rs.getInt("id_usuario")),
                            rs.getString("nombre"),
                            rs.getString("nombre_usuario"),
                            rs.getString("sexo"),
                            String.valueOf(rs.getInt("edad")),
                            rs.getString("contraseña"),
                            rs.getString("reseña"),
                            rs.getString("fecha_registro"),
                            rs.getString("email"),
                            rs.getString("descripcion"));
                    return info;
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
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
}