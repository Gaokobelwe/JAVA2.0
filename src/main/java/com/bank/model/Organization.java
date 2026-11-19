package com.bank.model;

import java.util.ArrayList;

// A customer that is a company or organization. Registered by a Banker.
// Name, email, address, cellNo, idNumber and the accounts list are inherited,
// so they are not written again here.
public class Organization extends Customer {

    private String businessName;
    private String cipaCertificate;

    // The individuals who own the organization
    private ArrayList<Individual> shareholders = new ArrayList<>();

    // The first five values go up to Customer, then User
    public Organization(String name, String email, String address, int cellNo, int idNumber,
                        String businessName, String cipaCertificate) {
        super(name, email, address, cellNo, idNumber);
        this.businessName = businessName;
        this.cipaCertificate = cipaCertificate;
    }

    // Adds a registered individual as a shareholder
    public void addShareholder(Individual shareholder) {
        shareholders.add(shareholder);
    }

    // Getters (the fields are private for encapsulation)
    public String getBusinessName() { return businessName; }
    public String getCipaCertificate() { return cipaCertificate; }
    public ArrayList<Individual> getShareholders() { return shareholders; }

    // Overrides Customer.showProfile() to add the business details
    @Override
    public void showProfile() {
        super.showProfile();   // prints the shared details first
        System.out.println("Business: " + businessName);
        for (Individual s : shareholders) {
            System.out.println("Shareholder: " + s.getName());
        }
    }

}