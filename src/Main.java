public class Main {
    public static void main(String[] args) {
        handling.Library library = new handling.Library();


        library.addBook(new handling.Book("1984", "George Orwell", "00001"));
        library.addBook(new handling.Book("The Great Gatsby", "F. Scott Fitzgerald", "00002"));


        library.addUser(new handling.LibraryUser("Alice", "U001"));
        library.addUser(new handling.LibraryUser("Bob", "U002"));


        System.out.println("\n--- Borrowing Book ---");
        library.borrowBook("00001", "U001");
        library.displayBooks();
        library.displayTransactions();


        System.out.println("\n--- Returning Book ---");
        library.returnBook("00001");
        library.displayBooks();
        library.displayTransactions();
    }
}

