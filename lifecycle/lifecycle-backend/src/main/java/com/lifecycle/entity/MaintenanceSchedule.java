package com.lifecycle.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name = "maintenance_schedules")
public class MaintenanceSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false) public Long assetId;
    @Column(nullable = false) public String title;
    public LocalDate lastServiceDate;
    public LocalDate nextServiceDate;
    public Integer frequencyDays;
    public String source = "User-defined"; // User-defined / General recommendation
}
