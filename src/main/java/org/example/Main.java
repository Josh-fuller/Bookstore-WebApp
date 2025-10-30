package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


    @Bean
    public CommandLineRunner demo(BookRepository bookInfoRepository, BookInventoryRepository bookInventoryRepository) {
        return (args) -> {
            // create a Book Inventory an add two book
            BookInventory inventory1 = new BookInventory();
            BookInfo book1 = new BookInfo("The Little Prince", "Fable, Children's literature, Novella, Fantasy", "$19.99", "978-0-00-862348-7");
            BookInfo book2 = new BookInfo("Treasure Island", "Adventure, Children's literature", "$10.99", "978-0-141-32100-4");
            inventory1.addBook(book1);
            inventory1.addBook(book2);

            bookInfoRepository.save(book1);
            bookInfoRepository.save(book2);


            System.out.println("LAUNCH APPLICATION HERE!!!!");
            System.out.println("http://localhost:8080/bookInventories");
        };
    }
}
