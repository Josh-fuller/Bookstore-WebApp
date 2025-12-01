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
        if (user == null) return Collections.emptyList();

        //get all genres from user purchases
        List<String> purchasedGenres = purchaseRepo.findGenresByUser(user.getId());

        // if no purchases
        if (purchasedGenres.isEmpty()) {
            return bookRepo.findTop10ByOrderByBookPriceDesc();
        }

        // counting frequency of each genre
        Map<String, Long> genreCounts = purchasedGenres.stream()
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()));

        // sort all genres by frequency
        List<String> sortedGenres = genreCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        // books in top genres but not purchased
        PurchaseHistory history = purchaseRepo.findByUser(user).orElse(null);

        List<BookInfo> recommended = new ArrayList<>();
        for (String genre : sortedGenres) {
            List<BookInfo> booksInGenre = bookRepo.findTop10ByBookGenresOrderByBookPriceDesc(genre);
            for (BookInfo b : booksInGenre) {
                if (history == null || !history.getBooks().contains(b)) {
                    recommended.add(b);
                    if (recommended.size() >= limit) break;
                }
            }
            if (recommended.size() >= limit) break;
        }

        return recommended;
    }
}