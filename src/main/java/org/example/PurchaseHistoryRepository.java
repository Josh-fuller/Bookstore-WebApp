package org.example;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    Optional<PurchaseHistory> findByUser(User user);

    @Query("""
        SELECT b.bookGenres 
        FROM PurchaseHistory ph 
        JOIN ph.books b 
        WHERE ph.user.id = :userId
    """)
    List<String> findGenresByUser(@Param("userId") Long userId);
}
