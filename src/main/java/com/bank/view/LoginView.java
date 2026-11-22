package com.bank.view;

import com.bank.controller.LoginController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

// Boundary class: reads the fields and shows messages. No banking logic here.
public class LoginView {

    // These names must match the fx:id values in LoginView.fxml
    @FXML private RadioButton rbBanker;
    @FXML private RadioButton rbCustomer;
    @FXML private TextField txtUsername;
    @FXML private TextField txtOtp;

    // Runs when the LogIn button is clicked
    @FXML
    private void onLogin() {
        String role = rbBanker.isSelected() ? "Banker" : "Customer";

        // The controller decides whether the login is valid
        String error = LoginController.login(role, txtUsername.getText(), txtOtp.getText());

        if (error != null) {
            new Alert(Alert.AlertType.WARNING, error).show();
            return;
        }

        // Open the page for the role
        if (role.equals("Banker")) {
            SceneNavigator.show("BankerView.fxml", "Banker");
        } else {
            SceneNavigator.show("CustomerView.fxml", "Customer");
        }
    }
}