package org.example;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookInventoryRepository extends JpaRepository<BookInventory, Long> {
}