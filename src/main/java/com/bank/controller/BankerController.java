package com.bank.controller;


import com.bank.dao.AccountDAO;
import com.bank.dao.CustomerDAO;
import com.bank.model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Handles everything the Banker page asks for.
// Each action applies the rules, saves the result with the DAO, and returns a message for the page to show.
public class BankerController {

    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final AccountDAO accountDAO = new AccountDAO();

    // Items for the two account type boxes on the page
    public static ArrayList<String> getFilterTypes() {
        ArrayList<String> types = new ArrayList<>();
        types.add("All types");
        types.add("Cheque");
        types.add("Investment");
        types.add("Savings");
        return types;
    }

    public static ArrayList<String> getAccountTypes() {
        ArrayList<String> types = getFilterTypes();
        types.remove("All types");
        return types;
    }

    // Loads the customers from the database, filtered by a name or ID number and by account type.
    // Pass an empty search and "All types" to get everyone.
    public static ArrayList<Customer> getCustomers(String search, String accountType) {
        ArrayList<Customer> result = new ArrayList<>();
        String text = search == null ? "" : search.trim().toLowerCase();

        for (Customer c : customerDAO.findAll()) {

            boolean matchesSearch = text.isEmpty()
                    || c.getName().toLowerCase().contains(text)
                    || String.valueOf(c.getIdNumber()).equals(text);

            boolean matchesType = accountType == null || accountType.equals("All types");
            if (!matchesType) {
                for (Account a : c.getAccounts()) {
                    if (a.getClass().getSimpleName().equals(accountType)) {
                        matchesType = true;
                    }
                }
            }

            if (matchesSearch && matchesType) {
                result.add(c);
            }
        }
        return result;
    }

    // Turns the typed text into a number. Returns -1 if it is not a valid positive number
    private static double parseAmount(String text) {
        try {
            double amount = Double.parseDouble(text.trim());
            return amount > 0 ? amount : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Opens a new account for a customer, applying the bank's rules
    public static String openAccount(Customer customer, String type, String depositText) {
        if (customer == null) return "Select a customer first.";
        if (type == null) return "Choose an account type.";
        double deposit = parseAmount(depositText);
        if (deposit < 0) return "Enter a valid opening deposit.";

        int number = accountDAO.nextAccountNumber();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Account account;
        switch (type) {
            case "Cheque":
                // A Cheque account is only for an individual who is working
                if (!(customer instanceof Individual) || !((Individual) customer).isWorking()) {
                    return "A Cheque account can only be opened for a working individual.";
                }
                account = new Cheque(number, deposit, "Active", date);
                break;
            case "Investment":
                if (deposit < 500) return "An Investment account needs an opening deposit of at least 500.";
                account = new Investment(number, deposit, "Active", date);
                break;
            default:
                account = new Savings(number, deposit, "Active", date);
        }

        // Record the opening deposit in the history, then save the account
        account.makeTransaction("DEPOSIT", deposit);
        accountDAO.insert(account, customer.getIdNumber());
        customer.addAccount(account);
        return type + " account " + number + " opened for " + customer.getName() + ".";
    }

    // The customer's accounts that can still be closed (for the "Close account" box)
    public static ArrayList<Account> getActiveAccounts(Customer customer) {
        ArrayList<Account> active = new ArrayList<>();
        if (customer != null) {
            for (Account a : customer.getAccounts()) {
                if (a.getAccstatus().equals("Active")) {
                    active.add(a);
                }
            }
        }
        return active;
    }

    // Closes an account. The account stays in the database with the status "Closed"
    public static String closeAccount(Account account) {
        if (account == null) return "Choose an account to close.";

        account.setAccstatus("Closed");
        accountDAO.update(account);
        return "Account " + account.getAccnumber() + " closed.";
    }

    // The history of all the customer's accounts, as text for the page to display
    public static String getHistory(Customer customer) {
        if (customer == null) return "Select a customer first.";
        if (customer.getAccounts().isEmpty()) return "This customer has no accounts yet.";

        String text = "";
        for (Account a : customer.getAccounts()) {
            text += "Account " + a.getAccnumber() + " (" + a.getClass().getSimpleName() + ")\n";
            for (String record : a.getHistory()) {
                text += "   " + record + "\n";
            }
        }
        return text;
    }

    // The customer's profile as text for the page to display
    public static String getProfile(Customer c) {
        if (c == null) return "Select a customer first.";

        String text = "Name: " + c.getName()
                + "\nEmail: " + c.getEmail()
                + "\nAddress: " + c.getAddress()
                + "\nCell: " + c.getCellNo()
                + "\nID number: " + c.getIdNumber();

        if (c instanceof Individual) {
            Individual i = (Individual) c;
            text += "\nWorkplace: " + i.getWorkplace() + " (" + i.getPosition() + ")"
                    + "\nNext of kin: " + i.getKinName() + " (" + i.getKinContact() + ")";
        } else if (c instanceof Organization) {
            Organization o = (Organization) c;
            text += "\nBusiness: " + o.getBusinessName() + "\nCIPA: " + o.getCipaCertificate();
            for (Individual s : o.getShareholders()) {
                text += "\nShareholder: " + s.getName();
            }
        }
        return text;
    }
}