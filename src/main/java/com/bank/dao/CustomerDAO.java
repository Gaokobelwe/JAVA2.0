package com.bank.dao;

import com.bank.model.*;
import java.sql.*;
import java.util.ArrayList;

// All database code for customers (Individual and Organization)
public class CustomerDAO {

    private AccountDAO accountDAO = new AccountDAO();

    // Create: saves a new customer. Returns false if it failed (for example the email or ID already exists)
    public boolean insert(Customer customer) {
        String sql = "INSERT INTO customers (id_number, type, name, email, address, cell_no, "
                + "workplace, position, start_date, end_date, salary, kin_name, kin_contact, "
                + "business_name, cipa_certificate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customer.getIdNumber());
            ps.setString(2, customer.getClass().getSimpleName());   // Individual or Organization
            ps.setString(3, customer.getName());
            ps.setString(4, customer.getEmail());
            ps.setString(5, customer.getAddress());
            ps.setInt(6, customer.getCellNo());

            if (customer instanceof Individual) {
                Individual i = (Individual) customer;
                ps.setString(7, i.getWorkplace());
                ps.setString(8, i.getPosition());
                ps.setString(9, i.getStartDate());
                ps.setString(10, i.getEndDate());
                ps.setDouble(11, i.getSalary());
                ps.setString(12, i.getKinName());
                ps.setString(13, i.getKinContact());
                ps.setString(14, null);
                ps.setString(15, null);
            } else {
                Organization o = (Organization) customer;
                ps.setString(7, null);
                ps.setString(8, null);
                ps.setString(9, null);
                ps.setString(10, null);
                ps.setNull(11, Types.REAL);
                ps.setString(12, null);
                ps.setString(13, null);
                ps.setString(14, o.getBusinessName());
                ps.setString(15, o.getCipaCertificate());
            }
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }

        // An organization also saves its shareholders
        if (customer instanceof Organization) {
            for (Individual s : ((Organization) customer).getShareholders()) {
                saveShareholder(customer.getIdNumber(), s.getIdNumber());
            }
        }
        return true;
    }

    // Links an individual to an organization as a shareholder
    public void saveShareholder(int organizationId, int individualId) {
        String sql = "INSERT OR IGNORE INTO shareholders (organization_id, individual_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, organizationId);
            ps.setInt(2, individualId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Read: finds one customer by ID number (null if not found)
    public Customer findById(int idNumber) {
        String sql = "SELECT * FROM customers WHERE id_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return fromRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    // Read: finds one customer by email (null if not found). Used by the login
    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return fromRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    // Read: loads every customer, with their accounts
    public ArrayList<Customer> findAll() {
        ArrayList<Customer> customers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers ORDER BY id_number");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(fromRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return customers;
    }

    // Update: changes the basic details of a customer
    public void update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, email = ?, address = ?, cell_no = ? WHERE id_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getAddress());
            ps.setInt(4, customer.getCellNo());
            ps.setInt(5, customer.getIdNumber());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Delete: removes a customer, their accounts and their shareholder links
    public void delete(int idNumber) {
        for (Account a : accountDAO.findByCustomer(idNumber)) {
            accountDAO.delete(a.getAccnumber());
        }

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM shareholders WHERE organization_id = ? OR individual_id = ?")) {
                ps.setInt(1, idNumber);
                ps.setInt(2, idNumber);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE id_number = ?")) {
                ps.setInt(1, idNumber);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Builds an Individual or Organization from one database row,
    // then loads their accounts (and shareholders for an organization)
    private Customer fromRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_number");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String address = rs.getString("address");
        int cellNo = rs.getInt("cell_no");

        Customer customer;
        if (rs.getString("type").equals("Individual")) {
            customer = new Individual(name, email, address, cellNo, id,
                    rs.getString("workplace"), rs.getString("position"),
                    rs.getString("start_date"), rs.getString("end_date"),
                    rs.getDouble("salary"), rs.getString("kin_name"), rs.getString("kin_contact"));
        } else {
            customer = new Organization(name, email, address, cellNo, id,
                    rs.getString("business_name"), rs.getString("cipa_certificate"));
        }

        for (Account a : accountDAO.findByCustomer(id)) {
            customer.addAccount(a);
        }

        if (customer instanceof Organization) {
            loadShareholders((Organization) customer);
        }
        return customer;
    }

    // Puts the saved shareholders back into an organization
    private void loadShareholders(Organization org) {
        String sql = "SELECT individual_id FROM shareholders WHERE organization_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, org.getIdNumber());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Customer found = findById(rs.getInt("individual_id"));
                if (found instanceof Individual) {
                    org.addShareholder((Individual) found);
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}

