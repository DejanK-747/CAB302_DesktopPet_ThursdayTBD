package com.cab302thursdaytbd;

import java.io.IOException;

import com.cab302thursdaytbd.Model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class AboutController {
    @FXML
    private void switchToMainMenu() {
        try {
            App.setRoot("main_menu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
