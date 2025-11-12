package org.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<BookInfo, Long> {
    // Containing means partial match, Ignore Case means capitals and lower case irrelevant
    // Find by title
    List<BookInfo> findByBookTitleContainingIgnoreCase(String title);

    // Find by author
    List<BookInfo> findByBookAuthorContainingIgnoreCase(String author);

    // Find by genre
    List<BookInfo> findByBookGenresContainingIgnoreCase(String genre);

    // Find by publisher
    List<BookInfo> findByBookPublisherContainingIgnoreCase(String publisher);

    // Find by ISBN (exact match)
    BookInfo findByBookISBN(String isbn);

    // Price filter min and max, min can be 0 and max can be large for single direction filter (below x or above y)
    List<BookInfo> findByBookPriceBetween(Double minPrice, Double maxPrice);

    // *experimental*
    @Query("SELECT DISTINCT b.bookGenre FROM BookInfo b")
    List<String> findDistinctGenres();

}