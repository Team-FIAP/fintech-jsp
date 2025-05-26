package com.fiap.fintechjsp.service;

import com.fiap.fintechjsp.model.Investment;

import java.time.LocalDate;

public interface InvestmentCalculator {
    double calculateGrossValueAt(Investment investment, LocalDate date);
}
