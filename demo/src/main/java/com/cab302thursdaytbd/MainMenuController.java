package com.cab302thursdaytbd;

import java.io.IOException;

import com.cab302thursdaytbd.Model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;



public class MainMenuController {

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void switchToMain() {
        try {
            App.setRoot("main_page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToStats() {
        try {
            App.setRoot("stats");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToAbout() {
        try {
            App.setRoot("about");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }


}
