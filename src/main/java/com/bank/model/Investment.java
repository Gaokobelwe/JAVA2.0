package com.bank.model;

public class Investment extends Account implements AddInterest {


    private static final double INTEREST_RATE = 5;

    public Investment(int accnumber, double balance, String accstatus, String dateopened) {
        super(accnumber, balance, accstatus, dateopened);
    }

    @Override
    public void addInterest() {
        double amount = getBalance() * INTEREST_RATE / 100;
        setBalance(getBalance() + amount);
        makeTransaction("INTEREST", amount);
    }

}
