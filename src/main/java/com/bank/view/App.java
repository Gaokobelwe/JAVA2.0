package com.bank.view;

import com.bank.dao.DBConnection;
import com.bank.dao.SampleData;
import javafx.application.Application;
import javafx.stage.Stage;

// Starts the program: prepares the database, then opens the login page
public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Creates the tables and the sample data (only if the database is empty)
        DBConnection.createTables();
        SampleData.insertIfEmpty();

        SceneNavigator.setStage(stage);
        SceneNavigator.show("LoginView.fxml", "Banking System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}