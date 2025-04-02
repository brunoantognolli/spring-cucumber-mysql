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
@Table(name = "day_transactions")
public class DayTransaction {
    @Id
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String status;
} 