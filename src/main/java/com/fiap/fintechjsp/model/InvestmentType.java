package com.fiap.fintechjsp.model;

import com.fiap.fintechjsp.service.InvestmentCalculator;
import com.fiap.fintechjsp.service.FixedRateCalculator;

public enum InvestmentType {
    CDB("CDB", new FixedRateCalculator()),
    LCI("LCI", new FixedRateCalculator()),
    LCA("LCA", new FixedRateCalculator());

    private final String description;
    private final InvestmentCalculator calculator;

    InvestmentType(String description, InvestmentCalculator calculator) {
        this.description = description;
        this.calculator = calculator;
    }

    public String getDescription() {
        return description;
    }

    public InvestmentCalculator getCalculator() {
        return calculator;
    }
}
