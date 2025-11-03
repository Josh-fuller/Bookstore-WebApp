package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
@ContextConfiguration(classes = BookInventory.class)
@ActiveProfiles("test")
class BookInventoryTest {

    private BookInventory inventory;
    private BookInfo book1;
    private BookInfo book2;
    private BookInfo book3;

    @BeforeEach
    void setUp() {
        inventory = new BookInventory();
        String description1 = "Winning means fame and fortune. Losing means certain death. The Hunger Games have begun. In the ruins of a place once known as North America lies the nation of Panem, a shining Capitol surrounded by twelve outlying districts. The Capitol is harsh and cruel and keeps the districts in line by forcing them all to send one boy and one girl between the ages of twelve and eighteen to participate in the annual Hunger Games, a fight to the death on live TV. Sixteen-year-old Katniss Everdeen regards it as a death sentence when she steps forward to take her sister's place in the Games. But Katniss has been close to dead before-and survival, for her, is second nature. Without really meaning to, she becomes a contender. But if she is to win, she will have to start making choices that weigh survival against humanity and life against love.";
        String description2 = "Against all odds, Katniss Everdeen has won the Hunger Games. She and fellow District 12 tribute Peeta Mellark are miraculously still alive. Katniss should be relieved, happy even. After all, she has returned to her family and her longtime friend, Gale. Yet nothing is the way Katniss wishes it to be. Gale holds her at an icy distance. Peeta has turned his back on her completely. And there are whispers of a rebellion against the Capitol—a rebellion that Katniss and Peeta may have helped create. Much to her shock, Katniss has fueled an unrest that she's afraid she cannot stop. And what scares her even more is that she's not entirely convinced she should try. As time draws near for Katniss and Peeta to visit the districts on the Capitol's cruel Victory Tour, the stakes are higher than ever. If they can't prove, without a shadow of a doubt, that they are lost in their love for each other, the consequences will be horrifying. In Catching Fire, the second novel of the Hunger Games trilogy, Suzanne Collins continues the story of Katniss Everdeen, testing her more than ever before . . . and surprising readers at every turn.";
        String description3 = "Katniss Everdeen, girl on fire, has survived, even though her home has been destroyed. Gale has escaped. Katniss's family is safe. Peeta has been captured by the Capitol. District 13 really does exist. There are rebels. There are new leaders. A revolution is unfolding. It is by design that Katniss was rescued from the arena in the cruel and haunting Quarter Quell, and it is by design that she has long been part of the revolution without knowing it. District 13 has come out of the shadows and is plotting to overthrow the Capitol. Everyone, it seems, has had a hand in the carefully laid plans—except Katniss. The success of the rebellion hinges on Katniss's willingness to be a pawn, to accept responsibility for countless lives, and to change the course of the future of Panem. To do this, she must put aside her feelings of anger and distrust. She must become the rebels' Mockingjay—no matter what the personal cost.";

        book1 = new BookInfo("The Hunger Games", "Fantasy", 19.99,
                "9780439023481", "Suzanne Collins",
                "Scholastic", description1,
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722975i/2767052.jpg");
        book2 = new BookInfo("Catching Fire", "Fantasy", 19.99,
                "9780439023498", "Suzanne Collins",
                "Scholastic", description2,
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722941i/6148028.jpg");
        book3 = new BookInfo("Mockingjay", "Fantasy", 19.99,
                "9780439023511", "Suzanne Collins",
                "Scholastic", description3,
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722918i/7260188.jpg");

    }

    @Test
    void testAddBook() {
        inventory.addBook(book1);
        inventory.addBook(book2);

        List<BookInfo> books = inventory.getBooks();
        assertEquals(2, books.size());
        assertTrue(books.contains(book1));
        assertTrue(books.contains(book2));
    }

    @Test
    void testAddNullBook() {
        inventory.addBook(null);

        assertEquals(0, inventory.getBooks().size());
    }

    @Test
    void testRemoveBook() {
        inventory.addBook(book1);
        inventory.addBook(book2);
        inventory.addBook(book3);

        inventory.removeBook(book2);

        List<BookInfo> books = inventory.getBooks();
        assertEquals(2, books.size());
        assertFalse(books.contains(book2));
        assertTrue(books.contains(book1));
        assertTrue(books.contains(book3));
    }

    @Test
    void testGetBooksReturnsSortedList() {
        inventory.addBook(book1);
        inventory.addBook(book2);
        inventory.addBook(book3);

        List<BookInfo> sortedBooks = inventory.getBooks();

        assertEquals(3, sortedBooks.size());
        assertEquals("The Hunger Games", sortedBooks.get(2).getBookTitle());
        assertEquals("Catching Fire", sortedBooks.get(0).getBookTitle());
        assertEquals("Mockingjay", sortedBooks.get(1).getBookTitle());
    }

    @Test
    void testGetBooksCaseInsensitiveSorting() {
        BookInfo bookA = new BookInfo("apple", "Fiction", 10.0, "1", "A", "P", "D", "U");
        BookInfo bookB = new BookInfo("Apple", "Fiction", 10.0, "2", "B", "P", "D", "U");
        BookInfo bookC = new BookInfo("banana", "Fiction", 10.0, "3", "C", "P", "D", "U");

        inventory.addBook(bookC);
        inventory.addBook(bookA);
        inventory.addBook(bookB);

        List<BookInfo> sortedBooks = inventory.getBooks();

        assertEquals("apple", sortedBooks.get(0).getBookTitle());
        assertEquals("Apple", sortedBooks.get(1).getBookTitle());
        assertEquals("banana", sortedBooks.get(2).getBookTitle());
    }
}