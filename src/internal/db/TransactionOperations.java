package internal.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionOperations {

    public void borrowBook(Connection conn, int userId, int bookId) {
        PreparedStatement checkStmt = null, updateStmt = null, transactionStmt = null;
        ResultSet rs = null;

        try {
            conn.setAutoCommit(false); // Start transaction

            // Check book availability
            String checkQuery = "SELECT availablecopies FROM books WHERE id = ?";
            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, bookId);
            rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Error: Book ID " + bookId + " does not exist.");
                conn.rollback();
                return;
            }

            int availableCopies = rs.getInt("availablecopies");
            if (availableCopies <= 0) {
                System.out.println("Error: No available copies for book ID " + bookId);
                conn.rollback();
                return;
            }

            // Decrease availableCopies
            String updateQuery = "UPDATE books SET availablecopies = availablecopies - 1 WHERE id = ?";
            updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, bookId);
            updateStmt.executeUpdate();

            // Insert into transactions (only active borrowings)
            String transactionQuery = "INSERT INTO transactions (user_id, book_id, transaction_date) VALUES (?, ?, NOW())";
            transactionStmt = conn.prepareStatement(transactionQuery);
            transactionStmt.setInt(1, userId);
            transactionStmt.setInt(2, bookId);
            transactionStmt.executeUpdate();

            conn.commit(); // Commit transaction
            System.out.println("Success: User " + userId + " borrowed book ID " + bookId);
        } catch (Exception e) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back due to error.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            closeResources(rs, checkStmt, updateStmt, transactionStmt);
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void returnBook(Connection conn, int userId, int bookId) {
        PreparedStatement checkStmt = null, updateStmt = null, deleteStmt = null;
        ResultSet rs = null;

        try {
            conn.setAutoCommit(false); // Start transaction

            // Check if user has borrowed this book
            String checkQuery = "SELECT COUNT(*) FROM transactions WHERE user_id = ? AND book_id = ?";
            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, bookId);
            rs = checkStmt.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                System.out.println("Error: User " + userId + " has not borrowed book ID " + bookId);
                conn.rollback();
                return;
            }

            // Increase availableCopies
            String updateQuery = "UPDATE books SET availablecopies = availablecopies + 1 WHERE id = ?";
            updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, bookId);
            updateStmt.executeUpdate();

            // Delete transaction row (since book is returned)
            String deleteQuery = "DELETE FROM transactions WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND book_id = ? ORDER BY transaction_date LIMIT 1)";
            deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, bookId);
            deleteStmt.executeUpdate();


            conn.commit(); // Commit transaction
            System.out.println("Success: User " + userId + " returned book ID " + bookId);
        } catch (Exception e) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back due to error.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            closeResources(rs, checkStmt, updateStmt, deleteStmt);
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void printTransactions(Connection conn) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT * FROM transactions";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", User ID: " + rs.getInt("user_id") +
                        ", Book ID: " + rs.getInt("book_id") + ", Date: " + rs.getTimestamp("transaction_date"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt);
        }
    }

    // Utility method to close resources
    private void closeResources(ResultSet rs, PreparedStatement... stmts) {
        try {
            if (rs != null) rs.close();
            for (PreparedStatement stmt : stmts) {
                if (stmt != null) stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
