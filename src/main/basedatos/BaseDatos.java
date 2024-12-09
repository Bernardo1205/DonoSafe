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
    // Insertar valores de usuario enviados desde servidor y guardados en la base de datos.
    public void insertarUsuario(String nombre, String username, String sexo, String edad, String contraseña, String reseñas, String fecha_registro, String email, String descripcion) {
        String comandoInsertarUsuario = "INSERT INTO Usuario VALUES ('autoincrement',?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(2, nombre);
            pstmt.setString(3, username);
            pstmt.setString(4, sexo);
            int edadParseada = Integer.parseInt(edad);
            pstmt.setInt(5, edadParseada);
            pstmt.setString(6,contraseña);
            pstmt.setString(7, reseñas);
            pstmt.setString(8,fecha_registro);
            pstmt.setString(9, email);
            pstmt.setString(10, descripcion);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // Insertar reporte
    public void insertarReporte(double longitud, double latitud, String tipoPeligro, String horaCreacion,
                                int idUsuario, String descripcionReporte, String horaDelReporte ) {
        String comandoInsertarUsuario = "INSERT INTO Reporte VALUES ('autoincrement',?,?,?,?,?,?)";
        Statement statement = null;
        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarUsuario, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(2, idUsuario);

            pstmt.setString(3,descripcionReporte);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //String hora_formateada = hora_reporte.format(formatter);
            pstmt.setString(4, horaDelReporte);


            //LocalDateTime fecha_reporte_creacion = horaCreacion;
            //String fecha_formateada = fecha_reporte_creacion.format(formatter);
            pstmt.setString(5, horaCreacion);

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
    //Insertar Mensaje
    public void insertarMensaje(String groupId, int id_usuario, String fecha, String contenido) {
        //int grupoId, String username/id, String mensaje, String fecha

        //Como mensaje no tiene id_mensaje en el primer interrogante no va 'autoincrement'
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
    // Insertar grupo
    public void insertarGrupo(String usuarios, String destino, String origen, String fecha) {
        String comandoInsertarGrupo = "INSERT INTO Grupo VALUES ('autoincrement',?,?,?,?)";
        try (PreparedStatement pstmt = con.prepareStatement(comandoInsertarGrupo, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(2, usuarios);
            pstmt.setString(3, destino);
            pstmt.setString(4, origen);
            pstmt.setString(5, fecha);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Insertar Reseña
    public void insertarReseña(String contenidoReseña, int calificacion, String fecha, int idReseñador, int idUsuario) {
        String comandoInsertarGrupo = "INSERT INTO Reseña VALUES ('autoincrement',?,?,?,?,?)";
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

    //Mostrar Reseña
    public ArrayList<String[]> mostarReseñas(int idUsuario) throws SQLException {

        String comandoObtenerReseñas = "SELECT contenido_reseña, calificacion, id_reseñador, fecha FROM Reseña where id_usuario = ?;";
        //Inicialización reseñas
        ArrayList<String[]> listaReseñas = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(comandoObtenerReseñas)) {

            // Establecer el parámetro id_usuario en la consulta
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
        //Devolver el array con la fecha, calificacion, contenido
        return listaReseñas;
    }


    public HashMap<Integer, String[]> getMensajesUsuario(int id_grupo, int id_usuario) {
        HashMap<Integer, String[]> mensajes = new HashMap<>(); // HashMap para almacenar mensajes que ha enviado un usuario en un grupo concreto

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
                    mensajes.put(grupoId, new String[]{fecha, texto});
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        // Devolver arraylist de mensajes enviados por un usuario en un grupo concreto
        return mensajes;
    }

    public HashMap<Integer, HashMap<Integer, String[]>> getMensajes() {
        HashMap<Integer, HashMap<Integer, String[]>> mensajes = new HashMap<>();
        // HashMap para almacenar todos los mensajes enviados por todos los usuarios en todos los grupos

        String sql = "SELECT grupo_id, id_usuario_transmisor, fecha_hora, contenido FROM Mensaje";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int grupoId = rs.getInt("grupo_id");
                int userId = rs.getInt("id_usuario_transmisor");
                String texto = rs.getString("contenido");
                String fecha = rs.getString("fecha_hora");

                // Solo procesar los mensajes que coincidan con el id_usuario y id_grupo dados
                //Primera clave id_usuario. Comprobar si ya existe, si no existe crear la clave con el primer mensaje. Si ya existe agregar mensaje.



                //Segunda clave id_grupo. Comprobar si existe en HashMap; si no existe crear clave y agregar mensaje. Si existe agregar mensaje directamente.
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        // Devolver arraylist de mensajes enviados por un usuario en un grupo concreto
        return mensajes;
    }

    public ArrayList<String[]> getGruposUsuario(int id_usuario){
        //Función para obtener los grupos en que participa un usuario en concreto
        ArrayList<String[]> grupos = new ArrayList<>();
        String sql = "SELECT id_grupo, usuarios, destino, inicio, fecha_creacion FROM Grupo ORDER BY fecha_hora ASC";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                int grupoId = rs.getInt("id_grupo");
                String grupoIdParseado = String.valueOf(grupoId);
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
                    grupos.add(new String[]{grupoIdParseado, usuarios, destino, inicio, fecha});
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }

        return grupos;
    }

    public ArrayList<String[]> getReporte() {
        String consulta = "SELECT id_reporte, id_usuario, descripcion, hora, fecha_creacion, fecha_fin, Tipo_peligro FROM Reporte";
        ArrayList<String[]> reportes = new ArrayList<>();

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {

            LocalDateTime hora_consulta = LocalDateTime.now();

            while (rs.next()) { // Asegurarse de recorrer cada fila del ResultSet
                String fecha_fin = rs.getString("fecha_fin");
                LocalDateTime fecha_fin_parseada = LocalDateTime.parse(fecha_fin);

                if (fecha_fin_parseada.isBefore(hora_consulta)) { // Comprobar si ya caducó
                    System.out.println("El reporte ha caducado");
                } else {
                    // Añadir los datos relevantes al ArrayList
                    reportes.add(new String[]{
                            rs.getString("id_reporte"),
                            rs.getString("id_usuario"),
                            rs.getString("descripcion"),
                            rs.getString("hora"),
                            rs.getString("fecha_creacion"),
                            rs.getString("fecha_fin"),
                            rs.getString("Tipo_peligro")
                    });
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al ejecutar la consulta: " + e.getMessage(), e);
        }
        return reportes;
    }

    public void añadirUsuario(int grupoId, int clienteId) {
        // Query para verificar si el grupo existe
        String verificarGrupo = "SELECT usuarios FROM Grupo WHERE id_grupo = ?";
        // Query para actualizar la lista de usuarios
        String actualizarUsuarios = "UPDATE Grupo SET usuarios = ? WHERE id_grupo = ?";

        try (PreparedStatement verificarStmt = con.prepareStatement(verificarGrupo);
             PreparedStatement actualizarStmt = con.prepareStatement(actualizarUsuarios)) {

            // Verificar si el grupo existe
            verificarStmt.setInt(1, grupoId);
            ResultSet rs = verificarStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("El grupo con id " + grupoId + " no existe en la base de datos.");
                return;
            }

            // Parsear los usuarios a un array de enteros
            String usuariosString = rs.getString("usuarios");
            ArrayList<Integer> idsUsuarioGrupo = new ArrayList<>();
            if (usuariosString != null && !usuariosString.isEmpty()) {
                String[] usuariosArray = usuariosString.split(",");
                for (String usuario : usuariosArray) {
                    idsUsuarioGrupo.add(Integer.parseInt(usuario.trim()));
                }
            }

            // Verificar si el clienteId ya está en el array
            if (idsUsuarioGrupo.contains(clienteId)) {
                System.out.println("El usuario con id " + clienteId + " ya está en el grupo con id " + grupoId);
                return;
            }

            // Añadir el clienteId al array
            idsUsuarioGrupo.add(clienteId);

            // Convertir la lista de usuarios de nuevo a un formato String para actualizar la base de datos
            StringBuilder usuariosActualizados = new StringBuilder();
            for (int id : idsUsuarioGrupo) {
                if (usuariosActualizados.length() > 0) {
                    usuariosActualizados.append(",");
                }
                usuariosActualizados.append(id);
            }

            // Actualizar la base de datos
            actualizarStmt.setString(1, usuariosActualizados.toString());
            actualizarStmt.setInt(2, grupoId);
            actualizarStmt.executeUpdate();

            System.out.println("El usuario con id " + clienteId + " ha sido añadido al grupo con id " + grupoId);

        } catch (SQLException e) {
            throw new RuntimeException("Error al añadir el usuario al grupo: " + e.getMessage(), e);
        }
    }

    public void eliminarUsuarioGrupo(int grupoId, int clienteId) {
        // Query para verificar si el grupo existe
        String verificarGrupo = "SELECT usuarios FROM Grupo WHERE id_grupo = ?";
        // Query para actualizar la lista de usuarios
        String actualizarUsuarios = "UPDATE Grupo SET usuarios = ? WHERE id_grupo = ?";

        try (PreparedStatement verificarStmt = con.prepareStatement(verificarGrupo);
             PreparedStatement actualizarStmt = con.prepareStatement(actualizarUsuarios)) {

            // Verificar si el grupo existe
            verificarStmt.setInt(1, grupoId);
            ResultSet rs = verificarStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("El grupo con id " + grupoId + " no existe en la base de datos.");
                return;
            }

            // Parsear los usuarios a un array de enteros
            String usuariosString = rs.getString("usuarios");
            if (usuariosString == null || usuariosString.isEmpty()) {
                System.out.println("El grupo no tiene usuarios registrados.");
                return;
            }

            String[] usuariosArray = usuariosString.split(",");
            ArrayList<Integer> idsUsuarioGrupo = new ArrayList<>();
            for (String usuario : usuariosArray) {
                idsUsuarioGrupo.add(Integer.parseInt(usuario.trim()));
            }

            // Verificar si el clienteId está en el array
            if (!idsUsuarioGrupo.contains(clienteId)) {
                System.out.println("El usuario con id " + clienteId + " no está en el grupo con id " + grupoId);
                return;
            }

            // Eliminar el clienteId del array
            idsUsuarioGrupo.remove((Integer) clienteId);

            // Convertir la lista de usuarios de nuevo a un formato String para actualizar la base de datos
            StringBuilder usuariosActualizados = new StringBuilder();
            for (int id : idsUsuarioGrupo) {
                if (usuariosActualizados.length() > 0) {
                    usuariosActualizados.append(",");
                }
                usuariosActualizados.append(id);
            }

            // Actualizar la base de datos
            actualizarStmt.setString(1, usuariosActualizados.toString());
            actualizarStmt.setInt(2, grupoId);
            actualizarStmt.executeUpdate();

            System.out.println("El usuario con id " + clienteId + " ha sido eliminado del grupo con id " + grupoId);

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el usuario del grupo: " + e.getMessage(), e);
        }
    }
    // Funcion para recoger todos los mensajes.
    //Hashmap con id_usuario: id_grupo: contenido mensaje. Y así sucesivamente.


}
