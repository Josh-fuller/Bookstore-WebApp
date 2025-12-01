package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private PurchaseHistoryRepository purchaseRepo;

    @Autowired
    private BookRepository bookRepo;

    public List<BookInfo> getRecommendations(User user, int limit) {

        System.out.println("=== RecommendationService.getRecommendations ===");
        System.out.println("User: " + (user == null ? "NULL" : user.getUsername()));
        System.out.println("Limit: " + limit);

        if (user == null) {
            System.out.println("User is NULL → returning empty list");
            return Collections.emptyList();
        }

        // get all genres from user purchases
        // Raw genres (each may contain comma-separated items)
        List<String> rawGenres = purchaseRepo.findGenresByUser(user.getId());
        System.out.println("Raw genres from DB: " + rawGenres);

        // Split each item by comma, flatten into one list
        List<String> purchasedGenres = rawGenres.stream()
                .filter(Objects::nonNull)
                .flatMap(g -> Arrays.stream(g.split(",")))   // split by comma
                .map(String::trim)                           // remove spaces
                .filter(s -> !s.isEmpty())                   // ignore empty
                .collect(Collectors.toList());

        System.out.println("Normalized genres: " + purchasedGenres);

        System.out.println("Purchased Genres: " + purchasedGenres);

        // if no purchases
        if (purchasedGenres.isEmpty()) {
            System.out.println("No purchased genres → returning top 10 expensive books");
            List<BookInfo> topBooks = bookRepo.findTop10ByOrderByBookPriceDesc();
            System.out.println("Top books: " + topBooks);
            return topBooks;
        }

        // counting frequency of each genre
        Map<String, Long> genreCounts = purchasedGenres.stream()
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()));
        System.out.println("Genre Counts: " + genreCounts);

        // sort all genres by frequency
        List<String> sortedGenres = genreCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        System.out.println("Sorted Genres by frequency: " + sortedGenres);

        // user's purchase history object
        PurchaseHistory history = purchaseRepo.findByUser(user).orElse(null);
        System.out.println("PurchaseHistory found? " + (history != null));
        if (history != null)
            System.out.println("Books already owned: " + history.getBooks());

        List<BookInfo> recommended = new ArrayList<>();

        // go through each genre and pick books
        for (String genre : sortedGenres) {

            System.out.println("---- Checking genre: " + genre);

            List<BookInfo> booksInGenre = bookRepo.findBooksByGenreContains(genre);


            System.out.println("Books found in genre '" + genre + "': " + booksInGenre);

            for (BookInfo b : booksInGenre) {
                boolean alreadyHas =
                        (history != null && history.getBooks().contains(b));

                System.out.println("   Book: " + b.getBookTitle()
                        + " | already owned? " + alreadyHas);

                if (!alreadyHas) {
                    recommended.add(b);
                    System.out.println("   → Added to recommendations");

                    if (recommended.size() >= limit) {
                        System.out.println("Reached limit (" + limit + ")");
                        break;
                    }
                }
            }

            if (recommended.size() >= limit) break;
        }

        System.out.println("=== Final Recommendations ===");
        System.out.println(recommended);

        return recommended;
    }
}
