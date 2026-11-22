package com.bank.controller;


import com.bank.dao.CustomerDAO;
import com.bank.model.Customer;
import com.bank.model.Individual;
import com.bank.model.Organization;
import java.util.ArrayList;

// Handles the Add customer page: checks the details, builds the customer and saves it.
// Every method returns an error message, or null if everything worked.
public class AddCustomerController {

    private static final CustomerDAO customerDAO = new CustomerDAO();

    // Turns typed text into a whole number. Returns -1 if it is not a valid number
    private static int parseNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Looks up a registered individual by ID number (for the shareholder list).
    // Returns null if the ID is invalid or does not belong to an individual
    public static Individual findIndividual(String idText) {
        int id = parseNumber(idText);
        if (id < 1) return null;

        Customer found = customerDAO.findById(id);
        return found instanceof Individual ? (Individual) found : null;
    }

    // The checks that every customer type needs
    private static String checkBasics(String name, String email, String address, String cellText, String idText) {
        if (isBlank(name)) return "Enter the name.";
        if (isBlank(email) || !email.contains("@")) return "Enter a valid email.";
        if (isBlank(address)) return "Enter the address.";
        if (parseNumber(cellText) < 1) return "Enter a valid cell number.";

        int id = parseNumber(idText);
        if (id < 1) return "Enter a valid ID number.";
        if (customerDAO.findById(id) != null) return "That ID number is already registered.";
        if (customerDAO.findByEmail(email.trim()) != null) return "That email is already registered.";
        return null;
    }

    public static String registerIndividual(String name, String email, String address, String cellText,
                                            String idText, String workplace, String position,
                                            String startDate, String endDate, String salaryText,
                                            String kinName, String kinContact) {

        String error = checkBasics(name, email, address, cellText, idText);
        if (error != null) return error;

        if (isBlank(kinName) || isBlank(kinContact)) return "Enter the next of kin name and contact.";

        // The salary may be left empty, but if it is typed it must be a valid number
        double salary = 0;
        if (!isBlank(salaryText)) {
            try {
                salary = Double.parseDouble(salaryText.trim());
            } catch (NumberFormatException e) {
                return "Enter a valid salary.";
            }
            if (salary < 0) return "Enter a valid salary.";
        }

        // A permanent job has no end date
        String end = isBlank(endDate) ? "none" : endDate.trim();

        Individual individual = new Individual(name.trim(), email.trim(), address.trim(),
                parseNumber(cellText), parseNumber(idText), workplace.trim(), position.trim(),
                startDate.trim(), end, salary, kinName.trim(), kinContact.trim());

        if (!customerDAO.insert(individual)) {
            return "The customer could not be saved. Check the details and try again.";
        }
        return null;
    }

    public static String registerOrganization(String name, String email, String address, String cellText,
                                              String idText, String businessName, String cipa,
                                              ArrayList<Individual> shareholders) {

        String error = checkBasics(name, email, address, cellText, idText);
        if (error != null) return error;

        if (isBlank(businessName)) return "Enter the business name.";
        if (isBlank(cipa)) return "Enter the CIPA certificate.";
        if (shareholders.isEmpty()) return "Add at least one shareholder.";

        Organization organization = new Organization(name.trim(), email.trim(), address.trim(),
                parseNumber(cellText), parseNumber(idText), businessName.trim(), cipa.trim());

        for (Individual shareholder : shareholders) {
            organization.addShareholder(shareholder);
        }

        // CustomerDAO.insert also saves the shareholders
        if (!customerDAO.insert(organization)) {
            return "The organization could not be saved. Check the details and try again.";
        }
        return null;
    }
}