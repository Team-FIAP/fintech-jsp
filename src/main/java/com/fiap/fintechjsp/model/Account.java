package com.fiap.fintechjsp.model;

public class Account {
    private Long id;
    private String name;
    private double balance;

    public Account(Long id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }
}
