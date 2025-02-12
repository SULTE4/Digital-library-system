package internal.services;

public interface LibraryServices {
    void addBook(String title, String author, String isbn, int stock);
    void updateBook(int bookId, String title, int stock);
    void deleteBook(int bookId);
    void deleteUser(int userId);
    void borrowBook(int userId, int bookId);
    void returnBook(int userId, int bookId);
    void displayUsers();
    void displayBooks();
    void displayTransactions();
}
