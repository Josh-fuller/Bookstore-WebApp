package org.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class InventoryColumnMigration implements CommandLineRunner {

    private final DataSource dataSource;

    public InventoryColumnMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection c = dataSource.getConnection();
             Statement s = c.createStatement()) {

            try {
                s.executeUpdate(
                        "ALTER TABLE book_info " +
                                "ADD COLUMN inventory INT DEFAULT 5 NOT NULL"
                );
                System.out.println("✅ Added inventory column to book_info.");
            } catch (SQLException ex) {
                int code = ex.getErrorCode();
                String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

                // H2 uses 42121 for "duplicate column name"
                if (code == 42121 || msg.contains("duplicate column")) {
                    System.out.println("ℹ️ inventory column already exists, skipping migration.");
                } else {
                    // anything unexpected: fail fast
                    throw ex;
                }
            }
        }
    }
}
