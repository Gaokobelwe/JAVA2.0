package com.bank.controller;


import com.bank.dao.AccountDAO;
import com.bank.model.*;
import java.util.ArrayList;

// Handles everything the Customer page asks for.
// Each action calls the model, saves the result with the DAO, and returns a message for the page to show.
public class CustomerController {

    private static final AccountDAO accountDAO = new AccountDAO();

    // The customer who is logged in
    public static Customer getCustomer() {
        return (Customer) LoginController.getCurrentUser();
    }

    public static ArrayList<Account> getAccounts() {
        return getCustomer().getAccounts();
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

    public static String deposit(Account account, String amountText) {
        if (account == null) return "Select an account first.";
        double amount = parseAmount(amountText);
        if (amount < 0) return "Enter a valid amount.";

        if (account.deposit(amount)) {
            accountDAO.update(account);
            return "Deposit successful. New balance: " + String.format("%.2f", account.getBalance());
        }
        return "Deposit failed. Check that the account is active.";
    }

    public static String withdraw(Account account, String amountText) {
        if (account == null) return "Select an account first.";
        double amount = parseAmount(amountText);
        if (amount < 0) return "Enter a valid amount.";

        if (account.withdraw(amount)) {
            accountDAO.update(account);
            return "Withdrawal successful. New balance: " + String.format("%.2f", account.getBalance());
        }

        // The model refused it: find out why, so the message is useful
        if (account instanceof Savings) return "Savings accounts do not allow withdrawals.";
        if (amount > account.getBalance()) return "Insufficient funds.";
        return "Withdrawal failed. Check that the account is active.";
    }

    public static String transfer(Account from, Account to, String amountText) {
        if (from == null) return "Select the account to transfer from.";
        if (to == null) return "Choose the account to transfer to.";
        double amount = parseAmount(amountText);
        if (amount < 0) return "Enter a valid amount.";

        if (getCustomer().moneyTransfer(from, to, amount)) {
            accountDAO.update(from);
            accountDAO.update(to);
            return "Transfer successful. New balance: " + String.format("%.2f", from.getBalance());
        }
        return "Transfer failed. Check the amount, the funds and that both accounts are active.";
    }

    // The history of one account as text for the page to display
    public static String getHistory(Account account) {
        if (account == null) return "Select an account first.";
        if (account.getHistory().isEmpty()) return "No transactions yet.";
        return String.join("\n", account.getHistory());
    }

    // The customer's profile as text for the page to display
    public static String getProfile() {
        Customer c = getCustomer();
        String text = "Name: " + c.getName()
                + "\nEmail: " + c.getEmail()
                + "\nAddress: " + c.getAddress()
                + "\nCell: " + c.getCellNo()
                + "\nID number: " + c.getIdNumber();

        if (c instanceof Individual) {
            Individual i = (Individual) c;
            text += "\nWorkplace: " + i.getWorkplace() + " (" + i.getPosition() + ")";
        } else if (c instanceof Organization) {
            Organization o = (Organization) c;
            text += "\nBusiness: " + o.getBusinessName();
            for (Individual s : o.getShareholders()) {
                text += "\nShareholder: " + s.getName();
            }
        }
        return text;
    }
}