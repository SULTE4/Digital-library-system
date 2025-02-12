package internal;

import internal.models.Book;

import java.util.Date;
//NO NEED
public class Transaction {
    private internal.models.LibraryUser user;
    private Book book;
    private Date borrowDate;
    private Date dueDate;

    public Transaction(internal.models.LibraryUser user, Book book) {
        this.user = user;
        this.book = book;
        this.borrowDate = new Date();
        this.dueDate = new Date(borrowDate.getTime() + 14L * 24 * 60 * 60 * 1000); // 14 days due
    }

    public Book getBook() {
        return book;
    }

    public internal.models.LibraryUser getUser() {
        return user;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void displayTransaction() {
        System.out.println("User: " + user.getName() + ", Book: " + book.getTitle() + ", Due Date: " + dueDate);
    }
}
