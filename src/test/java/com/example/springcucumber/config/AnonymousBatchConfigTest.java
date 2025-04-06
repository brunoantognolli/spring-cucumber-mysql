package com.example.springcucumber.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AnonymousBatchConfigTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String loadSqlFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("anonymousBlock.sql");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Test
    void testStoredProcedureDirectly() throws IOException {
        // Execute the SP and print raw results
        String sql = loadSqlFile();
        jdbcTemplate.execute(sql);

        List<Map<String, Object>> rawResults = jdbcTemplate.queryForList("SELECT * FROM tmpTransaction2");

        // Print raw results for debugging
        System.out.println("Raw Anonymous block Results:");
        rawResults.forEach(row -> {
            System.out.println("Row: " + row);
            row.forEach((column, value) -> 
                System.out.println("  " + column + " = " + value + " (" + 
                    (value != null ? value.getClass().getName() : "null") + ")"));
        });

        // Basic assertions
        assertFalse(rawResults.isEmpty(), "Anonymous block should return results");
        
        // Verify calculations for the first row
        Map<String, Object> firstRow = rawResults.get(0);
        assertNotNull(firstRow.get("transaction_id"), "transaction_id should not be null");
        assertNotNull(firstRow.get("original_amount"), "original_amount should not be null");
        assertNotNull(firstRow.get("processed_amount"), "processed_amount should not be null");
        assertNotNull(firstRow.get("new_processed_amount"), "new_processed_amount should not be null");
    }
} 