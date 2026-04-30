package com.cab302thursdaytbd;

import javafx.fxml.FXML;
import java.io.IOException;

public class aboutController {
    @FXML
    private void switchToMainMenu() throws IOException{
        App.setRoot("main_menu");
    }
}
