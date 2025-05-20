package com.fiap.fintechjsp.model;

import java.time.LocalDate;

public abstract class Transaction {
    private Long id;
    private double amount;
    private LocalDate date;
    private String description;
    private String observation;
    private final TransactionType type;
    private final Account originAccount;

    public Transaction(Long id, double amount, LocalDate date, String description, String observation, TransactionType type, Account originAccount) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.observation = observation;
        this.type = type;
        this.originAccount = originAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public TransactionType getType() {
        return type;
    }

    public Account getOriginAccount() {
        return originAccount;
    }
}
