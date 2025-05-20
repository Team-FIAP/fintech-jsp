package com.fiap.fintechjsp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transfer extends Transaction {
    private final Account destinationAccount;

    public Transfer(Long id, double amount, LocalDate date, String description, String observation, Account originAccount, Account destinationAccount, LocalDateTime createdAt) {
        super(id, amount, date, description, observation, TransactionType.TRANSFER, originAccount, createdAt);
        this.destinationAccount = destinationAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }
}
