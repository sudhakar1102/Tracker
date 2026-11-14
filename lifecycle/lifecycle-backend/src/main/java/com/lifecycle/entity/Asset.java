package com.lifecycle.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "assets")
public class Asset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false) public Long userId;
    @Column(nullable = false) public String productName;
    public String category;          // Electronics, Appliance, Vehicle, Furniture, Other
    public String brand;
    public String model;
    public String serialNumber;      // optional: not every product has one
    public String purchaseLocation;
    public LocalDate purchaseDate;
    @Column(precision = 12, scale = 2) public BigDecimal purchasePrice;
    @Column(precision = 12, scale = 2) public BigDecimal replacementCost; // price of a new equivalent
    @Column(name = "product_condition") public String condition; // Excellent, Good, Fair, Poor
    public Integer usefulLifeYears;
}
