package de.evaspringbuch.eva11mobpay.usermobpay.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import de.evaspringbuch.eva11mobpay.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Account extends BaseEntity<Long> {

    private double balance;

    @JsonBackReference
    @OneToOne(mappedBy = "account")
    private PayUser payUser;

    public Account() {
        this.balance = 100;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public PayUser getPayUser() {
        return payUser;
    }

    public void setPayUser(PayUser payUser) {
        this.payUser = payUser;
    }

    public void depositBalance(double amount) {
        this.balance += amount;
    }

    public void withdrawBalance(double amount) {
        this.balance -= amount;
    }
}