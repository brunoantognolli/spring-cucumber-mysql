package com.example.springcucumber.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionProcedureResult {
    @Id
    private Long transactionId;
    private BigDecimal originalAmount;
    private BigDecimal processedAmount;
    private BigDecimal newProcessedAmount;
    private LocalDateTime processingDate;
    private String originalStatus;
} 