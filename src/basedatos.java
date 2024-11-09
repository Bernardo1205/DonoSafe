import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class basedatos {
    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlite:C:/Users/lucas.stille/basedatos.db";
        ResultSet resultSet = null;
        try (Connection connection = DriverManager.getConnection(connectionUrl);) {
            Statement statement = connection.createStatement(); {

                // Create and execute a SELECT SQL statement.
                String selectSql = "SELECT * from Usuario";
                resultSet = statement.executeQuery(selectSql);

                // Print results from select statement
                while (resultSet.next()) {
                    System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
                }
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}




