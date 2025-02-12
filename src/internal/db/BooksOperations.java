package internal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BooksOperations implements DBOperations {

    @Override
    public void insertRow(Connection conn, String tableName, Object... values) {
        try {
            String title = (String) values[0];
            String author = (String) values[1];
            String isbn = (String) values[2];
            int stock = (int) values[3];

            // Check if the book already exists
            String checkQuery = "SELECT id FROM " + tableName + " WHERE isbn = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, isbn);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Error: Book with ISBN " + isbn + " already exists.");
                return;
            }

            // Insert new book
            String insertQuery = "INSERT INTO " + tableName + " (title, author, isbn, stock, availablecopies) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, title);
            insertStmt.setString(2, author);
            insertStmt.setString(3, isbn);
            insertStmt.setInt(4, stock);
            insertStmt.setInt(5, stock); // Initially all copies are available
            insertStmt.executeUpdate();

            System.out.println("New book added: " + title + " (" + stock + " copies).");
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

            // Check current stock and available copies
            String checkQuery = "SELECT stock, availablecopies FROM " + tableName + " WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, id);
            ResultSet checkRs = checkStmt.executeQuery();

            if (!checkRs.next()) {
                System.out.println("Error: Book ID " + id + " not found.");
                return;
            }

            int currentStock = checkRs.getInt("stock");
            int availableCopies = checkRs.getInt("availablecopies");

            // Handle stock update carefully
            if (filteredUpdates.containsKey("stock")) {
                int newStock = (int) filteredUpdates.get("stock");

                if (newStock < (currentStock - availableCopies)) {
                    System.out.println("Error: Cannot reduce stock below borrowed copies.");
                    return;
                }
            }

            // Build dynamic update query
            StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
            boolean first = true;

            for (String column : filteredUpdates.keySet()) {
                if (!first) query.append(", ");
                query.append(column).append(" = ?");
                first = false;
            }
            query.append(" WHERE id = ?");

            PreparedStatement pstmt = conn.prepareStatement(query.toString());

            int index = 1;
            for (Object value : filteredUpdates.values()) {
                pstmt.setObject(index++, value);
            }
            pstmt.setInt(index, id);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Book ID " + id + " updated successfully.");
            } else {
                System.out.println("Book ID " + id + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRow(Connection conn, String tableName, int id) {
        try {
            String query = "DELETE FROM " + tableName + " WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Book deleted");
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
                System.out.println("ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") +
                        ", Author: " + rs.getString("author") + ", ISBN: " + rs.getString("isbn") +
                        ", Stock: " + rs.getInt("stock") + ", Available: " + rs.getInt("availablecopies"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
