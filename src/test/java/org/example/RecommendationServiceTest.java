package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    @Mock
    private PurchaseHistoryRepository purchaseRepo;

    @Mock
    private BookRepository bookRepo;

    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testNoPurchasesReturnsTopBooks() {
        User user = new User();
        user.setId(1L);

        when(purchaseRepo.findGenresByUser(user.getId())).thenReturn(Collections.emptyList());

        List<BookInfo> topBooks = List.of(
                new BookInfo("Book1", "Fiction", 20.0, "123", "Author1", "Publisher1", "Desc1", "/cover1.jpg"),
                new BookInfo("Book2", "Sci-Fi", 25.0, "456", "Author2", "Publisher2", "Desc2", "/cover2.jpg")
        );

        when(bookRepo.findTop10ByOrderByBookPriceDesc()).thenReturn(topBooks);

        List<BookInfo> recommendations = recommendationService.getRecommendations(user, 10);

        assertEquals(2, recommendations.size());
        assertEquals("Book1", recommendations.get(0).getBookTitle());
        assertEquals("Book2", recommendations.get(1).getBookTitle());

        verify(purchaseRepo).findGenresByUser(user.getId());
        verify(bookRepo).findTop10ByOrderByBookPriceDesc();
    }

    @Test
    void testNullUserReturnsEmptyList() {
        List<BookInfo> recommendations = recommendationService.getRecommendations(null, 10);
        assertTrue(recommendations.isEmpty());
    }
}