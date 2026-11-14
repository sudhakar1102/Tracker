package com.lifecycle.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false) public Long userId;
    @Column(nullable = false, length = 500) public String message;
    public String type;  // WARRANTY / MAINTENANCE
    @Column(name = "is_read") public boolean read = false;
    public LocalDateTime createdAt = LocalDateTime.now();
}
