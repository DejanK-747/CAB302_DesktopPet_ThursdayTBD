package com.cab302thursdaytbd;

import com.cab302thursdaytbd.Model.PetDAO;
import com.cab302thursdaytbd.Model.Session;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

 /**
 * Controller for the pet selection screen.
 * Allows the user to browse available pet types, enter a name, and adopt a pet.
 * Handles sprite animation and slide transitions between pet options.
 */
public class PetSelectionController {

    private final PetDAO petDAO = new PetDAO();

    @FXML
    private TextField petNameField;

    @FXML
    private Button adoptButton;

    @FXML
    private ImageView petView;

    private int currentFrame = 0;
    private Image[] frames;

    private int currentPetIndex = 0;
    private final String[] petType = {"frog", "monkey"};

    private int userId;

     /**
     * Initialises the controller after the FXML fields are injected.
     * Loads the current user ID from the session, sets up the default frog sprite animation,
     * and binds the adopt button's disabled state to whether the name field is empty.
     */
    @FXML
    public void initialize() {
        // TEST USER - delete when login system exists
        userId = Session.getUserId();
        System.out.println("Test user ID: " + userId);

        frames = new Image[] {
                new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog1.png").toExternalForm()),
                new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog2.png").toExternalForm())
        };

        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(300), e -> {
                    currentFrame = (currentFrame + 1) % frames.length;
                    petView.setImage(frames[currentFrame]);
                })
        );

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        adoptButton.disableProperty().bind(
                javafx.beans.binding.Bindings.createBooleanBinding(
                        () -> petNameField.getText().trim().isEmpty(),
                        petNameField.textProperty()
                )
        );

        System.out.println("WORKING DIRECTORY = " + System.getProperty("user.dir"));
    }

     /**
     * Handles a click on the left arrow, cycling to the previous pet type.
     */
    @FXML
    private void onLeftArrowClick() {
        switchPet(-1);
    }

     /**
     * Handles a click on the right arrow, cycling to the next pet type.
     */
    @FXML
    private void onRightArrowClick() {
        switchPet(1);
    }

      /**
      * Switches the currently displayed pet by the given direction and plays a slide transition.
      * Loads the sprite frames for the new pet and animates the view sliding out then back in.
      *
      * @param direction -1 to move left (previous pet), +1 to move right (next pet)
      */
    private void switchPet(int direction) {
        currentPetIndex = (currentPetIndex + direction + petType.length) % petType.length;
        String newPet = petType[currentPetIndex];

        javafx.animation.TranslateTransition slideOut =
                new javafx.animation.TranslateTransition(Duration.millis(300), petView);
        slideOut.setByX(direction * -300);

        slideOut.setOnFinished(e -> {
            startAnimationFor(newPet);

            petView.setTranslateX(direction * 300);
            javafx.animation.TranslateTransition slideIn =
                    new javafx.animation.TranslateTransition(Duration.millis(300), petView);
            slideIn.setToX(0);
            slideIn.play();
        });
        slideOut.play();
    }

      /**
      * Loads and starts the sprite animation for the specified pet type.
      * Updates the {@code frames} array and resets the animation to the first frame.
      *
      * @param pet the pet type identifier (e.g. {@code "frog"} or {@code "monkey"})
      */
    private void startAnimationFor(String pet) {
        if (pet.equals("frog")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog2.png").toExternalForm())
            };
        }

        if (pet.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey2.png").toExternalForm())
            };
        }

        currentFrame = 0;
        petView.setImage(frames[0]);
    }

      /**
      * Sets the user ID for this controller, overriding the session value if needed.
      *
      * @param userId the ID of the logged-in user
      */
    public void setUserId(int userId) {
        this.userId = userId;
    }

      /**
      * Handles the adopt button click. Validates the pet name and user ID, deletes any existing
      * pet for the user, saves the new pet to the database, then navigates to the main page.
      * Logs a warning and returns early if the name is empty or the user ID is invalid.
      */
    @FXML
    private void handleAdopt() {
        System.out.println("ADOPT BUTTON CLICKED");

        String petName = petNameField.getText().trim();

        if (petName.isEmpty()) {
            System.out.println("Invalid name");
            return;
        }

        if (userId <= 0) {
            System.out.println("No valid user ID");
            return;
        }

        String selectedPet = petType[currentPetIndex];
        petDAO.deletePet(userId); // added so that users can only have one pet at a time until unique login system is set up
        petDAO.adoptPet(userId, selectedPet, petName);
        System.out.println("Pet: " + selectedPet + ", Name: " + petName);

        try {
            /*FXMLLoader loader = new FXMLLoader(App.class.getResource("stats.fxml"));
            Parent root = loader.load();
            PetStatsController statsController = loader.getController();
            statsController.setUserId(userId);
            App.getScene().setRoot(root); */
            App.setRoot("main_page");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}