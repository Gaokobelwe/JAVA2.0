package com.bank.controller;

import com.bank.dao.CustomerDAO;
import com.bank.model.Banker;
import com.bank.model.Customer;
import com.bank.model.User;

// Checks the login request from the login page and remembers who is logged in
public class LoginController {

    private static final CustomerDAO customerDAO = new CustomerDAO();

    // The one banker account (bankers are not stored in the database)
    private static final Banker admin = new Banker("Admin", "admin@bank.com", "Gaborone", 71000000, 1);

    private static User currentUser;

    // Returns null if the login worked, otherwise the error message to show.
    // The OTP is not checked yet (it comes later with the email part).
    public static String login(String role, String email, String otp) {
        if (email == null || email.trim().isEmpty()) {
            return "Enter your username (email).";
        }

        if (role.equals("Banker")) {
            if (email.trim().equalsIgnoreCase(admin.getEmail())) {
                currentUser = admin;
                return null;
            }
            return "Banker not found.";
        }

        // Customers are looked up in the database
        Customer customer = customerDAO.findByEmail(email.trim());
        if (customer == null) {
            return "Customer not found.";
        }
        currentUser = customer;
        return null;
    }

    public static User getCurrentUser() { return currentUser; }

    public static void logout() { currentUser = null; }
}