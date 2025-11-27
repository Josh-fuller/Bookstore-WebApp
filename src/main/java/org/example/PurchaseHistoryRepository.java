package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {
    Optional<PurchaseHistory> findByUser(User user);
}
