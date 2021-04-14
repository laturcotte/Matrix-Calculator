package main.java;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import main.java.matrix.Matrix;
import main.java.matrix.modules.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The client-side of the application.
 * Uses JavaFX to display a GUI to the user.
 * The user will have access to all modules through the UI.
 * The client is connected to the server by a specific thread & socket.
 * the client connects/disconnects from the server every time a command is entered.
 *
 * @author Liam Turcotte
 */
public class MatrixCalculatorClient extends Application {
    // for reading/writing to the server through the socket
    private BufferedReader networkInput;
    private PrintWriter networkOutput;

    // the socket for the thread/client
    private Socket socket;

    // the port chosen for client/server communication
    private static int serverPort = 41411;

    // the computer name, will be run locally
    public static String computerName = "localhost";

    // main pane and main scene of the client
    private BorderPane mainPane;
    private Scene mainScene;

    // tab pane to access all modules
    private TabPane modulePane;

    // path to style sheet to customize elements
    String pathToStyleSheet = getClass().getResource("..\\css\\stylingelements.css").toExternalForm();

    @Override
    public void start(Stage primaryStage) throws Exception {
        // initialize main pane
        mainPane = new BorderPane();
        Scene mainScene = new Scene(mainPane, 900, 600);
        mainScene.getStylesheets().add(pathToStyleSheet);

        // initialize tab pane which will contain tabs for each module
        modulePane = new TabPane();

        // create welcome tab
        initializeWelcomeTab();

        // create Gauss Jordan tab
        initializeGaussJordanTab();

        mainPane.setCenter(modulePane);
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Matrix Calculator");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public void initializeWelcomeTab() {
        // create the welcome screen/pane
        BorderPane welcomePane = new BorderPane();

        BorderPane welcomeTitlePane = new BorderPane();
        Text welcomeText = new Text("Welcome to the Matrix Calculator!");
        welcomeText.setId("welcomeTitle");
        welcomeTitlePane.setCenter(welcomeText);
        welcomePane.setTop(welcomeTitlePane);

        // add image
        GridPane imagePane = new GridPane();
        imagePane.setAlignment(Pos.CENTER);
        imagePane.setHgap(10);
        imagePane.setVgap(10);
        imagePane.setPadding(new Insets(20, 20, 20, 20));

        imagePane.setId("imagePane");

        // get image from path, stored in src/main/images
        Image matrixImage = new Image(getClass().getResource("..\\images\\matrix.png").toExternalForm());

        // add image to imagePane, add imagePane to welcomePane
        ImageView matrixImageView = new ImageView(matrixImage);
        matrixImageView.setPreserveRatio(true);
        matrixImageView.setFitHeight(350);
        imagePane.getChildren().add(matrixImageView);
        welcomePane.setCenter(imagePane);

        BorderPane learnButtonPane = new BorderPane();
        learnButtonPane.setPadding(new Insets(20, 20, 20, 20));
        Button learnMoreButton = new Button("Learn more about these modules");
        learnMoreButton.setId("button");
        learnMoreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                displayLinksStage();
            }
        });

        learnButtonPane.setCenter(learnMoreButton);
        welcomePane.setBottom(learnButtonPane);

        Tab welcomeTab = new Tab("Welcome", welcomePane);
        welcomeTab.setClosable(false);
        modulePane.getTabs().add(welcomeTab);
    }

    public void displayLinksStage() {
        Stage linksStage = new Stage();
        linksStage.setTitle("Wiki links of the modules");

        GridPane linksPane = new GridPane();
        linksPane.setHgap(10);
        linksPane.setVgap(10);

        // create labels/links for each module
        Label gaussJordanLabel = new Label("Gauss Jordan:");
        Hyperlink gaussJordanWiki = new Hyperlink("https://en.wikipedia.org/wiki/Gaussian_elimination");

        linksPane.add(gaussJordanLabel, 1, 1);
        linksPane.add(gaussJordanWiki, 2, 1);

        Scene linkScene = new Scene(linksPane, 400, 300);
        linksStage.setScene(linkScene);
        linksStage.show();
    }

    public void initializeGaussJordanTab() {
        // create the Gauss-Jordan screen
        BorderPane gaussPane = new BorderPane();

        BorderPane gaussTitlePane = new BorderPane();
        gaussTitlePane.setPadding(new Insets(10, 10, 10, 10));
        Label gaussLabel = new Label("Gauss-Jordan matrix solver");
        gaussLabel.setId("moduleTitle");
        gaussTitlePane.setCenter(gaussLabel);

        String message = "\n\nComputes the Reduced Row Echelon Form of a matrix to "
                + "solve a system of linear equations.";
        Text goalOfGJ = new Text(message);
        goalOfGJ.setId("descriptionText");
        gaussTitlePane.setBottom(goalOfGJ);
        gaussPane.setTop(gaussTitlePane);

        GridPane gjMatrixPane = new GridPane();
        gjMatrixPane.setAlignment(Pos.CENTER);
        gjMatrixPane.setHgap(10);
        gjMatrixPane.setVgap(10);
        gjMatrixPane.setPadding(new Insets(20, 20, 20, 20));
        Label gjMatrixInstructions = new Label("Please enter matrix dimensions:");
        gjMatrixInstructions.setId("matrixDimText");
        ChoiceBox<String> box1 = new ChoiceBox<>();
        box1.getItems().add("2");
        box1.getItems().add("3");
        box1.getItems().add("4");
        box1.getItems().add("5");
        box1.getItems().add("6");
        box1.setValue("2"); // default/initial value

        Text xText = new Text("x");

        ChoiceBox<String> box2 = new ChoiceBox<>();
        box2.getItems().add("3");
        box2.getItems().add("4");
        box2.getItems().add("5");
        box2.getItems().add("6");
        box2.getItems().add("7");
        box2.setValue("3"); // default/initial value

        Button gjSubmitButton = new Button("Submit");
        gjSubmitButton.setId("button");
        gjSubmitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                displayGaussJordanMatrixStage(box1, box2);
            }
        });

        gjMatrixPane.add(gjMatrixInstructions, 0, 1);
        gjMatrixPane.add(box1, 1, 1);
        gjMatrixPane.add(xText, 2, 1);
        gjMatrixPane.add(box2, 3, 1);
        gjMatrixPane.add(gjSubmitButton, 5, 3);

        gaussPane.setCenter(gjMatrixPane);

        Tab gaussTab = new Tab("Gauss-Jordan", gaussPane);
        gaussTab.setClosable(false);
        modulePane.getTabs().add(gaussTab);
    }

    // TODO: generalize this function so it can be used to make matrices for any module
    // need to take into account module name, allowed matrix sizes, (how many matrices)
    // another function for 2 matrix operations? like mul, add
    public void displayGaussJordanMatrixStage(ChoiceBox<String> box1, ChoiceBox<String> box2) {
        String stringedNumRows = box1.getValue();
        String stringedNumColumns = box2.getValue();

        int numRows = Integer.parseInt(stringedNumRows);
        int numColumns = Integer.parseInt(stringedNumColumns);

        // make borderpane
        // top: another borderpane to center title like "enter matrix for gauss jord"
        // middle: matrix and submit button
        // bottom: solution

        BorderPane matrixPane = new BorderPane();

        BorderPane topPane = new BorderPane();
        topPane.setPadding(new Insets(10, 10, 10, 10));
        Label enterMatrixLabel = new Label("Please enter the matrix.");
        enterMatrixLabel.setId("matrixDimText");
        topPane.setLeft(enterMatrixLabel);
        matrixPane.setTop(topPane);

        GridPane matrixTable = new GridPane();
        matrixTable.setPadding(new Insets(10, 10, 10, 10));

        // create text fields so user can input entries
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                TextField field = new TextField();
                field.setPrefWidth(40);
                field.setPrefHeight(40);
                field.setEditable(true);

                //matrixTable.setRowIndex(field, i);
                //matrixTable.setColumnIndex(field, j);
                //matrixTable.getChildren().add(field);
                matrixTable.add(field, j, i);
            }
        }

        Button calculateButton = new Button("Calculate Gauss-Jordan");
        calculateButton.setId("calculateButton");
        BorderPane solutionPane = new BorderPane();
        solutionPane.setPadding(new Insets(10, 10, 10, 10));
        calculateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                double matrixArray[][] = new double[numRows][numColumns];
                int i = 0;
                int j = 0;

                for (Node field: matrixTable.getChildren()) {
                    if (i >= numRows) {
                        break;
                    }

                    try {
                        matrixArray[i][j] = Double.parseDouble(((TextField) field).getText());
                        j++;

                        if (j == numColumns) {
                            i++;
                            j = 0;
                        }
                    } catch (ClassCastException e) {
                    }
                }

                Matrix matrix = new Matrix(matrixArray);
                Matrix solvedMatrix = GaussJordan.solve(matrix);

                // display solution
                /*
                for (int row = 0; i < solvedMatrix.getRowSize(); row++) {
                    for (int col = 0; j < solvedMatrix.getColSize(); col++) {
                        TextField field = new TextField();
                        field.setPrefWidth(40);
                        field.setPrefHeight(40);
                        field.setText(String.valueOf(solvedMatrix.getEntryAt(row, col)));
                        field.setEditable(false);

                        //holdTable.setRowIndex(field, row + i);
                        //holdTable.setColumnIndex(field, col + j);
                        holdTable.add(field, row+i, col+j);
                        //holdTable.getChildren().add(field);
                    }
                } */

                // display state and solution. check if solution is empty string (if it is, don't display)
                //System.out.println("\n\n");
                //System.out.println(solvedMatrix.getState());
                //System.out.println(solvedMatrix.getSolution());
                /*Text state = new Text();
                state.setText(solvedMatrix.getState());
                holdTable.add(state, 30, 30); */

                //Text solution = new Text();
                //solution.setText(solvedMatrix.getSolution());
                //matrixTable.add(solution, numColumns + 4, numRows + 4);
                //solution = solvedMatrix.getSolution();
                String solution = solvedMatrix.getSolution();

                Text solutionText = new Text();
                solutionText.setText(solution);
                solutionPane.setLeft(solutionText);
            }
        });

        matrixTable.add(calculateButton, numColumns+3, numRows+3);

        matrixPane.setCenter(matrixTable);
        matrixPane.setBottom(solutionPane);

        Stage tableMatrix = new Stage();
        tableMatrix.setTitle("Gauss-Jordan solver: Please enter matrix");

        Scene matrixScene = new Scene(matrixPane, 500, 600);
        matrixScene.getStylesheets().add(pathToStyleSheet);
        tableMatrix.setScene(matrixScene);
        tableMatrix.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
