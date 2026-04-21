import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton class to manage JDBC connection.
 *
 * JDBC URL format:
 * jdbc:mysql://<host>:<port>/<database>?useSSL=false&serverTimezone=UTC
 *
 * Change DB_USER and DB_PASSWORD to match your MySQL setup.
 */
public class DatabaseConnection {

    // -------------------------------------------------------
    // Connection settings — update these to match your MySQL
    // -------------------------------------------------------
    private static final String DB_URL = "jdbc:mysql://localhost:3306/payroll_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root"; // your MySQL username
    private static final String DB_PASSWORD = "Jaybhakat@1722"; // your MySQL password

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor — loads driver and opens connection
    private DatabaseConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver (required for older JDBC versions)
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Connected to MySQL database: payroll_db");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC Driver not found. Add mysql-connector-j.jar to your project.\n" + e.getMessage());
        }
    }

    /**
     * Returns the single shared instance (creates one if not yet created).
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the active JDBC Connection object.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
