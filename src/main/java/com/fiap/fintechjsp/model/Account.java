package com.fiap.fintechjsp.model;

import java.time.LocalDateTime;

public class Account {
    private Long id;
    private String name;
    private double balance;
    private final User user;
    private LocalDateTime createdAt;

    public Account(Long id, String name, double balance, User user, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Account(String name, double balance, User user) {
        this.name = name;
        this.balance = balance;
        this.user = user;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
