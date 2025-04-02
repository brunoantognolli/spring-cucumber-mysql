package com.example.springcucumber.config;

import com.example.springcucumber.entity.DayTransaction;
import com.example.springcucumber.entity.ProcessedDayTransaction;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;

public class ProcessingItemProcessor implements ItemProcessor<DayTransaction, ProcessedDayTransaction> {

    @Override
    public ProcessedDayTransaction process(DayTransaction transaction) {
        ProcessedDayTransaction processedTransaction = new ProcessedDayTransaction();
        processedTransaction.setId(transaction.getId());
        processedTransaction.setProcessedDescription("Processed: " + transaction.getDescription());
        processedTransaction.setProcessedAmount(transaction.getAmount().multiply(new java.math.BigDecimal("1.1"))); // Add 10% processing fee
        processedTransaction.setProcessedDate(LocalDateTime.now());
        processedTransaction.setProcessedStatus(determineProcessedStatus(transaction.getStatus()));
        processedTransaction.setProcessingNotes(generateProcessingNotes(transaction));
        return processedTransaction;
    }

    private String determineProcessedStatus(String originalStatus) {
        return switch (originalStatus) {
            case "PENDING" -> "IN_PROCESS";
            case "COMPLETED" -> "PROCESSED";
            case "FAILED" -> "REJECTED";
            default -> "UNKNOWN";
        };
    }

    private String generateProcessingNotes(DayTransaction transaction) {
        return String.format("Original transaction processed at %s. Original status: %s",
                LocalDateTime.now(),
                transaction.getStatus());
    }
} 