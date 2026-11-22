package com.bank.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Opens the database connection and creates the tables.
// Every DAO class gets its connection from here.
public class DBConnection {

    // The database is one file (bank.db) in the project folder
    private static final String URL = "jdbc:sqlite:bank.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Creates the tables if they do not exist yet (safe to run every time)
    public static void createTables() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {

            // One table for both customer types. "type" is Individual or Organization,
            // and the columns that do not apply to a type stay empty
            st.execute("CREATE TABLE IF NOT EXISTS customers ("
                    + "id_number INTEGER PRIMARY KEY, "
                    + "type TEXT NOT NULL, "
                    + "name TEXT NOT NULL, "
                    + "email TEXT NOT NULL UNIQUE, "
                    + "address TEXT, "
                    + "cell_no INTEGER, "
                    + "workplace TEXT, position TEXT, start_date TEXT, end_date TEXT, "
                    + "salary REAL, kin_name TEXT, kin_contact TEXT, "
                    + "business_name TEXT, cipa_certificate TEXT)");

            // "type" is Cheque, Investment or Savings
            st.execute("CREATE TABLE IF NOT EXISTS accounts ("
                    + "acc_number INTEGER PRIMARY KEY, "
                    + "type TEXT NOT NULL, "
                    + "balance REAL NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "date_opened TEXT, "
                    + "customer_id INTEGER NOT NULL REFERENCES customers(id_number))");

            // One row per history line of an account
            st.execute("CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "acc_number INTEGER NOT NULL REFERENCES accounts(acc_number), "
                    + "record TEXT NOT NULL)");

            // Links an Organization to its Individual shareholders
            st.execute("CREATE TABLE IF NOT EXISTS shareholders ("
                    + "organization_id INTEGER NOT NULL REFERENCES customers(id_number), "
                    + "individual_id INTEGER NOT NULL REFERENCES customers(id_number), "
                    + "PRIMARY KEY (organization_id, individual_id))");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}