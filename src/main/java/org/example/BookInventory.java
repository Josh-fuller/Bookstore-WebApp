package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
public class BookInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<BookInfo> books = new ArrayList<>();

    public BookInventory() {}

    /**
     * TO add book to the inventory
     * @param book
     */
    public void addBook (BookInfo book){
        if (book != null){
            books.add(book);
        }
    }

    public void removeBook(BookInfo book) {
        books.remove(book);
    }

    public Long getId() {
        return id;
    }

    public List<BookInfo> getBooks(){
        List<BookInfo> sortedBooks = new ArrayList<>(books);
        sortedBooks.sort(Comparator.comparing(BookInfo::getBookTitle, String.CASE_INSENSITIVE_ORDER));
        return sortedBooks;
    }
}
