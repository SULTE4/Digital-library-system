package handling;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<handling.Book> books;
    private List<LibraryUser> users;
    private List<Transaction> transactions;

    public Library() {
        books = new ArrayList<>();
        users = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public void addBook(handling.Book book) {
        books.add(book);
    }

    public void addUser(LibraryUser user) {
        users.add(user);
    }

    public void borrowBook(String isbn, String userId) {
        handling.Book bookToBorrow = null;
        for (handling.Book book : books) {
            if (book.getIsbn().equals(isbn) && book.isAvailable()) {
                bookToBorrow = book;
                break;
            }
        }

        if (bookToBorrow == null) {
            System.out.println("Book is not available or doesn't exist.");
            return;
        }

        LibraryUser user = null;
        for (LibraryUser u : users) {
            if (u.getUserId().equals(userId)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        bookToBorrow.borrowBook();
        Transaction transaction = new Transaction(user, bookToBorrow);
        transactions.add(transaction);
        System.out.println("Book borrowed successfully. Due date: " + transaction.getDueDate());
    }

    public void returnBook(String isbn) {
        Transaction transactionToRemove = null;
        for (Transaction transaction : transactions) {
            if (transaction.getBook().getIsbn().equals(isbn)) {
                transaction.getBook().returnBook();
                transactionToRemove = transaction;
                break;
            }
        }

        if (transactionToRemove != null) {
            transactions.remove(transactionToRemove);
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Transaction not found.");
        }
    }

    public void displayBooks() {
        for (handling.Book book : books) {
            book.displayBook();
        }
    }

    public void displayTransactions() {
        for (Transaction transaction : transactions) {
            transaction.displayTransaction();
        }
    }
}
