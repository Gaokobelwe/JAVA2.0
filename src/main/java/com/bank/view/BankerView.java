package com.bank.view;

import com.bank.controller.BankerController;
import com.bank.controller.LoginController;
import com.bank.model.Account;
import com.bank.model.Customer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

// Boundary class for the Banker page: shows data and collects input. No banking logic here.
public class BankerView {

    // These names must match the fx:id values in BankerView.fxml
    @FXML private Label lblWelcome;
    @FXML private Label lblMessage;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterType;
    @FXML private TableView<Customer> tblCustomers;
    @FXML private TableColumn<Customer, String> colId;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colCustType;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colAccCount;
    @FXML private ComboBox<String> cmbAccType;
    @FXML private TextField txtDeposit;
    @FXML private ComboBox<Account> cmbCustomerAccounts;
    @FXML private TextArea txtOutput;

    // Runs automatically when the page opens
    @FXML
    private void initialize() {
        lblWelcome.setText("Welcome, " + LoginController.getCurrentUser().getName());

        // Tell each column what to show for a customer
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdNumber())));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colCustType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClass().getSimpleName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colAccCount.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAccounts().size())));

        // Fill the two account type boxes
        cmbFilterType.setItems(FXCollections.observableArrayList(BankerController.getFilterTypes()));
        cmbFilterType.setValue("All types");
        cmbAccType.setItems(FXCollections.observableArrayList(BankerController.getAccountTypes()));

        // The "Close account" box shows the account number and type
        cmbCustomerAccounts.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account a) {
                return a == null ? "" : a.getAccnumber() + " " + a.getClass().getSimpleName()
                                        + " (balance " + String.format("%.2f", a.getBalance()) + ")";
            }

            @Override
            public Account fromString(String text) {
                return null;
            }
        });

        // When the banker selects a customer, the "Close account" box lists that customer's open accounts
        tblCustomers.getSelectionModel().selectedItemProperty().addListener((obs, old, customer) ->
                cmbCustomerAccounts.setItems(FXCollections.observableArrayList(
                        BankerController.getActiveAccounts(customer))));

        loadCustomers();
    }

    // Loads the table using the current search text and account type filter
    private void loadCustomers() {
        tblCustomers.setItems(FXCollections.observableArrayList(
                BankerController.getCustomers(txtSearch.getText(), cmbFilterType.getValue())));
    }

    // Reloads the table and selects the same customer again
    private void reload(int customerId) {
        loadCustomers();
        for (Customer c : tblCustomers.getItems()) {
            if (c.getIdNumber() == customerId) {
                tblCustomers.getSelectionModel().select(c);
            }
        }
    }

    private Customer selected() {
        return tblCustomers.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onSearch() {
        loadCustomers();
        lblMessage.setText(tblCustomers.getItems().size() + " customer(s) found.");
    }

    @FXML
    private void onShowAll() {
        txtSearch.clear();
        cmbFilterType.setValue("All types");
        loadCustomers();
        lblMessage.setText("Showing all customers.");
    }

    @FXML
    private void onOpenAccount() {
        Customer customer = selected();
        lblMessage.setText(BankerController.openAccount(customer, cmbAccType.getValue(), txtDeposit.getText()));
        if (customer != null) {
            reload(customer.getIdNumber());
        }
        txtDeposit.clear();
    }

    @FXML
    private void onCloseAccount() {
        Customer customer = selected();
        lblMessage.setText(BankerController.closeAccount(cmbCustomerAccounts.getValue()));
        if (customer != null) {
            reload(customer.getIdNumber());
        }
    }

    @FXML
    private void onHistory() {
        txtOutput.setText(BankerController.getHistory(selected()));
    }

    @FXML
    private void onProfile() {
        txtOutput.setText(BankerController.getProfile(selected()));
    }

    @FXML
    private void onAddCustomer() {
        SceneNavigator.show("AddCustomerView.fxml", "Add customer");
    }

    @FXML
    private void onLogout() {
        LoginController.logout();
        SceneNavigator.show("LoginView.fxml", "Banking System");
    }


}
