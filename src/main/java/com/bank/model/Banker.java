package com.bank.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Banker extends User {

    private Scanner input = new Scanner(System.in);
    private static int nextAccNumber = 1001;

    public Banker(String name, String email, String address, int cellNo, int idNumber) {
        super(name, email, address, cellNo, idNumber);
    }

    public void BDashboard(ArrayList<Customer> customers) {
        System.out.println("You are logged in as a banker");
        int choice;
        do {
            System.out.println("\n1. Add a customer\n2. Open an Account\n3. Close an Account\n4. View Transaction History\n5. Exit");
            choice = readInt("Select your choice: ");

            switch (choice) {
                case 1: addCustomer(customers); break;
                case 2: openAccount(findCustomer(customers)); break;
                case 3: closeAccount(findCustomer(customers)); break;
                case 4: viewHistory(findCustomer(customers)); break;
                case 5: System.out.println("Logged out"); break;
                default: System.out.println("Invalid choice");
            }
        } while (choice != 5);
    }

    private void addCustomer(ArrayList<Customer> customers) {
        int type = readInt("1. Individual  2. Organization: ");
        String name = read("Name: ");
        String email = read("Email: ");
        String address = read("Address: ");
        int cellNo = readInt("Cell number: ");
        int id = readInt("ID number: ");

        if (type == 1) {
            customers.add(new Individual(name, email, address, cellNo, id,
                    read("Workplace: "), read("Position: "), read("Start date: "),
                    read("End date: "), Double.parseDouble(read("Salary: ")),
                    read("Next of kin name: "), read("Next of kin contact: ")));
        } else {
            Organization org = new Organization(name, email, address, cellNo, id,
                    read("Business name: "), read("CIPA certificate: "));
            addShareholders(org, customers);   // ask for the shareholders
            customers.add(org);
        }
        System.out.println("Customer registered: " + name);
    }

    // Adds already registered individuals as shareholders of an organization
    private void addShareholders(Organization org, ArrayList<Customer> customers) {
        int shareholderId = readInt("Shareholder ID number (0 to finish): ");

        while (shareholderId != 0) {
            // Look for a registered customer with that ID
            Customer found = null;
            for (Customer c : customers) {
                if (c.getIdNumber() == shareholderId) {
                    found = c;
                }
            }

            // Only individuals can be shareholders
            if (found instanceof Individual) {
                org.addShareholder((Individual) found);
                System.out.println("Shareholder added: " + found.getName());
            } else {
                System.out.println("No registered individual with that ID. Register them first.");
            }
            shareholderId = readInt("Shareholder ID number (0 to finish): ");
        }
    }

    private Customer findCustomer(ArrayList<Customer> customers) {
        int id = readInt("Customer ID number: ");
        for (Customer c : customers) {
            if (c.getIdNumber() == id) return c;
        }
        System.out.println("Customer not found");
        return null;
    }

    private void openAccount(Customer customer) {
        if (customer == null) return;

        int type = readInt("1. Cheque  2. Investment  3. Savings: ");
        double amount = Double.parseDouble(read("Opening deposit: "));
        String date = LocalDate.now().toString();
        int number = nextAccNumber++;

        Account account;
        switch (type) {
            case 1:
                // Cheque accounts are only for individuals who are working
                if (!(customer instanceof Individual) || !((Individual) customer).isWorking()) {
                    System.out.println("Cheque account is only for working individuals");
                    return;
                }

                account = new Cheque(number, amount, "Active", date); break;
            case 2:
                if (amount < 500) {
                    System.out.println("Investment account needs at least 500");
                    return;
                }
                account = new Investment(number, amount, "Active", date);
                break;
            case 3: account = new Savings(number, amount, "Active", date); break;
            default: System.out.println("Invalid choice"); return;
        }
        customer.addAccount(account);
        System.out.println("Account " + number + " opened");
    }

    private void closeAccount(Customer customer) {
        if (customer == null) return;

        int number = readInt("Account number to close: ");
        for (Account a : customer.getAccounts()) {
            if (a.getAccnumber() == number) {
                a.setAccstatus("Closed");
                System.out.println("Account closed");
                return;
            }
        }
        System.out.println("Account not found");
    }

    // overload: User already has viewHistory(Account), this one takes a Customer
    public void viewHistory(Customer customer) {
        if (customer == null) return;

        for (Account a : customer.getAccounts()) {
            System.out.println("Account " + a.getAccnumber());
            viewHistory(a);
        }
    }

    private String read(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }

    private int readInt(String prompt) {
        return Integer.parseInt(read(prompt));
    }
}
