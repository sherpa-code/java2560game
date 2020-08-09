package game;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.Function;

import static javafx.scene.paint.Color.*;
import static javafx.scene.paint.Color.RED;

import java.util.Timer;
import java.util.TimerTask;

public class GameplayController {
    /**
     * Simply instantiation of a player object
     */
    double fuelConsumptionRate = 1.0;
    PlayerStats player = new PlayerStats(
            0, 0, 100, 0, 0, 0, 0, 500
    );
    int tickRateMS = 10;
    Timer currentGameTickTimer;

    @FXML private Label dateValueLabel;
    @FXML private Label hungerValueLabel;
    @FXML private Label thirstValueLabel;
    @FXML private Label fuelValueLabel;
    @FXML private Label restroomValueLabel;
    @FXML private Label fatigueValueLabel;
    @FXML private Label nextLandmarkValueLabel;
    @FXML private Label lastLandmarkValueLabel;
    @FXML private Label distanceTraveledValueLabel;
    @FXML private Label speedValueLabel;
    @FXML private Label cashValueLabel;
    @FXML private Button speedUpButton;
    @FXML private Button slowDownButton;
    @FXML public ImageView moose;
    @FXML public ImageView carImageView;


    /**
     * Fired when the .fxml and controller has loaded.
     * Begins the gameplay ticking.
     */
    public void initialize()
    {
        cashValueLabel.setText(String.valueOf(player.getCash()));
        beginTick();
    }

