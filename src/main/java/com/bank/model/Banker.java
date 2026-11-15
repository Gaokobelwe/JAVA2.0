package com.bank.model;

public class Banker extends User{


    public void BDashboard(){

        System.out.println("You are logged in as a banker");

        System.out.println(
                "1. Add a customer " + " " +
                        "2.Open an Account " + " " +
                        "3. View Transection History" + " "
        );

        System.out.println("Select your choice");


    }

}
