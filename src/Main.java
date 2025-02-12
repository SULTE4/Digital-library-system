import java.sql.Connection;
import internal.utils.DBConnection;
import internal.db.DBOperations;
import internal.db.UsersOperations;
import internal.db.BooksOperations;
import internal.db.TransactionOperations;
import internal.models.LibraryUser;
import internal.models.LibraryAdmin;
import internal.services.Library;
import internal.services.LibraryServices;

public class Main {
    public static void main(String[] args) {
        // Establish database connection
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection("Data", "postgres", "1256");

        if (conn == null) {
            System.out.println("Connection failed. Cannot perform database operations.");
            return;
        }

        dbConnection.deleteTable(conn, "books");
        dbConnection.deleteTable(conn, "users");
        dbConnection.deleteTable(conn, "transactions");

        // Create tables
        dbConnection.createTable(conn, "users");
        dbConnection.createTable(conn, "books");
        dbConnection.createTable(conn, "transactions");

        // Initialize database operations
        DBOperations userOperations = new UsersOperations();
        DBOperations bookOperations = new BooksOperations();
        TransactionOperations transactionOps = new TransactionOperations();

        // Create LibraryServices instance
        LibraryServices libraryServices = new Library(conn, bookOperations, userOperations, transactionOps);

        // Create admin and regular users
        LibraryAdmin admin = new LibraryAdmin(1, "AdminUser", libraryServices);
        LibraryUser user1 = new LibraryUser(2, "Yertugan", libraryServices);
        LibraryUser user2 = new LibraryUser(3, "Tamerlan", libraryServices);

        admin.addBook("1984", "George Orwell", "01", 5);
        user1.borrowBook(1);
        user1.returnBook(1);
        admin.displayBooks();
    }
}
