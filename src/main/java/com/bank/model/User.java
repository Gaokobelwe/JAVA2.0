package com.bank.model;

import java.util.Random;
import java.util.Scanner;

public abstract class User {
    //atributes

    private String name;
    private String email;
    private String address;
    private int cellNo;
    private int idNumber;

    //Constructor

    public User(String name, String email,String address, int cellNo, int idNumber) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.cellNo = cellNo;
        this.idNumber = idNumber;
    }

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress(){return address; }
    public void setAddress(String address) {this.address = address;}
    public int getCellNo() { return cellNo; }
    public void setCellNo(int cellNo) { this.cellNo = cellNo; }
    public int getIdNumber() { return idNumber; }

    public void logIn(){

    }

    public void viewHistory(Account account) {
        for (String record : account.getHistory()) {
            System.out.println(record);
        }
    }


}
