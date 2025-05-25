package com.fiap.fintechjsp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Investment extends Transaction {
    private InvestmentType investmentType;
    private String risk;
    private String liquidity;
    private double profitability;
    private LocalDate dueDate;
    private double interestRate;
    private boolean redeemed;

    public Investment(Long id, double amount, LocalDate date, String description, String observation, Account originAccount, LocalDateTime createdAt, InvestmentType investmentType, String risk, String liquidity, double profitability, LocalDate dueDate, double interestRate, boolean redeemed) {
        super(id, amount, date, description, observation, TransactionType.INVESTMENT, originAccount, createdAt);
        this.investmentType = investmentType;
        this.risk = risk;
        this.liquidity = liquidity;
        this.profitability = profitability;
        this.dueDate = dueDate;
        this.interestRate = interestRate;
        this.redeemed = redeemed;
    }

    public double getGrossValue() {
        return calculateValueAt(LocalDate.now());
    }

    public double getAccumulatedEarnings() {
        return getGrossValue() - getAmount();
    }

    public double calculateValueAt(LocalDate date) {
        return investmentType.getCalculator().calculateGrossValueAt(this, date);
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public InvestmentType getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(InvestmentType investmentType) {
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
