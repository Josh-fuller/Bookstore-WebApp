package org.example;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "genres")
    private Set<BookInfo> books = new HashSet<>();

    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }

    //getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<BookInfo> getBooks() {
        return books;
    }

    public void setBooks(Set<BookInfo> books) {
        this.books = books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre genre = (Genre) o;
        return name != null && name.equalsIgnoreCase(genre.name);
    }

    @Override
    public String toString() {
        return name;
    }
}