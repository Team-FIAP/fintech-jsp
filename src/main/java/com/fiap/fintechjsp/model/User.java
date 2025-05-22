package com.fiap.fintechjsp.model;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String cpf;
    private LocalDateTime createdAt;

    public User(Long id, String name, String email, String password, String cpf, LocalDateTime createdAt) {
        this(name, email, password, cpf);
        this.id = id;
        this.createdAt = createdAt;
    }

    public User(String name, String username, String password, String cpf) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.cpf = cpf;
    }

    public User(Long id, String name, String email, String password, String cpf) {
        this(name, email, password, cpf);
        this.id = id;
    }

    public User() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
