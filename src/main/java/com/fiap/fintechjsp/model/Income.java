package com.fiap.fintechjsp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Income extends Transaction {
    public Income(Long id, double amount, LocalDate date, String description, String observation, Account originAccount, LocalDateTime createdAt) {
        super(id, amount, date, description, observation, TransactionType.INCOME, originAccount, createdAt);
    }
}
