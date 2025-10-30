package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BookInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // FOR PRIMARY KEY

    private String bookName;
    private String bookGenre;
    private String bookPrice;
    private String bookISBN;

    //Maybe we add a additional String(s) for the authors name and a short text summary

    //Not 100% sure why but this needs to be here to work or else IntelliJ complains
    public BookInfo(){
        //Class 'BookInfo' should have [public, protected] no-arg constructor
    }

    //Constructor
    public BookInfo(String bookName, String bookGenre, String bookPrice, String bookISBN){
        this.bookName = bookName;
        this.bookGenre = bookGenre;
        this.bookPrice = bookPrice;
        this.bookISBN = bookISBN;
    }

    //ALL Getters
    public String getBookName(){
        return bookName;
    }
    public String getBookGenre(){
        return bookGenre;
    }
    public String getBookPrice(){
        return bookPrice;
    }
    public String getBookISBN(){
        return bookISBN;
    }

    //ALL setters IDK if well need them
    public void setBookName(String bookName){
        this.bookName = bookName;
    }

    public void setGenre(String bookGenre){
        this.bookGenre = bookGenre;
    }

    public void setPrice(String bookPrice){
        this.bookPrice = bookPrice;
    }
    public void setISBN(String bookISBN){
        this.bookISBN = bookISBN;
    }

}
