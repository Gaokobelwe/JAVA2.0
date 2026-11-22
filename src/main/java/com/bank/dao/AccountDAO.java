package com.bank.dao;

import com.bank.model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// All database code for accounts (Cheque, Investment and Savings)
public class AccountDAO {

    private TransactionDAO transactionDAO = new TransactionDAO();

    // Create: saves a new account for a customer, with its history
    public void insert(Account account, int customerId) {
        String sql = "INSERT INTO accounts (acc_number, type, balance, status, date_opened, customer_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getAccnumber());
            ps.setString(2, account.getClass().getSimpleName());   // Cheque, Investment or Savings
            ps.setDouble(3, account.getBalance());
            ps.setString(4, account.getAccstatus());
            ps.setString(5, account.getDateopened());
            ps.setInt(6, customerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        transactionDAO.saveHistory(account.getAccnumber(), account.getHistory());
    }

    // Update: saves the new balance, status and history (after a deposit, withdrawal, closing...)
    public void update(Account account) {
        String sql = "UPDATE accounts SET balance = ?, status = ? WHERE acc_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.getAccstatus());
            ps.setInt(3, account.getAccnumber());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        transactionDAO.saveHistory(account.getAccnumber(), account.getHistory());
    }

    // Read: loads all the accounts of one customer
    public ArrayList<Account> findByCustomer(int customerId) {
        ArrayList<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int number = rs.getInt("acc_number");
                double balance = rs.getDouble("balance");
                String status = rs.getString("status");
                String date = rs.getString("date_opened");

                // Build the right subclass from the saved type
                Account account;
                switch (rs.getString("type")) {
                    case "Cheque": account = new Cheque(number, balance, status, date); break;
                    case "Investment": account = new Investment(number, balance, status, date); break;
                    default: account = new Savings(number, balance, status, date);
                }

                // Put the saved history lines back into the account
                account.getHistory().addAll(transactionDAO.findHistory(number));
                accounts.add(account);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return accounts;
    }

    // Delete: removes an account and its history
    public void delete(int accNumber) {
        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE acc_number = ?")) {
                ps.setInt(1, accNumber);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM accounts WHERE acc_number = ?")) {
                ps.setInt(1, accNumber);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Gives the next free account number (starts at 1001), so numbers never repeat after a restart
    public int nextAccountNumber() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT MAX(acc_number) FROM accounts");
             ResultSet rs = ps.executeQuery()) {

            int max = rs.getInt(1);   // 0 when the table is empty
            return max < 1001 ? 1001 : max + 1;

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return 1001;
        }
    }
}