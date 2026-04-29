package com.cab302thursdaytbd;


import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainPageController {

    @FXML
    private Button interactButton1;

    @FXML
    private Pane pane;

    @FXML
    private ImageView image1;

    @FXML
    private ImageView petView;

    @FXML
    private AnchorPane popUp1;

    @FXML
    private Label statusChangeLabel;

    @FXML
    private Pane speechPane;

    @FXML
    private Button menuButton;

    private ParallelTransition statusChangePopUp = new ParallelTransition();



    //----------------------------------
    // Functions if there are more things that can be done with interactions
    // I was thinking of implementing multiple foods options or different ways to clean the pet
    // Obviously, kind of difficult to implement.
    // Thinking I should limit goals first. Just have these buttons raise stats first.
    @FXML
    protected void showPopUp1() {
        showPopUp(popUp1);
    }


    protected void showPopUp(AnchorPane popUp) {
        if (popUp.getScaleX() == 0) {
            ScaleTransition transition = new ScaleTransition(Duration.seconds(0.25), popUp);
            transition.setToX(1);
            transition.setToY(1);
            transition.setInterpolator(Interpolator.LINEAR);

            transition.play();

        } else if (popUp.getScaleX() == 1){
            ScaleTransition transition = new ScaleTransition(Duration.seconds(0.25), popUp);
            transition.setToX(0);
            transition.setToY(0);
            transition.setInterpolator(Interpolator.LINEAR);

            transition.play();
        }
    }
    //-----------------------------------------

    @FXML
    protected void interactWithPet(){

        if (statusChangePopUp.getCurrentRate() == 0.0d) {
        TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.5), statusChangeLabel);
        translateAnimation.setToY(-50);

        statusChangePopUp.getChildren().add(translateAnimation);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), statusChangeLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0);
        fadeTransition.setInterpolator(Interpolator.LINEAR);

        statusChangePopUp.getChildren().add(fadeTransition);

        statusChangePopUp.play();

        statusChangeLabel.setTranslateY(0);
        }
    }


    //
    @FXML
    protected void petSpeech( /* String text*/){
        System.out.println("I was pressed");

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(3), speechPane);
        fadeTransition.setCycleCount(2);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(Interpolator.EASE_IN);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }


    @FXML
    protected void onMenuClick () throws IOException{
        /*
        Stage stage = (Stage) menuButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), App.WIDTH, App.HEIGHT);
        stage.setScene(scene);
        */

    }

}
