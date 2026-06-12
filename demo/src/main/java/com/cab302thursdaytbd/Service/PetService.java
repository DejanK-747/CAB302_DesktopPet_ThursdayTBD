package com.cab302thursdaytbd.Service;

import com.cab302thursdaytbd.Model.Pet;
import com.cab302thursdaytbd.Model.PetDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * Service class responsible for pet behaviour and stat management.
 *
 * <p>This service manages background pet stat decay,
 * death conditions, and pet animation frame retrieval.
 * It periodically updates pet statistics and writes
 * changes to the database through {@link PetDAO}.</p>
 *
 * <p>The service also provides animation frame sets
 * for different pet moods and pet types.</p>
 */
public class PetService {
    private PetDAO petDAO = new PetDAO();
    //id of the currently logged in user
    private int userId;

    //timeline is used as a repeating background timer.
    //both timeline and keyframe are apart of the javafx animation framework (according to google) thye are usually used
    //for animations but for this i can use them to schedule repeated tasks, like the decay of stats.

    //timeline = the repeating loop
    // keyframe = the action performed after a set duration

    // the keyframe defines how long to wait and what code to execute after waiting

    private Timeline decayLoop;

    /**
     * Creates a new pet service for a specific user.
     *
     * @param userId the ID of the user whose pet will be managed
     */
    public PetService(int userId) {
        this.userId = userId;
    } // constructor to store id of user whose pet will be managed

    //stops the decay loop if it is currently running.
    //this is for changing scenes or closing the application so the background timers dont continue to run.
    /**
     * Stops the pet stat decay loop.
     *
     * <p>This prevents background timers from continuing
     * to execute after scene changes or application closure.</p>
     */
    public void stop() {
        if (decayLoop != null) {
            decayLoop.stop();
        }
    }

    //starts the stat decay system. every five seconds, hunger, energy and affection decrease and boredom increases.
    // it also updates the values in the db
    //if hunger or energy specifically reach zero then the pet dies and the death callback is triggered.



    private int tickCount = 0;

    /**
     * Starts the pet stat decay system.
     *
     * <p>Pet statistics are updated periodically using a
     * JavaFX {@link Timeline}. Hunger, energy, affection,
     * and boredom values change over time to simulate
     * ongoing pet needs.</p>
     *
     * <p>If hunger or energy reaches zero, the pet is
     * considered dead and the provided death callback
     * is executed.</p>
     *
     * @param onDeath callback executed when the pet dies
     */
    public void startDecay(Runnable onDeath) {
        decayLoop = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {    // decay stats Hunger, Affection, Energy (cleanliness)
                    Pet pet = petDAO.getPet(userId);

                    if (pet != null) { // continue only if the pet still exists (if it was dead it would not exist in db)
                        tickCount++;

                        if (tickCount % 3 == 0) {
                            pet.setHunger(pet.getHunger() - 1);
                        }

                        if (tickCount % 4 == 0) {
                            pet.setEnergy(pet.getEnergy() - 1);
                        }

                        if (tickCount % 6 == 0) {
                            pet.setAffection(pet.getAffection() - 1);
                            if (pet.getBoredom() >= 5){
                                pet.setAffection(pet.getAffection() - 1);
                            }
                        }

                        if (tickCount % 2 == 0) {
                            pet.setBoredom(pet.getBoredom() + 1);
                        }

                        petDAO.updatePetStats(pet); // save updated stat values back to the db
                        //this is the death condition. if hunger or energy reach zero, stop timer and trigger death callback
                        if (pet.getHunger() <= 0 || pet.getEnergy() <= 0) {
                            decayLoop.stop(); // stop loop
                            onDeath.run();    // trigger death event
                        petDAO.updatePetStats(pet);
                    }
                }})
        );

        decayLoop.setCycleCount(Timeline.INDEFINITE); // causes timeline to repeat forever, so the keyframe doesnt only execute once
        decayLoop.play(); // starts the repeating decay loop
    }

    // Determines death reason based on which stat hit zero
    /**
     * Determines the cause of a pet's death.
     *
     * @param pet the pet being evaluated
     * @return the determined cause of death
     */

    public String determineDeathReason(Pet pet) {
        if (pet == null)          return "Unknown";
        if ((pet.getHunger() <= 0) & (pet.getEnergy() <= 0)) return "Mysterious Circumstances";
        if (pet.getHunger() <= 0) return "Starvation";
        if (pet.getEnergy() <= 0) return "Exhaustion";
        return "Unknown";
    }

    /**
     * Forces a pet into a dead state.
     *
     * <p>This method sets hunger and energy to zero,
     * allowing the normal death logic to trigger.</p>
     *
     * @param userId the ID of the user whose pet will be killed
     */
    public void killPet(int userId){
        Pet userPet = petDAO.getPet(userId);
        userPet.setHunger(0);
        userPet.setEnergy(0);

        petDAO.updatePetStats(userPet);
    }


    // this returns the image frames associated with the pet type. these are used by the animation system to make the
    // pet sprites look like they are moving on the main page!
    // the controller repeatedly switches between the images in the returned array using the javafx timeline
    // from frog1, to frog , to frog 3 etc, which creates the illusion of animation
    //each pet type is mapped to its own set of image frames.
    /**
     * Retrieves animation frames for a pet's idle state.
     *
     * @param petType the type of pet
     * @return an array of animation frames for the idle state
     */
    public Image[] getIdleFrames(String petType) {
        Image[] frames;

        if (petType.equals("frog")) { // frog animation frames
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog2.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) { // monkey animation frames
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{ // fallback error image
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    };

    /**
     * Retrieves animation frames for a pet's excited state.
     *
     * @param petType the type of pet
     * @return an array of animation frames for the excited state
     */
    public Image[] getExcitedFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-excited1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-excited2.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/Monkey2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }

    /**
     * Retrieves animation frames for a pet's sad state.
     *
     * @param petType the type of pet
     * @return an array of animation frames for the sad state
     */
    public Image[] getSadFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sad1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sad2.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sad3.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sad1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sad2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }

    /**
     * Retrieves animation frames for a pet's angry state.
     *
     * @param petType the type of pet
     * @return an array of animation frames for the angry state
     */
    public Image[] getAngryFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-angry1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-angry2.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-angry1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-angry2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }

    /**
     * Retrieves animation frames for a pet's sleepy state.
     *
     * @param petType the type of pet
     * @return an array of animation frames for the sleepy state
     */
    public Image[] getSleepyFrames(String petType){
        Image[] frames;

        if (petType.equals("frog")) {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sleep1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sleep2.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/frog-sleep2.png").toExternalForm())
            };
        } else if (petType.equals("monkey")) {
            frames = new Image[] {
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sleep1.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sleep2.png").toExternalForm()),
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/monkey-sleep2.png").toExternalForm())
            };
        } else {
            frames = new Image[]{
                    new Image(getClass().getResource("/com/cab302thursdaytbd/images/icons/cross.png").toExternalForm())
            };
        }
        return frames;
    }
}
