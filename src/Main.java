public class Main {
    public static void main(String[] args) {
        handling.Library library = new handling.Library();
        library.loadBooksFromFile("C:/Users/SULTE4/IdeaProjects/Digital-library-system//src/books.txt");

        library.addBook(new handling.Book("1984", "George Orwell", "00001"));
        library.addBook(new handling.Book("The Great Gatsby", "F. Scott Fitzgerald", "00002"));

        library.loadUsersFromFile("C:/Users/SULTE4/IdeaProjects/Digital-library-system/src/people_data.txt");
        library.addUser(new handling.LibraryUser("Alice", "U001"));
        library.addUser(new handling.LibraryUser("Bob", "U002"));

        library.displayUsers();

        System.out.println("\n--- Borrowing Book ---");
        library.borrowBook("00001", "U001");
        library.displayTransactions();


        /*
        System.out.println("\n--- Borrowed Books ---");
        library.displayBorrowedBooks();
*/

        System.out.println("\n--- Returning Book ---");
        library.returnBook("00001");
        library.displayTransactions();

        //library.displayBooks();
    }
}

