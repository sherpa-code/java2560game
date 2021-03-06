package game;
/**
Advanced J2SE Programming CP2560 - Term Project
    Moose and Vamoose
        a reimagining of Oregon Trail

@author joseph.crane
 @author paul.gatto
 @author mohammad.f.rad
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;

// Main application class that loads and displays the initial screen.
public class Main extends Application {
    static ArrayList<String> landmarks = new ArrayList<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("Gameplay.fxml")); // debug
        //Parent root = FXMLLoader.load(getClass().getResource("Landmark.fxml")); // debug
        Scene scene = new Scene(root);
        stage.setTitle("Welcome to Moose and Vamoose");
        stage.setScene(scene);
        stage.show();
    }
}