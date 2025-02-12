import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class DBfunc {
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
            // Define table structures dynamically
            Map<String, String> tableSchemas = new HashMap<>();
            tableSchemas.put("users", "empid SERIAL PRIMARY KEY, name VARCHAR(255), status VARCHAR(50)");
            tableSchemas.put("books", "id SERIAL PRIMARY KEY, title VARCHAR(255), author VARCHAR(255), isbn VARCHAR(50), isAvailable VARCHAR(255)");

            // Check if table structure is defined
            if (!tableSchemas.containsKey(tableName)) {
                System.out.println("Table structure for " + tableName + " is not defined.");
                System.out.println("Only permitted tables like \"users\" and \"books\"");
                return;
            }

            // Construct SQL query
            String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableSchemas.get(tableName) + ")";

            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table '" + tableName + "' created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertRow(Connection conn, String tableName, Object... values) {
        try {
            // Define table structures dynamically
            Map<String, String> tableSchemas = new HashMap<>();
            tableSchemas.put("users", "(name, status) VALUES (?, ?)");
            tableSchemas.put("books", "(title, author, isbn, isAvailable) VALUES (?, ?, ?, ?)");

            // Define unique keys for checking duplicates
            Map<String, String> uniqueKeys = new HashMap<>();
            uniqueKeys.put("users", "name");
            uniqueKeys.put("books", "isbn");

            // Validate table name
            if (!tableSchemas.containsKey(tableName)) {
                System.out.println("Invalid table: " + tableName);
                return;
            }

            String uniqueColumn = uniqueKeys.get(tableName);

            // Check if the record already exists
            String checkQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE " + uniqueColumn + " = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setObject(1, values[tableName.equals("books") ? 2 : 0]); // ISBN for books, name for users
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Record already exists in table: " + tableName);
                return;
            }

            // Construct and execute the insert query
            String query = "INSERT INTO " + tableName + " " + tableSchemas.get(tableName);
            PreparedStatement pstmt = conn.prepareStatement(query);

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Row inserted into table: " + tableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readData(Connection conn, String tableName) {
        Statement statement;
        ResultSet rs = null;
        try {
            String query = "SELECT * FROM " + tableName;
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                System.out.print(rs.getString("empid") + " ");
                System.out.println(rs.getString("name"));
            }
            System.out.println("Data read");
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void updateData(Connection conn, String tableName, int id, Map<String, Object> updates) {
        try {
            // Define primary keys and column names dynamically
            Map<String, String> primaryKeys = new HashMap<>();
            primaryKeys.put("users", "empid");
            primaryKeys.put("books", "id");

            // Validate table
            if (!primaryKeys.containsKey(tableName)) {
                System.out.println("Invalid table: " + tableName);
                return;
            }

            String primaryKey = primaryKeys.get(tableName);

            // Fetch current data
            String selectQuery = "SELECT * FROM " + tableName + " WHERE " + primaryKey + " = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No record found with ID: " + id);
                return;
            }

            // Check for changes
            boolean changesDetected = false;
            for (String column : updates.keySet()) {
                Object newValue = updates.get(column);
                Object currentValue = rs.getObject(column);

                if (newValue != null && !newValue.equals(currentValue)) {
                    changesDetected = true;
                    break;
                }
            }

            if (!changesDetected) {
                System.out.println("No changes detected for ID: " + id);
                return;
            }

            // Build dynamic SQL update query
            StringBuilder queryBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
            boolean first = true;

            for (String column : updates.keySet()) {
                if (!first) queryBuilder.append(", ");
                queryBuilder.append(column).append(" = ?");
                first = false;
            }

            queryBuilder.append(" WHERE ").append(primaryKey).append(" = ?");

            // Prepare update statement
            PreparedStatement updateStmt = conn.prepareStatement(queryBuilder.toString());

            int paramIndex = 1;
            for (String column : updates.keySet()) {
                updateStmt.setObject(paramIndex++, updates.get(column));
            }
            updateStmt.setInt(paramIndex, id);

            // Execute update
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Record ID " + id + " updated successfully.");
            } else {
                System.out.println("Failed to update record ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}