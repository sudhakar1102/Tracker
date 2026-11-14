package com.lifecycle.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "expenses")
public class Expense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false) public Long assetId;
    public String expenseType;   // Maintenance, Accessories, Insurance, Other
    @Column(precision = 12, scale = 2) public BigDecimal amount;
    public LocalDate expenseDate;
    @Column(length = 1000) public String description;
}
