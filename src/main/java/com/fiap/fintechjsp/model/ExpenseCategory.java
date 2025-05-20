package com.fiap.fintechjsp.model;

import java.time.LocalDateTime;

public class ExpenseCategory {
    private Long id;
    private String name;
    private ExpenseCategoryType type;
    private String color;
    private String icon;
    private final LocalDateTime createdAt;

    public ExpenseCategory(Long id, String name, ExpenseCategoryType type, String color, String icon, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExpenseCategoryType getType() {
        return type;
    }

    public void setType(ExpenseCategoryType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
