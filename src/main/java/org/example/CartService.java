package org.example;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final PurchaseHistoryService purchaseHistoryService;

    public CartService(CartRepository cartRepository, BookRepository bookRepository, PurchaseHistoryService purchaseHistoryService) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.purchaseHistoryService = purchaseHistoryService;
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart(user);
                    return cartRepository.save(cart);
                });
    }

    public Cart getCart(User user) {
        return getOrCreateCart(user);
    }

    public void addBookToCart(User user, Long bookId) {
        Cart cart = getOrCreateCart(user);
        BookInfo book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        cart.addBook(book);
        cartRepository.save(cart);
    }

    public void removeBookFromCart(User user, Long bookId) {
        Cart cart = getOrCreateCart(user);
        Optional<BookInfo> bookOpt = cart.getBooks().stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst();

        bookOpt.ifPresent(book -> {
            cart.removeBook(book);
            cartRepository.save(cart);
        });
    }


    public void checkout(User user) {
        Cart cart = getOrCreateCart(user);

        if (cart.getBooks().isEmpty()) {
            return; // nothing to do
        }

        // 1) Count how many of each book is in the cart
        Map<Long, Long> countsByBookId = cart.getBooks().stream()
                .filter(Objects::nonNull)
                .filter(b -> b.getId() != null)
                .collect(Collectors.groupingBy(BookInfo::getId, Collectors.counting()));

        // 2) Load fresh copies from the DB for stock checks
        Iterable<BookInfo> booksFromDb = bookRepository.findAllById(countsByBookId.keySet());

        // 3) Verify stock for each book
        for (BookInfo book : booksFromDb) {
            long needed = countsByBookId.getOrDefault(book.getId(), 0L);
            if (!book.hasStock((int) needed)) {
                // Not enough inventory: do NOT complete checkout.
                // (You could instead throw an exception and show a nice message on the UI.)
                System.out.println("Not enough stock for '" + book.getBookTitle()
                        + "'. Needed: " + needed + ", available: " + book.getInventory());
                return;
            }
        }

        // 4) All good → decrement inventory
        for (BookInfo book : booksFromDb) {
            long neededLong = countsByBookId.getOrDefault(book.getId(), 0L);
            int needed = (int) neededLong;
            if (needed > 0) {
                book.decreaseStock(needed);
                bookRepository.save(book);
            }
        }

        // 5) Move all books from cart → purchase history
        PurchaseHistory history = purchaseHistoryService.getOrCreateHistory(user);
        history.getBooks().addAll(cart.getBooks());
        cart.getBooks().clear();

        // 6) Persist
        purchaseHistoryService.save(history);
        cartRepository.save(cart);
    }


    public double getCartTotal(User user) {
        Cart cart = getOrCreateCart(user);
        return cart.getBooks().stream()
                .map(BookInfo::getBookPrice)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }



}
