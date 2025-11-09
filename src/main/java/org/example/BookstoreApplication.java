package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookstoreApplication {

    @Autowired
    private BookInventoryRepository inventoryRepository;

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);

        System.out.println("LAUNCH APPLICATION HERE!!!");
        System.out.println("http://localhost:8080/");
    }
}