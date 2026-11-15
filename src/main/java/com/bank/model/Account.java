package com.bank.model;

public abstract class Account {


    protected int accnumber;
    protected double balance;
    protected String accstatus;
    protected String dateopened;

    public Account(int accnumber, double balance, String accstatus, String dateopened) {
        this.accnumber = accnumber;
        this.balance = balance;
        this.accstatus = accstatus;
        this.dateopened = dateopened;
    }

    protected Account (){

    }
}
