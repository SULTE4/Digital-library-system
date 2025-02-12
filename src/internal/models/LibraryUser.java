package internal.models;

import internal.services.LibraryServices;

public class LibraryUser implements User {
    private final int userId;
    private final String name;
    private final LibraryServices libraryServices;

    public LibraryUser(int userId, String name, LibraryServices libraryServices) {
        this.userId = userId;
        this.name = name;
        this.libraryServices = libraryServices;
    }

    @Override
    public int getUserId() { return userId; }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isAdmin() { return false; } // Regular users are NOT admins

    public void borrowBook(int bookId) {
        libraryServices.borrowBook(userId, bookId);
    }

    public void returnBook(int bookId) {
        libraryServices.returnBook(userId, bookId);
    }

    @Override
    public String toString() {
        return "Name: " + this.name + ", UserID: " + this.userId + ", Admin: false";
    }
}
