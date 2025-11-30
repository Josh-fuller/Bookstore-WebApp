package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = BookstoreApplication.class)
@ActiveProfiles("test")
class BookRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private BookInfo book1;
    private BookInfo book2;
    private BookInfo book3;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        String description1 = "Winning means fame and fortune. Losing means certain death. The Hunger Games have begun. In the ruins of a place once known as North America lies the nation of Panem, a shining Capitol surrounded by twelve outlying districts. The Capitol is harsh and cruel and keeps the districts in line by forcing them all to send one boy and one girl between the ages of twelve and eighteen to participate in the annual Hunger Games, a fight to the death on live TV. Sixteen-year-old Katniss Everdeen regards it as a death sentence when she steps forward to take her sister's place in the Games. But Katniss has been close to dead before-and survival, for her, is second nature. Without really meaning to, she becomes a contender. But if she is to win, she will have to start making choices that weigh survival against humanity and life against love.";
        String description2 = "Against all odds, Katniss Everdeen has won the Hunger Games. She and fellow District 12 tribute Peeta Mellark are miraculously still alive. Katniss should be relieved, happy even. After all, she has returned to her family and her longtime friend, Gale. Yet nothing is the way Katniss wishes it to be. Gale holds her at an icy distance. Peeta has turned his back on her completely. And there are whispers of a rebellion against the Capitol—a rebellion that Katniss and Peeta may have helped create. Much to her shock, Katniss has fueled an unrest that she's afraid she cannot stop. And what scares her even more is that she's not entirely convinced she should try. As time draws near for Katniss and Peeta to visit the districts on the Capitol's cruel Victory Tour, the stakes are higher than ever. If they can't prove, without a shadow of a doubt, that they are lost in their love for each other, the consequences will be horrifying. In Catching Fire, the second novel of the Hunger Games trilogy, Suzanne Collins continues the story of Katniss Everdeen, testing her more than ever before . . . and surprising readers at every turn.";
        String description3 = "Katniss Everdeen, girl on fire, has survived, even though her home has been destroyed. Gale has escaped. Katniss's family is safe. Peeta has been captured by the Capitol. District 13 really does exist. There are rebels. There are new leaders. A revolution is unfolding. It is by design that Katniss was rescued from the arena in the cruel and haunting Quarter Quell, and it is by design that she has long been part of the revolution without knowing it. District 13 has come out of the shadows and is plotting to overthrow the Capitol. Everyone, it seems, has had a hand in the carefully laid plans—except Katniss. The success of the rebellion hinges on Katniss's willingness to be a pawn, to accept responsibility for countless lives, and to change the course of the future of Panem. To do this, she must put aside her feelings of anger and distrust. She must become the rebels' Mockingjay—no matter what the personal cost.";

        book1 = new BookInfo(
                "The Hunger Games",
                "Fantasy",
                19.99,
                "9780439023481",
                "Suzanne Collins",
                "Scholastic",
                description1,
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722975i/2767052.jpg"
        );
        book2 = new BookInfo(
                "Catching Fire",
                "Fantasy",
                19.99,
                "9780439023498",
                "Suzanne Collins",
                "Scholastic",
                description2,
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722941i/6148028.jpg"
        );
        book3 = new BookInfo(
                "Mockingjay",
                "Fantasy",
                19.99,
                "9780439023511",
                "Suzanne Collins",
                "Scholastic",
                description3,
                "https://m.media-amazon.com/images/S/compressed.photo.goodreads.com/books/1586722918i/7260188.jpg"
        );
    }

    @Test
    void findByBookTitleContainingIgnoreCase() {
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();

        List<BookInfo> found = bookRepository.findByBookTitleContainingIgnoreCase("catching");
        assertEquals(1, found.size());
        assertEquals("Catching Fire", found.get(0).getBookTitle());
    }

    @Test
    void findByBookAuthorContainingIgnoreCase() {
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();

        List<BookInfo> found = bookRepository.findByBookAuthorContainingIgnoreCase("collins");
        assertEquals(2, found.size());
    }

    @Test
    void findByBookGenreContainingIgnoreCase() {
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();

        List<BookInfo> found = bookRepository.findByBookGenresContainingIgnoreCase("fantasy");
        assertEquals(2, found.size());
    }

    @Test
    void whenFindByBookISBN() {
        entityManager.persist(book1);
        entityManager.flush();

        BookInfo found = bookRepository.findByBookISBN("9780439023481");
        assertNotNull(found);
        assertEquals("9780439023481", found.getBookISBN());
    }

    @Test
    void whenFindByBookPriceBetween() {
        book1.setBookPrice(25.00);
        book2.setBookPrice(30.00);
        book3.setBookPrice(35.00);

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.flush();

        List<BookInfo> found = bookRepository.findByBookPriceBetween(25.00, 30.00);
        assertEquals(2, found.size());

        Set<Double> prices = found.stream().map(BookInfo::getBookPrice).collect(Collectors.toSet());
        assertTrue(prices.contains(25.00));
        assertTrue(prices.contains(30.00));
    }

    @Test
    void findByBookPublisherContainingIgnoreCase() {
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();

        List<BookInfo> found = bookRepository.findByBookPublisherContainingIgnoreCase("scholastic");
        assertEquals(2, found.size());
    }

    @Test
    void findDistinctGenres() {
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();

        List<String> genres = bookRepository.findDistinctGenres();
        assertNotNull(genres);
    }
}
