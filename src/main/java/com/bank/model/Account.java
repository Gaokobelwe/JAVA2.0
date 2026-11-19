package com.bank.model;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class Account {

    private int accnumber;
    private double balance;
    private String accstatus;
    private String dateopened;
    private ArrayList<String> history = new ArrayList<>();

    public Account(int accnumber, double balance, String accstatus, String dateopened) {
        this.accnumber = accnumber;
        this.balance = balance;
        this.accstatus = accstatus;
        this.dateopened = dateopened;
    }

    protected Account() {
    }

    // getters and setters (the way subclasses reach the private fields)
    public int getAccnumber() { return accnumber; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getAccstatus() { return accstatus; }
    public void setAccstatus(String accstatus) { this.accstatus = accstatus; }
    public String getDateopened() { return dateopened; }
    public ArrayList<String> getHistory() { return history; }

    // behaviours from your diagram

    // Adds money to this account. Returns true if it worked
    public boolean deposit(double amount) {
        if (amount <= 0 || !accstatus.equals("Active")) return false;
        balance += amount;
        makeTransaction("DEPOSIT", amount);
        return true;
    }

    // Takes money out of this account. Returns true if it worked
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance || !accstatus.equals("Active")) return false;
        balance -= amount;
        makeTransaction("WITHDRAWAL", amount);
        return true;
    }


    public double checkBalance() {
        return balance;
    }

    public void makeTransaction(String type, double amount) {
        history.add(type + ": " + amount + " | Balance: " + balance);
    }




    // Overload: moves money from THIS account to another account
    // Returns true if it worked, false if it was refused
    public boolean makeTransaction(Account to, double amount) {

        // Refuse if the destination is missing or is this same account
        if (to == null || to == this) return false;

        // Refuse if the amount is invalid or the funds are too low
        if (amount <= 0 || amount > balance) return false;

        // Refuse if either account is not active
        if (!accstatus.equals("Active") || !to.getAccstatus().equals("Active")) return false;

        // Move the money
        balance -= amount;
        to.setBalance(to.getBalance() + amount);

        // Record both sides using the existing makeTransaction(type, amount)
        makeTransaction("TRANSFER OUT", amount);
        to.makeTransaction("TRANSFER IN", amount);
        return true;
    }


}

