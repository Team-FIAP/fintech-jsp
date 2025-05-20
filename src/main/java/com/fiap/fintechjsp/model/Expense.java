package com.fiap.fintechjsp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Expense extends Transaction {
    private ExpenseCategory category;

    public Expense(Long id, double amount, LocalDate date, String description, String observation, Account originAccount, LocalDateTime createdAt, ExpenseCategory category) {
        super(id, amount, date, description, observation, TransactionType.EXPENSE, originAccount, createdAt);
        this.category = category;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }
}
