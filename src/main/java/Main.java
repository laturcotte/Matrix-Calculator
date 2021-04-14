package main.java;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import main.java.matrix.Matrix;
import main.java.matrix.modules.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main extends Application {
    private BufferedReader networkInput;
    private PrintWriter networkOutput;

    private Socket socket;

    private static int serverPort = 41411;

    public static String computerName = "localhost";

    // main pane of client
    private BorderPane mainPane;

    @Override
    public void start(Stage primaryStage) throws Exception{
        // initialize main pane
        mainPane = new BorderPane();

        // initialize tab pane which will contain tabs for each module
        TabPane modulePane = new TabPane();

        // create the welcome screen/pane
        BorderPane welcomePane = new BorderPane();
        Text welcomeText = new Text("Welcome to the Matrix Calculator!");
        welcomePane.setCenter(welcomeText);

        Tab welcomeTab = new Tab("Welcome", welcomePane);
        welcomeTab.setClosable(false);
        modulePane.getTabs().add(welcomeTab);

        // create the Gauss-Jordan screen
        BorderPane gaussPane = new BorderPane();
        Label gaussLabel = new Label("Welcome to the Gauss-Jordan matrix solver");
        gaussPane.setTop(gaussLabel);

        GridPane gjMatrixPane = new GridPane();
        gjMatrixPane.setAlignment(Pos.CENTER);
        gjMatrixPane.setHgap(10);
        gjMatrixPane.setVgap(10);
        gjMatrixPane.setPadding(new Insets(20, 20, 20, 20));
        Label gjMatrixInstructions = new Label("Please enter matrix dimensions:");
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
        gjSubmitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String stringedNumRows = box1.getValue().toString();
                String stringedNumColumns = box2.getValue().toString();

                int numRows = Integer.parseInt(stringedNumRows);
                int numColumns = Integer.parseInt(stringedNumColumns);

                GridPane matrixTable = new GridPane();

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

                        Text solution = new Text();
                        solution.setText(solvedMatrix.getSolution());
                        matrixTable.add(solution, numColumns + 2, numRows + 2);
                    }
                });

                matrixTable.add(calculateButton, numColumns+1, numRows+1);

                Stage tableMatrix = new Stage();
                tableMatrix.setTitle("Gauss-Jordan solver: Please enter matrix");

                tableMatrix.setScene(new Scene(matrixTable, 900, 600));
                //tableMatrix.setScene(new Scene(displaySolvedMatrix, 900, 600));
                tableMatrix.show();
            }
        });

        gjMatrixPane.add(gjMatrixInstructions, 0, 1);
        gjMatrixPane.add(box1, 1, 1);
        gjMatrixPane.add(xText, 2, 1);
        gjMatrixPane.add(box2, 3, 1);
        gjMatrixPane.add(gjSubmitButton, 5, 3);

        gaussPane.setCenter(gjMatrixPane);

        Tab gaussTab = new Tab("Gauss Jordan", gaussPane);
        gaussTab.setClosable(false);
        modulePane.getTabs().add(gaussTab);

        mainPane.setCenter(modulePane);
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Matrix Calculator");
        Scene mainScene = new Scene(mainPane, 900, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
