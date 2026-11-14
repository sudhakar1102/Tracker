package com.lifecycle.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name = "warranties")
public class Warranty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false) public Long assetId;
    public String warrantyType;   // Manufacturer / Extended
    public LocalDate startDate;
    public LocalDate expiryDate;
    public String provider;
}
