package com.cab302thursdaytbd;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class MainPageController {

    @FXML
    private Button interactButton1;

    @FXML
    private HBox popUp1;


    @FXML
    public void showPopUp1() {
        popUp1.setVisible(!popUp1.isVisible());

    }

}
