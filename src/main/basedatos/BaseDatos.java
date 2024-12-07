package main.basedatos;

import main.java.com.example.client.Client;
import main.java.com.example.client.ClientRegister;
import main.java.com.example.mapa.Reporte;
import main.java.com.example.server.Grupo;

import main.java.com.example.server.ClientHandler;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public void insertarUsuario(String nombre, String username, String sexo, String edad, String contraseña, Array reseñas, String fecha_registro, String email, String descripcion) {
        String comandoInsertarUsuario = "INSERT INTO Usuario VALUES (?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(2, nombre);
            pstmt.setString(3, username);
            pstmt.setString(4, sexo);
            int edadParseada = Integer.parseInt(edad);
            pstmt.setInt(5, edadParseada);
            pstmt.setString(6,contraseña);
            pstmt.setArray(7, reseñas);
            pstmt.setString(8,fecha_registro);
            pstmt.setString(9, email);
            pstmt.setString(10, descripcion);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertarReporte(int id, String descripcionReporte,String horaDelReporte,
                                String horaCreacion, String tipoPeligro) {
        String comandoInsertarUsuario = "INSERT INTO Reporte VALUES (?,?,?,?,?,?,?)";
        Statement statement = null;
        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(2, id);

            pstmt.setString(3,descripcionReporte);

            //LocalDateTime hora_reporte = horaDelReporte;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //String hora_formateada = hora_reporte.format(formatter);
            //pstmt.setString(4, horaDelReporte);


            //LocalDateTime fecha_reporte_creacion = horaCreacion;
            //String fecha_formateada = fecha_reporte_creacion.format(formatter);
            pstmt.setString(5, horaDelReporte);

            LocalDateTime fecha_reporte_creacion = LocalDateTime.parse(horaDelReporte, formatter);
            LocalDateTime fecha_reporte_finalizacion = fecha_reporte_creacion.plusHours(1);
            String fecha_finalizacion_formateada = fecha_reporte_finalizacion.format(formatter);
            pstmt.setString(6, fecha_finalizacion_formateada);

            pstmt.setString(7, tipoPeligro);
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

    public void insertarGrupo(Array clientes, String destino, String origen, String fecha) {
        String comandoInsertarGrupo = "INSERT INTO Grupo VALUES (?,?,?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarGrupo, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setArray(2, clientes);
            pstmt.setString(3, destino);
            pstmt.setString(4, origen);
            pstmt.setString(5, fecha);

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
        }
        return listaReseñas;
    }

    public void eliminarClienteGrupo(Grupo grupo, int clienteId) {
        HashMap<String, ClientHandler> clientes = grupo.getClientes();

        try (Statement stmt = con.createStatement()) {
            String seleccionarUsuarios = "SELECT usuarios FROM Grupo";
            ResultSet rs = stmt.executeQuery(seleccionarUsuarios);
            ArrayList<Integer> idsUsuarioGrupo = new ArrayList<>();
            idsUsuarioGrupo.add(rs.getInt("usuarios"));

            if (idsUsuarioGrupo.contains(clienteId)) {
                idsUsuarioGrupo.remove(clienteId);
            } else {
                System.out.println("Ese usuario no está en el grupo");
            }

            String updateUsuario = "UPDATE Grupo SET usuarios = ?";



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList getMensajesUsuario(int id_grupo, int id_usuario) {
        ArrayList<String[]> mensajes = new ArrayList<>(); // Lista temporal para almacenar mensajes


        String sql = "SELECT grupo_id, id_usuario_transmisor, fecha_hora, contenido FROM Mensaje ORDER BY fecha_hora ASC";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int grupoId = rs.getInt("grupo_id");
                int userId = rs.getInt("id_usuario_transmisor");
                String texto = rs.getString("contenido");
                String fecha = rs.getString("fecha_hora");

                // Solo procesar los mensajes que coincidan con el id_usuario y id_grupo dados
                if (userId == id_usuario && grupoId == id_grupo) {
                    // Añadir un nuevo mensaje como un arreglo {fecha, texto}
                    mensajes.add(new String[]{fecha, texto});
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }

        return mensajes;
    }
    public ArrayList<List> getGrupo(int id_usuario){
        ArrayList<List> grupos = new ArrayList<>();
        String sql = "SELECT id_grupo, usuarios, destino, inicio, fecha_creacion FROM Grupo ORDER BY fecha_hora ASC";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                int grupoId = rs.getInt("id_grupo");
                String usuarios = rs.getString("usuarios");
                String[] arrayUsuarios = usuarios.split(",");
                ArrayList<Integer> arrayIndicesUsuarios = new ArrayList<>();
                for (int i = 0; i < arrayUsuarios.length; i++) {
                    arrayIndicesUsuarios.add(Integer.parseInt(arrayUsuarios[i].trim()));
                }
                String destino = rs.getString("destino");
                String inicio = rs.getString("inicio");
                String fecha = rs.getString("fecha_creacion");

                // Solo procesar los mensajes que coincidan con el id_usuario y id_grupo dados
                if (arrayIndicesUsuarios.contains(id_usuario)) {
                    grupos.add(Arrays.asList(grupoId, arrayIndicesUsuarios, destino, inicio, fecha));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }

        return grupos;
    }

}
