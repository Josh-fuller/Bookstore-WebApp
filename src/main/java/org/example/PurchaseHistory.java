package org.example;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class PurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "purchased_books",
            joinColumns = @JoinColumn(name = "purchaseHistory_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<BookInfo> books = new LinkedHashSet<>();

    public PurchaseHistory() {}

    public PurchaseHistory(User user) {
        this.user = user;
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<BookInfo> getBooks() {
        return books;
    }

    public void setBooks(Set<BookInfo> books) {
        this.books = books;
    }

    public void addBook(BookInfo book) {
        this.books.add(book);
    }

    public void removeBook(BookInfo book) {
        this.books.remove(book);
    }
}
