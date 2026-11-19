package com.bank.model;

public class Savings extends Account implements AddInterest , withdrawal {

    private static final double INTEREST_RATE = 0.05;

    public Savings(int accnumber, double balance, String accstatus, String dateopened) {
        super(accnumber, balance, accstatus, dateopened);
    }

    @Override
    public void addInterest() {
        double amount = getBalance() * INTEREST_RATE / 100;
        setBalance(getBalance() + amount);
        makeTransaction("INTEREST", amount);
    }
    // Savings accounts do not allow withdrawals
    @Override
    public boolean withdraw(double amount) {
        return false;
    }


}
