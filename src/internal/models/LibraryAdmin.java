package internal.models;

import internal.services.LibraryServices;

public class LibraryAdmin implements User, AdminActions {
    private final int userId;
    private final String name;
    private final LibraryServices libraryServices;

    public LibraryAdmin(int userId, String name, LibraryServices libraryServices) {
        this.userId = userId;
        this.name = name;
        this.libraryServices = libraryServices;
    }

    @Override
    public int getUserId() { return userId; }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isAdmin() { return true; } // Admins have special privileges

    @Override
    public void addBook(String title, String author, String isbn, int stock) {
        libraryServices.addBook(title, author, isbn, stock);
    }

    @Override
    public void updateBook(int bookId, String title, int stock) {
        libraryServices.updateBook(bookId, title, stock);
    }

    @Override
    public void deleteBook(int bookId) {
        libraryServices.deleteBook(bookId);
    }

    @Override
    public void deleteUser(int userId) {
        libraryServices.deleteUser(userId);
    }

    @Override
    public void displayUsers() {
        libraryServices.displayUsers();
    }

    public void displayBooks() {
        libraryServices.displayBooks();
    }

    public void displayTransactions() {
        libraryServices.displayTransactions();
    }
}
