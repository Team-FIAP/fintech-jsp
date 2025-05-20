package com.fiap.fintechjsp.model;

import java.time.LocalDate;

public class Income extends Transaction {
    public Income(Long id, double amount, LocalDate date, String description, String observation, TransactionType type, Account originAccount) {
        super(id, amount, date, description, observation, type, originAccount);
    }
}
