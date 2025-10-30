package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BookInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
        return new ArrayList(books);
    }

}
