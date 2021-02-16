package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import main.java.matrix.Matrix;
import main.java.matrix.modules.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Matrix Calculator");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();

        // WHEN CLICK BUTTON, CREATE NEW SCENE (and new fxml file?)

    }


    public static void main(String[] args) {
        launch(args);
    }
}
