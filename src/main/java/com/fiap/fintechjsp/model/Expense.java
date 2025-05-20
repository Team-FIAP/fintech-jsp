package com.fiap.fintechjsp.model;

import java.time.LocalDate;

public class Expense extends Transaction {
    private ExpenseCategory category;

    public Expense(Long id, double amount, LocalDate date, String description, String observation, TransactionType type, Account originAccount) {
        super(id, amount, date, description, observation, type, originAccount);
    }
}
