package com.fiap.fintechjsp.service;

import com.fiap.fintechjsp.model.Investment;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class FixedRateCalculator implements InvestmentCalculator {
    /**
     * Calcula o valor final de um investimento com taxa pré-fixada.
     * Fórmula: M = P × (1 + i)^n
     *
     * @param investment investimento a ser calculado
     * @param date data final para o cálculo (ex: hoje)
     * @return valor bruto acumulado
     */
    @Override
    public double calculateGrossValueAt(Investment investment, LocalDate date) {
        long days = ChronoUnit.DAYS.between(investment.getDate(), date);
        if (days <= 0) return investment.getAmount();

        double dailyTax = (investment.getInterestRate() / 100) / 365.0;
        return investment.getAmount() * Math.pow(1 + dailyTax, days);
    }
}
