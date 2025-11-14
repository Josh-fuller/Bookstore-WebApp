package org.example;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;

    public CartService(CartRepository cartRepository, BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
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
}
