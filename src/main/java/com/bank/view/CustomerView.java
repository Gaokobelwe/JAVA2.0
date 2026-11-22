package com.bank.view;


import com.bank.controller.CustomerController;
import com.bank.controller.LoginController;
import com.bank.model.Account;
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

// Boundary class for the Customer page: shows data and collects input. No banking logic here.
public class CustomerView {

    // These names must match the fx:id values in CustomerView.fxml
    @FXML private Label lblWelcome;
    @FXML private Label lblMessage;
    @FXML private TableView<Account> tblAccounts;
    @FXML private TableColumn<Account, String> colAccNo;
    @FXML private TableColumn<Account, String> colType;
    @FXML private TableColumn<Account, String> colStatus;
    @FXML private TableColumn<Account, String> colBalance;
    @FXML private TextField txtAmount;
    @FXML private ComboBox<Account> cmbTransferTo;
    @FXML private TextArea txtOutput;

    // Runs automatically when the page opens
    @FXML
    private void initialize() {
        lblWelcome.setText("Welcome, " + CustomerController.getCustomer().getName());

        // Tell each column what to show for an account
        colAccNo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAccnumber())));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClass().getSimpleName()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccstatus()));
        colBalance.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getBalance())));

        // The "Transfer to" box shows the account number and type
        cmbTransferTo.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account a) {
                return a == null ? "" : a.getAccnumber() + " " + a.getClass().getSimpleName();
            }

            @Override
            public Account fromString(String text) {
                return null;
            }
        });

        refresh(null);
    }

    // Reloads the table and the "Transfer to" box, then selects the given account again
    private void refresh(Account toSelect) {
        tblAccounts.setItems(FXCollections.observableArrayList(CustomerController.getAccounts()));
        cmbTransferTo.setItems(FXCollections.observableArrayList(CustomerController.getAccounts()));
        if (toSelect != null) {
            tblAccounts.getSelectionModel().select(toSelect);

            tblAccounts.refresh();
        }
    }

    private Account selected() {
        return tblAccounts.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onDeposit() {
        Account account = selected();
        lblMessage.setText(CustomerController.deposit(account, txtAmount.getText()));
        refresh(account);
        txtAmount.clear();
    }

    @FXML
    private void onWithdraw() {
        Account account = selected();
        lblMessage.setText(CustomerController.withdraw(account, txtAmount.getText()));
        refresh(account);
        txtAmount.clear();
    }

    @FXML
    private void onTransfer() {
        Account from = selected();
        lblMessage.setText(CustomerController.transfer(from, cmbTransferTo.getValue(), txtAmount.getText()));
        refresh(from);
        txtAmount.clear();
    }

    @FXML
    private void onHistory() {
        txtOutput.setText(CustomerController.getHistory(selected()));
    }

    @FXML
    private void onProfile() {
        txtOutput.setText(CustomerController.getProfile());
    }

    @FXML
    private void onLogout() {
        LoginController.logout();
        SceneNavigator.show("LoginView.fxml", "Banking System");
    }
}
