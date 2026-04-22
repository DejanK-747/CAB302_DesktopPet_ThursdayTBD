package com.cab302thursdaytbd;


import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

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
    private AnchorPane popUp2;

    @FXML
    private AnchorPane popUp3;

    @FXML
    private Label statusChangeLabel;

    private AnchorPane[] popUps = {popUp1, popUp2, popUp3};



    @FXML
    public void showPopUp1() {
        hidePopUp(popUp2);
        hidePopUp(popUp3);
        showPopUp(popUp1);
    }

    @FXML
    public void showPopUp2() {
        hidePopUp(popUp1);
        hidePopUp(popUp3);
        showPopUp(popUp2);
    }

    @FXML
    public void showPopUp3() {
        hidePopUp(popUp1);
        hidePopUp(popUp2);
        showPopUp(popUp3);
    }

    private void showPopUp(AnchorPane popUp) {
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

    private void hidePopUp(AnchorPane popUp) {
        if (popUp.getScaleX() == 1) {
            ScaleTransition transition = new ScaleTransition(Duration.seconds(0.25), popUp);
            transition.setToX(0);
            transition.setToY(0);
            transition.setInterpolator(Interpolator.LINEAR);

            transition.play();
        }
    }

    @FXML
    private void interactWithPet(){
        ParallelTransition pt = new ParallelTransition();

        TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.5), statusChangeLabel);
        translateAnimation.setToY(-50);

        pt.getChildren().add(translateAnimation);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), statusChangeLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0);
        fadeTransition.setInterpolator(Interpolator.LINEAR);

        pt.getChildren().add(fadeTransition);

        pt.play();

        statusChangeLabel.setTranslateY(0);

    }
}
