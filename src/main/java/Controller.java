package main.java;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import main.java.matrix.Matrix;
import main.java.matrix.modules.GaussJordan;

public class Controller {
    @FXML private ChoiceBox box1;
    @FXML private ChoiceBox box2;

    public void handleMatrixDimensionsButton(ActionEvent actionEvent) throws IOException {
        // once submit button is clicked, get values in choice boxes and create new scene with matrix
        String stringedNumRows = box1.getValue().toString();
        String stringedNumColumns = box2.getValue().toString();

        int numRows = Integer.parseInt(stringedNumRows);
        int numColumns = Integer.parseInt(stringedNumColumns);
        //System.out.println(box1.getValue());
        // WHEN CLICK BUTTON, CREATE NEW SCENE (and new fxml file?)
        //Parent root = FXMLLoader.load(getClass().getResource("matrixTable.fxml"));
        Stage tableMatrix = new Stage();
        tableMatrix.setTitle("Gauss-Jordan solver: Please enter matrix");

        // have fill rest with zeros button?

        GridPane holdTable = new GridPane();

        // create text fields so user can input entries
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                TextField field = new TextField();
                field.setPrefWidth(40);
                field.setPrefHeight(40);
                field.setEditable(true);

                holdTable.setRowIndex(field, i);
                holdTable.setColumnIndex(field, j);
                holdTable.getChildren().add(field);
            }
        }

        // create submit button after matrix is set
        Button calculateButton = new Button("Calculate Gauss-Jordan");
        HBox hbCalculateButton = new HBox(10); // horizontal box?
        hbCalculateButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbCalculateButton.getChildren().add(calculateButton);
        holdTable.add(hbCalculateButton, 15, 20);

        calculateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Submit pressed");

                double matrixArray[][] = new double[numRows][numColumns];
                int i = 0;
                int j = 0;

                for (Node field: holdTable.getChildren()) {
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
                holdTable.add(solution, 40, 40);
            }
        });

        tableMatrix.setScene(new Scene(holdTable, 900, 600));
        //tableMatrix.setScene(new Scene(displaySolvedMatrix, 900, 600));
        tableMatrix.show();
    }
}
