package com.cab302thursdaytbd;

import java.io.IOException;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private void switchToStats() throws IOException {
        App.setRoot("stats");
    }

    @FXML
    private void switchToAbout() throws IOException {
        App.setRoot("about_app");
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }


}
