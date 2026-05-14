package com.cab302thursdaytbd;

import java.io.IOException;
import javafx.fxml.FXML;

public class aboutController {

    @FXML
    private void switchToMainMenu() throws IOException {
        App.setRoot("main_menu");
    }
}