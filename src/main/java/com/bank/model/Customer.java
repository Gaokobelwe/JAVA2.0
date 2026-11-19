package com.bank.model;

import java.util.ArrayList;
import java.util.Scanner;

// Abstract parent of Individual and Organization.
// Customers are created by a Banker, never directly from this class.
public abstract class Customer extends User {

    // The accounts this customer owns (a customer can have many)
    private ArrayList<Account> accounts = new ArrayList<>();
    private Scanner input = new Scanner(System.in);

    // Passes the shared details up to User, so they are not rewritten here
    public Customer(String name, String email, String address, int cellNo, int idNumber) {
        super(name, email, address, cellNo, idNumber);
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    // Called by the Banker when an account is opened for this customer
    public void addAccount(Account account) {
        accounts.add(account);
    }

    // Looks for one of the customer's accounts by its number (null if not found)
    public Account findAccount(int accnumber) {
        for (Account a : accounts) {
            if (a.getAccnumber() == accnumber) {
                return a;
            }
        }
        return null;
    }

    // The customer asks the sending account to make the transaction
    public boolean moneyTransfer(Account from, Account to, double amount) {
        if (from == null) return false;
        return from.makeTransaction(to, amount);
    }

    // ---------- Customer dashboard (the only menu method other classes call) ----------
    public void CDashboard() {
        System.out.println("Welcome " + getName());
        int choice;
        do {
            System.out.println("\n--- CUSTOMER MENU ---");
            System.out.println("1. View my accounts");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer between my accounts");
            System.out.println("5. Check balance");
            System.out.println("6. View transaction history");
            System.out.println("7. View my profile");
            System.out.println("8. Log out");
            choice = readInt("Select your choice: ");

            switch (choice) {
                case 1: showAccounts(); break;
                case 2: deposit(); break;
                case 3: withdraw(); break;
                case 4: transfer(); break;
                case 5: checkBalance(); break;
                case 6: showHistory(); break;
                case 7: showProfile(); break;
                case 8: System.out.println("Logged out"); break;
                default: System.out.println("Invalid choice");
            }
        } while (choice != 8);
    }

    // ---------- Menu actions (private: only the dashboard uses them) ----------
    private void showAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts yet. Ask a banker to open one.");
            return;
        }
        for (Account a : accounts) {
            System.out.println(a.getAccnumber() + " | " + a.getClass().getSimpleName()
                    + " | " + a.getAccstatus() + " | Balance: " + a.checkBalance());
        }
    }

    // Shows the accounts and returns the one the customer picks (null if none)
    private Account pickAccount(String label) {
        if (accounts.isEmpty()) {
            System.out.println("You have no accounts yet. Ask a banker to open one.");
            return null;
        }
        showAccounts();
        Account a = findAccount(readInt(label));
        if (a == null) {
            System.out.println("Account not found");
        }
        return a;
    }

    private void deposit() {
        Account a = pickAccount("Account number to deposit into: ");
        if (a == null) return;

        if (a.deposit(readDouble("Amount: "))) {
            System.out.println("Deposit successful. Balance: " + a.checkBalance());
        } else {
            System.out.println("Deposit failed (invalid amount or account not active)");
        }
    }

    private void withdraw() {
        Account a = pickAccount("Account number to withdraw from: ");
        if (a == null) return;

        if (a.withdraw(readDouble("Amount: "))) {
            System.out.println("Withdrawal successful. Balance: " + a.checkBalance());
        } else {
            System.out.println("Withdrawal failed (savings accounts cannot withdraw, "
                    + "or invalid amount, insufficient funds, or account not active)");
        }
    }

    private void transfer() {
        Account from = pickAccount("Transfer FROM account number: ");
        if (from == null) return;
        Account to = findAccount(readInt("Transfer TO account number: "));
        if (to == null) {
            System.out.println("Account not found");
            return;
        }

        if (moneyTransfer(from, to, readDouble("Amount: "))) {
            System.out.println("Transfer successful");
        } else {
            System.out.println("Transfer failed (invalid amount, insufficient funds, or account not active)");
        }
    }

    private void checkBalance() {
        Account a = pickAccount("Account number: ");
        if (a != null) {
            System.out.println("Balance: " + a.checkBalance());
        }
    }

    private void showHistory() {
        Account a = pickAccount("Account number: ");
        if (a == null) return;

        if (a.getHistory().isEmpty()) {
            System.out.println("No transactions yet");
        }
        for (String record : a.getHistory()) {
            System.out.println(record);
        }
    }

    // Shows the details every customer has.
    // Individual and Organization override this to add their own details.
    public void showProfile() {
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Address: " + getAddress());
        System.out.println("Cell: " + getCellNo());
        System.out.println("ID number: " + getIdNumber());
    }

    // ---------- Input helpers (ask again instead of crashing on wrong input) ----------
    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a whole number");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a number");
            }
        }
    }
}