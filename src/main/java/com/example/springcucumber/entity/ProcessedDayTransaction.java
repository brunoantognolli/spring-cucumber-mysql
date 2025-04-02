package com.example.springcucumber.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processed_day_transactions")
public class ProcessedDayTransaction {
    @Id
    private Long id;
    private String processedDescription;
    private BigDecimal processedAmount;
    private LocalDateTime processedDate;
    private String processedStatus;
    private String processingNotes;
} 