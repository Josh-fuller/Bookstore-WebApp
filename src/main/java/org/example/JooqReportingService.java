package org.example;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.count;
import static org.example.jooq.tables.BookInfo.BOOK_INFO;
import static org.example.jooq.tables.BookInventoryBooks.BOOK_INVENTORY_BOOKS;
import static org.example.jooq.tables.Users.USERS;

@Service
public class JooqReportingService {

    private final DSLContext dsl;

    public JooqReportingService(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * REPORT 1: Top purchased books (ONLY purchased lists)
     */
    public Result<Record4<String, String, Double, Integer>> topPurchasedBooks() {

        return dsl
                .select(
                        BOOK_INFO.BOOK_TITLE,
                        BOOK_INFO.BOOK_AUTHOR,
                        BOOK_INFO.BOOK_PRICE,
                        count().as("timesPurchased")
                )
                .from(BOOK_INVENTORY_BOOKS)
                .join(USERS)
                .on(BOOK_INVENTORY_BOOKS.BOOK_INVENTORY_ID.eq(USERS.PURCHASED_ID))
                .join(BOOK_INFO)
                .on(BOOK_INFO.ID.eq(BOOK_INVENTORY_BOOKS.BOOKS_ID))
                .groupBy(BOOK_INFO.ID)
                .orderBy(count().desc())
                .limit(10)
                .fetch();
    }

    /**
     * REPORT 2: Top users by number of purchased books
     */
    public Result<Record2<String, Integer>> topUsers() {

        return dsl
                .select(
                        USERS.USERNAME,
                        count().as("booksPurchased")
                )
                .from(USERS)
                .join(BOOK_INVENTORY_BOOKS)
                .on(BOOK_INVENTORY_BOOKS.BOOK_INVENTORY_ID.eq(USERS.PURCHASED_ID))
                .groupBy(USERS.USERNAME)
                .orderBy(count().desc())
                .limit(10)
                .fetch();
    }

    /**
     * REPORT 3: Top books currently in carts (NOT purchased)
     */
    public Result<Record4<String, String, Double, Integer>> topInCartBooks() {

        return dsl
                .select(
                        BOOK_INFO.BOOK_TITLE,
                        BOOK_INFO.BOOK_AUTHOR,
                        BOOK_INFO.BOOK_PRICE,
                        count().as("timesInCart")
                )
                .from(BOOK_INVENTORY_BOOKS)
                .join(USERS)
                .on(BOOK_INVENTORY_BOOKS.BOOK_INVENTORY_ID.eq(USERS.CART_ID))
                .join(BOOK_INFO)
                .on(BOOK_INFO.ID.eq(BOOK_INVENTORY_BOOKS.BOOKS_ID))
                .groupBy(BOOK_INFO.ID)
                .orderBy(count().desc())
                .limit(10)
                .fetch();
    }

    public Result<Record2<String, Double>> topGenresBySales() {

        // Step 1: Pull raw purchased books and their genre strings
        Result<Record2<String, Double>> raw =
                dsl.select(
                                BOOK_INFO.BOOK_GENRE,
                                BOOK_INFO.BOOK_PRICE
                        )
                        .from(BOOK_INVENTORY_BOOKS)
                        .join(USERS).on(BOOK_INVENTORY_BOOKS.BOOK_INVENTORY_ID.eq(USERS.PURCHASED_ID))
                        .join(BOOK_INFO).on(BOOK_INFO.ID.eq(BOOK_INVENTORY_BOOKS.BOOKS_ID))
                        .fetch();

        // Step 2: Aggregate totals
        Map<String, Double> totals = new HashMap<>();

        for (Record2<String, Double> row : raw) {
            String genres = row.value1();
            Double price  = row.value2();
            if (genres == null || price == null) continue;

            for (String g : genres.split(",")) {
                String genre = g.trim();
                if (!genre.isEmpty()) {
                    totals.merge(genre, price, Double::sum);
                }
            }
        }

        // Step 3: Build jOOQ Result<Record2<String, Double>>
        DSLContext ctx = DSL.using(dsl.configuration());

        Result<Record2<String, Double>> result =
                ctx.newResult(
                        DSL.field("genre", SQLDataType.VARCHAR),
                        DSL.field("total_sales", SQLDataType.DOUBLE)
                );

        totals.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(entry -> {
                    Record2<String, Double> rec =
                            ctx.newRecord(
                                    DSL.field("genre", SQLDataType.VARCHAR),
                                    DSL.field("total_sales", SQLDataType.DOUBLE)
                            );
                    rec.value1(entry.getKey());
                    rec.value2(entry.getValue());
                    result.add(rec);
                });

        return result;
    }

}
