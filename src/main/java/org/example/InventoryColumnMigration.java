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

            // Try to add the column; if it exists, catch and ignore
            try {
                s.executeUpdate("ALTER TABLE book_info ADD COLUMN inventory INT DEFAULT 5 NOT NULL");
                System.out.println("✅ Added inventory column to book_info.");
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                if (msg != null && msg.toLowerCase().contains("already")
                        && msg.toLowerCase().contains("exist")) {
                    System.out.println("ℹ️ inventory column already exists, skipping migration.");
                } else {
                    throw ex; // unexpected, rethrow
                }
            }
        }
    }
}
