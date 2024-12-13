package com.hammer.hammer.user.entity;

import com.hammer.hammer.transaction.entity.Transaction;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String email;
    private String password;  // 비밀번호는 해시로 저장하는 것이 좋습니다

    @OneToMany(mappedBy = "buyer")
    private List<Transaction> boughtTransactions;

    @OneToMany(mappedBy = "seller")
    private List<Transaction> soldTransactions;


    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Transaction> getBoughtTransactions() {
        return boughtTransactions;
    }

    public void setBoughtTransactions(List<Transaction> boughtTransactions) {
        this.boughtTransactions = boughtTransactions;
    }

    public List<Transaction> getSoldTransactions() {
        return soldTransactions;
    }

    public void setSoldTransactions(List<Transaction> soldTransactions) {
        this.soldTransactions = soldTransactions;
    }

}
