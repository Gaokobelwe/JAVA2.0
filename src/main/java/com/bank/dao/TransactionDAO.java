package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// All database code for the transaction history lines of an account
public class TransactionDAO {

    // Saves an account's history: removes the old lines, then writes the current ones
    public void saveHistory(int accNumber, ArrayList<String> history) {
        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM transactions WHERE acc_number = ?")) {
                delete.setInt(1, accNumber);
                delete.executeUpdate();
            }

            try (PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO transactions (acc_number, record) VALUES (?, ?)")) {
                for (String record : history) {
                    insert.setInt(1, accNumber);
                    insert.setString(2, record);
                    insert.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Loads the history lines of an account, in the order they were saved
    public ArrayList<String> findHistory(int accNumber) {
        ArrayList<String> history = new ArrayList<>();
        String sql = "SELECT record FROM transactions WHERE acc_number = ? ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.add(rs.getString("record"));
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return history;
    }
}
