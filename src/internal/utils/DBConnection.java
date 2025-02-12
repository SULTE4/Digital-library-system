package internal.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
    public Connection getConnection(String dbname, String user, String pass) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname, user, pass);
            if (conn != null) {
                System.out.println("Connected");
            } else {
                System.out.println("Not Connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void createTable(Connection conn, String tableName) {
        Statement statement;
        try {
            Map<String, String> tableSchemas = new HashMap<>();
            tableSchemas.put("users", "empid SERIAL PRIMARY KEY, name VARCHAR(255), status VARCHAR(50)");
            tableSchemas.put("books", "id SERIAL PRIMARY KEY, title VARCHAR(255), author VARCHAR(255), isbn VARCHAR(50), stock INT, availableCopies INT");
            tableSchemas.put("transactions", "id SERIAL PRIMARY KEY, user_id INT, book_id INT, action VARCHAR(50), transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

            if (!tableSchemas.containsKey(tableName)) {
                System.out.println("Table structure for " + tableName + " is not defined.");
                return;
            }

            String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableSchemas.get(tableName) + ")";
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table '" + tableName + "' created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTable(Connection conn, String tableName) {
        Statement statement;
        try {
            String query = "DROP TABLE IF EXISTS " + tableName + " CASCADE";
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table '" + tableName + "' deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
