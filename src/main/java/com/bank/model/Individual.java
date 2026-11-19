package com.bank.model;

// A customer who is a person. Registered by a Banker.
// Name, email, address, cellNo, idNumber and the accounts list are inherited,
// so they are not written again here.
public class Individual extends Customer {

    // Proof of work
    private String workplace;
    private String position;
    private String startDate;
    private String endDate;      // "none" if the job is permanent
    private double salary;

    // Next of kin
    private String kinName;
    private String kinContact;

    // The first five values go up to Customer, then User
    public Individual(String name, String email, String address, int cellNo, int idNumber,
                      String workplace, String position, String startDate,
                      String endDate, double salary, String kinName, String kinContact) {
        super(name, email, address, cellNo, idNumber);
        this.workplace = workplace;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.kinName = kinName;
        this.kinContact = kinContact;
    }

    // A Cheque account can only be opened for a person who is working
    // The Banker checks this before opening a Cheque account
    public boolean isWorking() {
        return workplace != null && !workplace.trim().isEmpty();
    }

    // Getters (the fields are private for encapsulation)
    public String getWorkplace() { return workplace; }
    public String getPosition() { return position; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getSalary() { return salary; }
    public String getKinName() { return kinName; }
    public String getKinContact() { return kinContact; }

    // Setters for the details that can change
    public void setWorkplace(String workplace) { this.workplace = workplace; }
    public void setPosition(String position) { this.position = position; }
    public void setSalary(double salary) { this.salary = salary; }


    // Overrides Customer.showProfile() to add the individual's work details
    @Override
    public void showProfile() {
        super.showProfile();   // prints the shared details first
        System.out.println("Workplace: " + workplace + " (" + position + ")");
    }


}