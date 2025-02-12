package internal.models;

public interface AdminActions {
    void addBook(String title, String author, String isbn, int stock);
    void updateBook(int bookId, String title, int stock);
    void deleteBook(int bookId);
    void deleteUser(int userId);
    void displayUsers();
}
