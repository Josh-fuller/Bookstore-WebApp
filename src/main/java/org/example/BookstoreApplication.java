package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookstoreApplication implements CommandLineRunner {

    @Autowired
    private BookInventoryRepository inventoryRepository;

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
        System.out.println("LAUNCH APPLICATION HERE!!!");
        System.out.println("http://localhost:8080/");
    }

    // Just to add some test books before running so its not empty
    @Override
    public void run(String... args) {
        if (inventoryRepository.count() == 0) {
            BookInventory inventory = new BookInventory();
            inventory.addBook(new BookInfo("The Little Prince", "Fable, Children's literature, Novella, Fantasy", "$19.99", "978-0-00-862348-7"));
            inventory.addBook(new BookInfo("Treasure Island", "Adventure, Children's literature", "$10.99", "978-0-141-32100-4"));
            inventoryRepository.save(inventory);
        }
    }
}