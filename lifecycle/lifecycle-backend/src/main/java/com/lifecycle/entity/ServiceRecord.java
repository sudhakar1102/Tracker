package com.lifecycle.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "service_records")
public class ServiceRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false) public Long assetId;
    public LocalDate serviceDate;
    public String serviceType;
    public String serviceProvider;
    @Column(length = 2000) public String description;
    @Column(precision = 12, scale = 2) public BigDecimal repairCost;
}
