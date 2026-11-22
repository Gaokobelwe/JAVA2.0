package com.bank.view;

import com.bank.controller.AddCustomerController;
import com.bank.model.Individual;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import java.util.ArrayList;

// Boundary class for the Add customer page: collects input and shows messages. No banking logic here.
public class AddCustomerView {

    // These names must match the fx:id values in AddCustomerView.fxml
    @FXML private RadioButton rbIndividual;
    @FXML private RadioButton rbOrganization;
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private TextField txtCellNo;
    @FXML private TextField txtIdNumber;
    @FXML private TextField txtWorkplace;
    @FXML private TextField txtPosition;
    @FXML private TextField txtStartDate;
    @FXML private TextField txtEndDate;
    @FXML private TextField txtSalary;
    @FXML private TextField txtKinName;
    @FXML private TextField txtKinContact;
    @FXML private TextField txtBusinessName;
    @FXML private TextField txtCipa;
    @FXML private TextField txtShareholderId;
    @FXML private Button btnAddShareholder;
    @FXML private ListView<String> lstShareholders;
    @FXML private Label lblMessage;

    // The shareholders added so far (only used for an Organization)
    private ArrayList<Individual> shareholders = new ArrayList<>();

    // Runs automatically when the page opens
    @FXML
    private void initialize() {
        // Switch the matching fields on and off whenever the customer type changes
        rbOrganization.selectedProperty().addListener((obs, old, isOrganization) -> updateFields(isOrganization));
        updateFields(false);
    }

    // Individual: the individual fields are on. Organization: the organization fields are on
    private void updateFields(boolean isOrganization) {
        txtWorkplace.setDisable(isOrganization);
        txtPosition.setDisable(isOrganization);
        txtStartDate.setDisable(isOrganization);
        txtEndDate.setDisable(isOrganization);
        txtSalary.setDisable(isOrganization);
        txtKinName.setDisable(isOrganization);
        txtKinContact.setDisable(isOrganization);

        txtBusinessName.setDisable(!isOrganization);
        txtCipa.setDisable(!isOrganization);
        txtShareholderId.setDisable(!isOrganization);
        btnAddShareholder.setDisable(!isOrganization);
        lstShareholders.setDisable(!isOrganization);
    }

    @FXML
    private void onAddShareholder() {
        // The controller checks that the ID belongs to a registered individual
        Individual found = AddCustomerController.findIndividual(txtShareholderId.getText());

        if (found == null) {
            lblMessage.setText("No registered individual with that ID number.");
            return;
        }

        for (Individual s : shareholders) {
            if (s.getIdNumber() == found.getIdNumber()) {
                lblMessage.setText("That shareholder is already in the list.");
                return;
            }
        }

        shareholders.add(found);
        lstShareholders.getItems().add(found.getIdNumber() + "  " + found.getName());
        txtShareholderId.clear();
        lblMessage.setText("Shareholder added: " + found.getName());
    }

    @FXML
    private void onSave() {
        String error;

        if (rbIndividual.isSelected()) {
            error = AddCustomerController.registerIndividual(
                    txtName.getText(), txtEmail.getText(), txtAddress.getText(),
                    txtCellNo.getText(), txtIdNumber.getText(),
                    txtWorkplace.getText(), txtPosition.getText(),
                    txtStartDate.getText(), txtEndDate.getText(), txtSalary.getText(),
                    txtKinName.getText(), txtKinContact.getText());
        } else {
            error = AddCustomerController.registerOrganization(
                    txtName.getText(), txtEmail.getText(), txtAddress.getText(),
                    txtCellNo.getText(), txtIdNumber.getText(),
                    txtBusinessName.getText(), txtCipa.getText(), shareholders);
        }

        if (error != null) {
            lblMessage.setText(error);
            return;
        }

        lblMessage.setText("Customer saved. You can add another or click Cancel to go back.");
        clearFields();
    }

    @FXML
    private void onCancel() {
        SceneNavigator.show("BankerView.fxml", "Banker");
    }

    // Empties every field after a successful save
    private void clearFields() {
        txtName.clear();
        txtEmail.clear();
        txtAddress.clear();
        txtCellNo.clear();
        txtIdNumber.clear();
        txtWorkplace.clear();
        txtPosition.clear();
        txtStartDate.clear();
        txtEndDate.clear();
        txtSalary.clear();
        txtKinName.clear();
        txtKinContact.clear();
        txtBusinessName.clear();
        txtCipa.clear();
        txtShareholderId.clear();
        shareholders.clear();
        lstShareholders.getItems().clear();
    }
}