import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class basedatos {

    // URL de conexión a la base de datos SQLite
    private static final String CONNECTION_URL = "jdbc:sqlite:C:/Users/lucas.stille/basedatos.db";

    // Método para obtener una conexión a la base de datos
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL);
    }

    // Método para insertar un usuario en la base de datos
    public static void insertarUsuario(String nombre, int edad) {
        String sql = "INSERT INTO usuarios (id_usuario, nombre, nombre_usuario, sexo, edad, contraseña, reseña, fecha_registro, email, descripcion) VALUES (?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Asignar valores a los parámetros del PreparedStatement
            pstmt.setString(1, nombre);  // Primer parámetro (nombre)
            pstmt.setInt(2, edad);       // Segundo parámetro (edad)
            pstmt.set
            // Ejecutar la inserción
            pstmt.executeUpdate();
            System.out.println("Usuario insertado exitosamente: " + nombre + ", " + edad);
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Llamada al método para insertar usuarios de ejemplo
        insertarUsuario("Maria Gomez", 25);
        insertarUsuario("Carlos Rivera", 34);
    }
}
