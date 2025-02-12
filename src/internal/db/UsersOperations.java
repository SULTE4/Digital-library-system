package internal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class UsersOperations implements DBOperations {

    @Override
    public void insertRow(Connection conn, String tableName, Object... values) {
        try {
            String name = (String) values[0];
            String status = (String) values[1];

            // Check if user already exists
            String checkQuery = "SELECT COUNT(*) FROM " + tableName + " WHERE name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, name);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                System.out.println("User with name '" + name + "' already exists. Skipping insertion.");
                return;
            }

            // Insert new user if not exists
            String query = "INSERT INTO " + tableName + " (name, status) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, status);
            pstmt.executeUpdate();
            System.out.println("User inserted: " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateData(Connection conn, String tableName, int id, Map<String, Object> updates) {
        try {
            if (updates.isEmpty()) {
                System.out.println("No updates provided.");
                return;
            }

            // Validate columns before updating
            String columnCheckQuery = "SELECT column_name FROM information_schema.columns WHERE table_name = ?";
            PreparedStatement columnCheckStmt = conn.prepareStatement(columnCheckQuery);
            columnCheckStmt.setString(1, tableName);
            ResultSet rs = columnCheckStmt.executeQuery();

            // Store valid columns
            Set<String> validColumns = new HashSet<>();
            while (rs.next()) {
                validColumns.add(rs.getString("column_name"));
            }

            // Filter updates to include only valid columns
            Map<String, Object> filteredUpdates = updates.entrySet().stream()
                    .filter(entry -> validColumns.contains(entry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (filteredUpdates.isEmpty()) {
                System.out.println("No valid columns provided for update.");
                return;
            }

            // Build dynamic update query
            StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
            boolean first = true;

            for (String column : filteredUpdates.keySet()) {
                if (!first) query.append(", ");
                query.append(column).append(" = ?");
                first = false;
            }
            query.append(" WHERE empid = ?");

            PreparedStatement pstmt = conn.prepareStatement(query.toString());

            int index = 1;
            for (Object value : filteredUpdates.values()) {
                pstmt.setObject(index++, value);
            }
            pstmt.setInt(index, id);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User ID " + id + " updated successfully.");
            } else {
                System.out.println("User ID " + id + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRow(Connection conn, String tableName, int id) {
        try {
            String query = "DELETE FROM " + tableName + " WHERE empid = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User ID " + id + " deleted.");
            } else {
                System.out.println("User ID " + id + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readData(Connection conn, String tableName) {
        try {
            String query = "SELECT * FROM " + tableName;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("empid") + ", Name: " + rs.getString("name") + ", Status: " + rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
