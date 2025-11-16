package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String role = "CUSTOMER"; //CUSTOMER or ADMIN

    @OneToOne(cascade = {CascadeType.ALL, CascadeType.REMOVE})
    @JoinColumn(name = "cart_id")
    private BookInventory inCart;

    @OneToOne(cascade = {CascadeType.ALL, CascadeType.REMOVE})
    @JoinColumn(name = "purchased_id")
    private BookInventory purchasedBooks;



    public User() {
        this.inCart = new BookInventory();
        this.purchasedBooks = new BookInventory();
    }

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;

        this.inCart = new BookInventory();
        this.purchasedBooks = new BookInventory();
    }

    //getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }
    public boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(this.role);
    }
    public BookInventory getInCart() {
        return inCart;
    }

    public void setInCart(BookInventory inCart) {
        this.inCart = inCart;
    }

    public BookInventory getPurchasedBooks() {
        return purchasedBooks;
    }

    public void setPurchasedBooks(BookInventory purchasedBooks) {
        this.purchasedBooks = purchasedBooks;
    }

    public boolean hasInCart(BookInfo book) {
        return inCart != null && inCart.getBooks().contains(book);
    }

    public boolean hasPurchased(BookInfo book) {
        return purchasedBooks != null && purchasedBooks.getBooks().contains(book);
    }


}
