package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookInfoTest {

    @Test
    void testBookInfoCreation() {
        String description = "Harry Potter has no idea how famous he is. That's because he's " +
                "being raised by his miserable aunt and uncle who are terrified Harry will " +
                "learn that he's really a wizard, just as his parents were. But everything " +
                "changes when Harry is summoned to attend an infamous school for wizards, " +
                "and he begins to discover some clues about his illustrious birthright. From " +
                "the surprising way he is greeted by a lovable giant, to the unique curriculum and " +
                "colorful faculty at his unusual school, Harry finds himself drawn deep inside a " +
                "mystical world he never knew existed and closer to his own noble destiny.";
        BookInfo book = new BookInfo(
                "Harry Potter and the Sorcerer's Stone",
                "Fiction",
                24.92,
                "9780590353427",
                "J.K. Rowling",
                "Scholastic",
                description,
                "https://pictures.abebooks.com/isbn/9780590353427-us.jpg"
        );

        assertNull(book.getId());
        assertEquals("Harry Potter and the Sorcerer's Stone", book.getBookTitle());
        assertEquals("Fiction", book.getBookGenres());
        assertEquals(24.92, book.getBookPrice());
        assertEquals("9780590353427", book.getBookISBN());
        assertEquals("J.K. Rowling", book.getBookAuthor());
        assertEquals("Scholastic", book.getBookPublisher());
        assertEquals(description, book.getBookDescription());
        assertEquals("https://pictures.abebooks.com/isbn/9780590353427-us.jpg", book.getBookCoverURL());
    }

    @Test
    void testBookInfoSetters() {
        String description = "Harry Potter has no idea how famous he is. That's because he's " +
                "being raised by his miserable aunt and uncle who are terrified Harry will " +
                "learn that he's really a wizard, just as his parents were. But everything " +
                "changes when Harry is summoned to attend an infamous school for wizards, " +
                "and he begins to discover some clues about his illustrious birthright. From " +
                "the surprising way he is greeted by a lovable giant, to the unique curriculum and " +
                "colorful faculty at his unusual school, Harry finds himself drawn deep inside a " +
                "mystical world he never knew existed and closer to his own noble destiny.";
        BookInfo book = new BookInfo();

        book.setBookTitle("Harry Potter and the Sorcerer's Stone");
        book.setBookGenres("Fiction");
        book.setBookPrice(24.92);
        book.setBookISBN("9780590353427");
        book.setBookAuthor("J.K. Rowling");
        book.setBookPublisher("Scholastic");
        book.setBookDescription(description);
        book.setBookCoverURL("https://pictures.abebooks.com/isbn/9780590353427-us.jpg");

        // Then
        assertNull(book.getId());
        assertEquals("Harry Potter and the Sorcerer's Stone", book.getBookTitle());
        assertEquals("Fiction", book.getBookGenres());
        assertEquals(24.92, book.getBookPrice());
        assertEquals("9780590353427", book.getBookISBN());
        assertEquals("J.K. Rowling", book.getBookAuthor());
        assertEquals("Scholastic", book.getBookPublisher());
        assertEquals(description, book.getBookDescription());
        assertEquals("https://pictures.abebooks.com/isbn/9780590353427-us.jpg", book.getBookCoverURL());
    }

    @Test
    void testBookNameAlias() {
        BookInfo book = new BookInfo("Test Book", "Fiction", 20.00,
                "123", "Author", "Publisher", "Desc", "URL");

        // When & Then
        assertEquals("Test Book", book.getBookName());
        book.setBookName("Test Book Part 2");
        assertEquals("Test Book Part 2", book.getBookTitle());
    }
}