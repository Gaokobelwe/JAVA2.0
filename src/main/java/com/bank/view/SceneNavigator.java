package com.bank.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

// Loads an FXML page into the window
public class SceneNavigator {

    private static Stage stage;

    public static void setStage(Stage s) { stage = s; }

    // Opens the page, for example show("LoginView.fxml", "Banking System")
    public static void show(String fxmlFile, String title) {
        try {
            // Your FXML files sit directly in the resources folder
            Parent root = FXMLLoader.load(SceneNavigator.class.getResource("/" + fxmlFile));
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (Exception e) {
        e.printStackTrace();

        // Find the real cause at the bottom of the chain and show it in a pop-up
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                "Could not open " + fxmlFile + "\n" + cause).show();
    }

    }
}