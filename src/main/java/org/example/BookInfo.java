package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BookInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookISBN;
    private String bookTitle;
    private String bookGenres;
    private String bookAuthor;
    private String bookPublisher;

    @Column(length = 2000)
    private String bookDescription;

    private Double bookPrice;
    private int inventory = 5; //Added a default value to update the db
    @Column(length = 2000)
    private String bookCategory;
    @Column(length = 2000)
    private String bookCoverURL; // full URL or relative filename

    // Constructors
    public BookInfo() {}

    public BookInfo(String bookTitle, String bookGenre, Double bookPrice, String bookISBN, String bookAuthor, String bookPublisher,
                    String bookDescription, String bookCoverURL) {
        this.bookISBN = bookISBN;
        this.bookTitle = bookTitle;
        this.bookGenres = bookGenre;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.bookDescription = bookDescription;
        this.bookPrice = bookPrice;
        this.bookCoverURL = bookCoverURL;
    }

    //ALL Getters
    public String getBookISBN(){
        return bookISBN;
    }
    public String getBookTitle(){
        return bookTitle;
    }
    public String getBookGenre(){
        return bookGenres;
    }
    public String getBookAuthor(){
        return bookAuthor;
    }
    public String getBookPublisher(){
        return bookPublisher;
    }
    public String getBookDescription(){
        return bookDescription;
    }
    public Double getBookPrice(){
        return bookPrice;
    }
    public String getBookCoverURL(){
        return bookCoverURL;
    }
    public Long getId(){ return id; }
    public int getInventory() {return inventory;}


    //ALL setters IDK if well need them
    public void setBookISBN(String bookISBN){
        this.bookISBN = bookISBN;
    }
    public void setBookTitle(String bookTitle){
        this.bookTitle = bookTitle;
    }
    public void setBookGenre(String bookGenre){
        this.bookGenres = bookGenre;
    }
    public void setBookAuthor(String bookAuthor){ this.bookAuthor = bookAuthor; }
    public void setBookPublisher(String bookPublisher){
        this.bookPublisher = bookPublisher;
    }
    public void setBookDescription(String bookDescription){
        this.bookDescription =  bookDescription;
    }
    public void setBookPrice(Double bookPrice){
        this.bookPrice = bookPrice;
    }
    public void setBookCoverURL(String bookCoverURL){
        this.bookCoverURL = bookCoverURL;
    }
    public void setInventory(int inventory) {this.inventory = inventory;}


    public boolean hasStock(int quantity) {return inventory >= quantity;}
    public void decreaseStock(int quantity) {this.inventory -= quantity;}

}
