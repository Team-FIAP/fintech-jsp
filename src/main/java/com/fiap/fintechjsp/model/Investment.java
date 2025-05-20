package com.fiap.fintechjsp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Investment extends Transaction {
    private String investmentType;
    private String risk;
    private String liquidity;
    private double profitability;
    private LocalDate dueDate;

    public Investment(Long id, double amount, LocalDate date, String description, String observation, Account originAccount, LocalDateTime createdAt, String investmentType, String risk, String liquidity, double profitability, LocalDate dueDate) {
        super(id, amount, date, description, observation, TransactionType.INVESTMENT, originAccount, createdAt);
        this.investmentType = investmentType;
        this.risk = risk;
        this.liquidity = liquidity;
        this.profitability = profitability;
        this.dueDate = dueDate;
    }

    public String getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(String investmentType) {
        this.investmentType = investmentType;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getLiquidity() {
        return liquidity;
    }

    public void setLiquidity(String liquidity) {
        this.liquidity = liquidity;
    }

    public double getProfitability() {
        return profitability;
    }

    public void setProfitability(double profitability) {
        this.profitability = profitability;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
