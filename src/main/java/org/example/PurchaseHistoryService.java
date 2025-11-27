package org.example;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final BookRepository bookRepository;

    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository,
                                  BookRepository bookRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
        this.bookRepository = bookRepository;
    }

    public PurchaseHistory getOrCreateHistory(User user) {
        return purchaseHistoryRepository.findByUser(user)
                .orElseGet(() -> purchaseHistoryRepository.save(new PurchaseHistory(user)));
    }

    public void addBookToHistory(User user, Long bookId) {
        PurchaseHistory history = getOrCreateHistory(user);

        BookInfo book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        history.addBook(book);
        purchaseHistoryRepository.save(history);
    }

    public void removeBookFromHistory(User user, Long bookId) {
        PurchaseHistory history = getOrCreateHistory(user);

        Optional<BookInfo> bookOpt = history.getBooks().stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst();

        bookOpt.ifPresent(book -> {
            history.removeBook(book);
            purchaseHistoryRepository.save(history);
        });
    }

    public void save(PurchaseHistory history) {
        purchaseHistoryRepository.save(history);
    }

}
