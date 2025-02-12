package internal.services;

import internal.db.DBOperations;
import internal.db.TransactionOperations;
import java.sql.Connection;
import java.util.Map;

public class Library implements LibraryServices {
    private final Connection conn;
    private final DBOperations bookOperations;
    private final DBOperations userOperations;
    private final TransactionOperations transactionOperations;

    public Library(Connection conn, DBOperations bookOperations, DBOperations userOperations, TransactionOperations transactionOperations) {
        this.conn = conn;
        this.bookOperations = bookOperations;
        this.userOperations = userOperations;
        this.transactionOperations = transactionOperations;
    }

    @Override
    public void addBook(String title, String author, String isbn, int stock) {
        bookOperations.insertRow(conn, "books", title, author, isbn, stock);
        System.out.println("✅ Book added: " + title);
    }

    @Override
    public void updateBook(int bookId, String title, int stock) {
        bookOperations.updateData(conn, "books", bookId, Map.of("title", title, "stock", stock));
        System.out.println("🔄 Book updated: " + title);
    }

    @Override
    public void deleteBook(int bookId) {
        bookOperations.deleteRow(conn, "books", bookId);
        System.out.println("❌ Book deleted: ID " + bookId);
    }

    @Override
    public void deleteUser(int userId) {
        userOperations.deleteRow(conn, "users", userId);
        System.out.println("❌ User deleted: ID " + userId);
    }

    @Override
    public void borrowBook(int userId, int bookId) {
        transactionOperations.borrowBook(conn, userId, bookId);
    }

    @Override
    public void returnBook(int userId, int bookId) {
        transactionOperations.returnBook(conn, userId, bookId);
    }

    @Override
    public void displayUsers() {
        System.out.println("👥 All users:");
        userOperations.readData(conn, "users");
    }

    @Override
    public void displayBooks() {
        System.out.println("📚 All books:");
        bookOperations.readData(conn, "books");
    }

    @Override
    public void displayTransactions() {
        System.out.println("📜 All transactions:");
        transactionOperations.printTransactions(conn);
    }
}