    /**
     * Creates a Timer and Task that will execute every tickRateMS;
     * within updatePlayerStats, it will:
     * - calculate new player stat values
     * - update the corresponding fxml Labels
     */
    public void beginTick() {
        updatePlayerStatsLabels(player);
        currentGameTickTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    updatePlayerStats(player);
                });
            }
        };
        currentGameTickTimer.scheduleAtFixedRate(task, 0, tickRateMS);

    }

    /**
     * Sets the player object's new stat values,
     * ensures the values are within the range 0 to 100, and
     * updates the corresponding Labels in the UI.
     *
     * @param player
     */
    public void updatePlayerStats(PlayerStats player) {
        if (player.getFuel() <= 110) { // Player burns 20% more fuel traveling over 100kmh
            player.setFuel(player.getFuel() - player.getFuelRate() * player.getSpeed());
        } else {
            player.setFuel(player.getFuel() - player.getFuelRate() * player.getSpeed() * 1.1);
        }
        player.setHunger(player.getHunger() + player.getHungerRate());
        player.setThirst(player.getThirst() + player.getThirstRate());
        player.setRestroom(player.getRestroom() + player.getRestroomRate());
        player.setFatigue(player.getFatigue() + player.getFatigueRate());
        player.setDistanceTraveled(player.getDistanceTraveled() + player.getSpeed()/80000); // numeric value controls the ratio between distance traveled and speed

        player.clampPlayerStats();
        updatePlayerStatsLabels(player);
    }

    /**
     * A method that updates player status labels to current running values, truncated (via casting) to an integer
     *
     * @param player as an instance of PlayerStats class and gets its properties
     */
    //TODO: Should updates stats of the player during a time period (per second?!)
    public void updatePlayerStatsLabels(PlayerStats player) {
        hungerValueLabel.setText(String.valueOf((int) player.getHunger()));
        thirstValueLabel.setText(String.valueOf((int) player.getThirst()));
        fuelValueLabel.setText(String.valueOf((int) player.getFuel()));
        restroomValueLabel.setText(String.valueOf((int) player.getRestroom()));
        fatigueValueLabel.setText(String.valueOf((int) player.getFatigue()));
        speedValueLabel.setText(String.valueOf(player.getSpeed()));
        //TODO: Date stats label should be updated later on the screen

        //DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        //String strDate = dateFormat.format(player.getCurrentDate());
        dateValueLabel.setText(player.getCurrentDate().toString()); // replace this with a function to update only when X time has elapsed
        nextLandmarkValueLabel.setText(player.getNextLandmarkName());
        lastLandmarkValueLabel.setText(player.getLastLandmarkName());
        //distanceTraveledValueLabel.setText(String.valueOf(player.getDistanceTraveled()) + " km");
//        distanceTraveledValueLabel.setText(player.getDistanceTraveled() + " km");
        distanceTraveledValueLabel.setText(Math.round(player.getDistanceTraveled()*100.0)/100.0 + " km"); // rounds the distance traveled to 2 decimal places for the Label
    }

    /**
     * Stops the game tick (i.e. player stat updates and moose spawning)
     */
    public void cancelTick() {
        // This will be called when a landmark is reached and when game over happens.
        currentGameTickTimer.cancel();
        currentGameTickTimer.purge();
    }

    public GameplayController() throws ParseException, IOException, ClassNotFoundException {

    }

    public void main(String[] args) throws InterruptedException {

    }

    public void startDriving() {

    }

    public void enterLandmark() {
        cancelTick();
        // load new scene, making sure it has a reference to the current player object
    }



    public void gameOver(String reason) {
        cancelTick();
        System.out.println(reason);
    }


    /**
     * checks if car has collided with an 850lb ham on stilts
     *
     * @param moose moose object
     * @param car   car object
     * @return true if horrific auto accident, false otherwise
     */
    public Boolean checkIfMooseCollision(ImageView moose, ImageView car) {
        // calculate movement/collision bounds of the moose
        double mooseXPosMax = moose.getBoundsInParent().getMaxX();
        double carXPosMin = car.getBoundsInParent().getMinX();
        return (mooseXPosMax >= carXPosMin);
    }

    /**
     * creates an Avoid Button for random placement
     *
     * @return avoidButton
     */

    protected Button createAvoidButton() {
        Button avoidButton = new Button();
        avoidButton.setText("AVOID");
        avoidButton.setStyle("-fx-background-color: #ff0000; ");
        return avoidButton;
    }

    /**
     * creates a moose image for placement elsewhere
     *
     * @return moose
     */
    private ImageView createMoose() {
        File mooseFile = new File("img/moose.png");
        Image mooseImage = new Image(mooseFile.toURI().toString());
        ImageView moose = new ImageView();
        moose.setImage(mooseImage);
        return moose;
    }

    /**
     * placeholder function
     * needs to create objects, place moose on fxml line, start anim
     */
    public void initializeMooseInteraction() {
        ImageView moose = createMoose();
        Button avoidButton = createAvoidButton();

        //TODO clean insertions into FXML, need to figure out pathing for anim

    }


    @FXML
    public void animateMoose(ActionEvent event) {
        double mooseXPosition = moose.getTranslateX();
        moose.setTranslateX(mooseXPosition + 10);
        System.out.println("animateMoose() fired");//DEBUG
        if (checkIfMooseCollision(moose, carImageView)) {
            //System.out.println("COLLISION DETECTED");//DEBUG
            gameOver("Game Over.\nYou hit a moose.");
        }
    }

    @FXML
    public void animateMooseAtSpeed(ActionEvent event) {
        double mooseXPosition = moose.getTranslateX();
        moose.setTranslateX(mooseXPosition + player.getSpeed());
        System.out.println("animateMooseAtSpeed() fired");//DEBUG
        if (checkIfMooseCollision(moose, carImageView)) {
            //System.out.println("COLLISION DETECTED");//DEBUG
            gameOver("Game Over.\nYou hit a moose.");
        }
    }

    @FXML
    void slowDownBtnClicked(ActionEvent event) {
        double newSpeed = player.getSpeed() - 5;
        //System.out.println(newSpeed);
        player.setSpeed(newSpeed);
        speedValueLabel.setText(String.valueOf((player.getSpeed())));

    }

    @FXML
    void speedUpBtnClicked(ActionEvent event) {
        double newSpeed = player.getSpeed() + 5;
        //System.out.println(newSpeed);
        player.setSpeed(newSpeed);
        speedValueLabel.setText(String.valueOf((player.getSpeed())));
        if (newSpeed > 75) {
            //TODO: Fuel should get consumed in faster rate
        }
    }

    @FXML
    void speed50BtnClicked(ActionEvent event) {
        //System.out.println(50);
        player.setSpeed(50);
        speedValueLabel.setText(String.valueOf((player.getSpeed())));
    }

    @FXML
    void speed100BtnClicked(ActionEvent event) {
        //System.out.println(100);
        player.setSpeed(100);
        speedValueLabel.setText(String.valueOf((player.getSpeed())));
    }

    // buy buttons are not yet implemented, need to be handled in landmark scene
    @FXML
    void buyFuelBtnClicked(ActionEvent event) {

    }

    @FXML
    void buyFoodBtnClicked(ActionEvent event) {

    }

    @FXML
    void buyDrinkBtnClicked(ActionEvent event) {

    }

    @FXML
    void buyRestBtnClicked(ActionEvent event) {

    }
}
