package com.lifecycle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @Column(nullable = false) public String name;
    @Column(unique = true, nullable = false) public String email;
    @JsonIgnore @Column(nullable = false) public String password;
    public String phone;
    public LocalDateTime createdAt = LocalDateTime.now();
}
