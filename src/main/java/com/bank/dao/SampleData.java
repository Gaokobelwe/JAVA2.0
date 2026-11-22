package com.bank.dao;

import com.bank.model.*;
import java.util.ArrayList;

// Inserts 10 sample individuals and 2 organizations (with accounts) into an empty database
public class SampleData {

    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final String DATE = "20/11/2026";

    public static void insertIfEmpty() {

        // Do nothing if the database already has customers
        if (!customerDAO.findAll().isEmpty()) return;

        String[] names = {"Kewinne Gaokobelwe", "Thabo Molefe", "Neo Kgosi", "Lesego Dube",
                "Mpho Seretse", "Boitumelo Phiri", "Kabelo Modise", "Tshepo Ramotswe",
                "Onalenna Tau", "Gorata Mosweu"};
        String[] emails = {"kewinne@mail.com", "thabo@mail.com", "neo@mail.com", "lesego@mail.com",
                "mpho@mail.com", "boitumelo@mail.com", "kabelo@mail.com", "tshepo@mail.com",
                "onalenna@mail.com", "gorata@mail.com"};
        String[] workplaces = {"BAC", "Debswana", "Choppies", "Mascom", "BTC",
                "Sefalana", "FNB", "Orange", "Stanbic", "Letshego"};
        String[] positions = {"Lecturer", "Engineer", "Manager", "Technician", "Analyst",
                "Clerk", "Teller", "Developer", "Accountant", "Officer"};

        // 10 individuals, each with a Cheque and a Savings account (every second one also an Investment)
        ArrayList<Individual> people = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Individual person = new Individual(names[i], emails[i], "Gaborone", 71230001 + i, 1001 + i,
                    workplaces[i], positions[i], "01/02/2024", "none", 6000 + i * 500,
                    "Next of kin " + (i + 1), String.format("7200%04d", i + 1));
            customerDAO.insert(person);
            people.add(person);

            open(person, new Cheque(accountDAO.nextAccountNumber(), 2000 + i * 100, "Active", DATE));
            open(person, new Savings(accountDAO.nextAccountNumber(), 1000 + i * 100, "Active", DATE));
            if (i % 2 == 0) {
                open(person, new Investment(accountDAO.nextAccountNumber(), 500 + i * 100, "Active", DATE));
            }
        }

        // 2 organizations, each with shareholders chosen from the individuals above
        Organization traders = new Organization("Kgosi Traders", "info@kgositraders.co.bw", "Francistown",
                71240001, 2001, "Kgosi Traders (Pty) Ltd", "CIPA-2024-0001");
        traders.addShareholder(people.get(2));
        traders.addShareholder(people.get(3));
        customerDAO.insert(traders);
        open(traders, new Savings(accountDAO.nextAccountNumber(), 5000, "Active", DATE));
        open(traders, new Investment(accountDAO.nextAccountNumber(), 3000, "Active", DATE));

        Organization builders = new Organization("Modise Builders", "info@modisebuilders.co.bw", "Maun",
                71240002, 2002, "Modise Builders (Pty) Ltd", "CIPA-2024-0002");
        builders.addShareholder(people.get(6));
        builders.addShareholder(people.get(7));
        customerDAO.insert(builders);
        open(builders, new Savings(accountDAO.nextAccountNumber(), 8000, "Active", DATE));
        open(builders, new Investment(accountDAO.nextAccountNumber(), 4000, "Active", DATE));
    }

    // Records the opening deposit in the account's history, then saves the account
    private static void open(Customer customer, Account account) {
        account.makeTransaction("DEPOSIT", account.getBalance());
        accountDAO.insert(account, customer.getIdNumber());
    }
}