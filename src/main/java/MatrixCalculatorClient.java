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

import java.io.*;
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
    //String pathToStyleSheet = getClass().getResource("..\\css\\stylingelements.css").toExternalForm();
    //String pathToStyleSheet = getClass().getResource(".\\src\\main\\css\\stylingelements.css").toExternalForm();
    //String pathToStyleSheet = "file:\\" + System.getProperty("user.dir") + "\\src\\main\\css\\stylingelements.css";
    //String pathToStyleSheet = getClass().getResource(System.getProperty("user.dir") + "\\src\\main\\css\\stylingelements.css").toExternalForm();
    //String pathToStyleSheet = new File(System.getProperty("user.dir") + "/src/main/images/matrix.png").toURI().toString();
    String pathToStyleSheet = "file:/" + System.getProperty("user.dir").replace("\\", "/") + "/src/main/css/stylingelements.css";

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

        // create multiplication tab
        initializeMultTab();

        // set the main scene
        mainPane.setCenter(modulePane);
        primaryStage.setTitle("Matrix Calculator");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    /**
     * Connect to new socket tied to server address and port.
     * The client will connect to the server every time a command is entered.
     */
    public void connectToServer() {
        // connect to the socket
        try {
            socket = new Socket(computerName, serverPort);
        } catch (IOException error) {
            System.err.println("Problem while connecting to server");
        }

        if (null == socket) {
            System.err.println("no socket");
        }

        try {
            // get input and output stream from the server
            // true -> auto-flush buffer at the end of every println
            networkInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            networkOutput = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException error) {
            System.err.println("input/output exception while opening connection");
        }
    }

    /**
     * Closes the socket, called after each command.
     */
    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException error) {
            System.err.println("Error closing the socket");
        }
    }

    /**
     * Initialize the welcome tab.
     * Includes welcome message, matrix image, and a button which leads to a
     * new stage that displays Wikipedia links to the module articles.
     */
    public void initializeWelcomeTab() {
        // create the welcome screen/pane
        BorderPane welcomePane = new BorderPane();

        // create the title message, put at the top/center
        BorderPane welcomeTitlePane = new BorderPane();
        Text welcomeText = new Text("Welcome to the Matrix Calculator!");
        welcomeText.setId("welcomeTitle");
        welcomeTitlePane.setCenter(welcomeText);
        welcomePane.setTop(welcomeTitlePane);

        // add image, put at the center
        GridPane imagePane = new GridPane();
        imagePane.setAlignment(Pos.CENTER);
        imagePane.setHgap(10);
        imagePane.setVgap(10);
        imagePane.setPadding(new Insets(20, 20, 20, 20));
        imagePane.setId("imagePane");

        // get image from path, stored in src/main/images
        //Image matrixImage = new Image(getClass().getResource("..\\images\\matrix.png").toExternalForm());
        Image matrixImage = new Image(new File(System.getProperty("user.dir") + "/src/main/images/matrix.png").toURI().toString());

        // add image to imagePane, add imagePane to welcomePane
        ImageView matrixImageView = new ImageView(matrixImage);
        matrixImageView.setPreserveRatio(true);
        matrixImageView.setFitHeight(350);
        imagePane.getChildren().add(matrixImageView);
        welcomePane.setCenter(imagePane);

        // Add a button to the bottom/center
        // when clicked, it will show a new window with the links
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

        // add the tab
        Tab welcomeTab = new Tab("Welcome", welcomePane);
        welcomeTab.setClosable(false);
        modulePane.getTabs().add(welcomeTab);
    }

    /**
     * Displays the module Wikipedia links in a new window.
     */
    public void displayLinksStage() {
        // initialize stage
        Stage linksStage = new Stage();
        linksStage.setTitle("Wiki links of the modules");

        GridPane linksPane = new GridPane();
        linksPane.setHgap(10);
        linksPane.setVgap(10);

        // create labels/links for each module
        Label gaussJordanLabel = new Label("Gauss Jordan:");
        Hyperlink gaussJordanWiki = new Hyperlink("https://en.wikipedia.org/wiki/Gaussian_elimination");

        // add label and link for each module
        linksPane.add(gaussJordanLabel, 1, 1);
        linksPane.add(gaussJordanWiki, 2, 1);

        // show the scene
        Scene linkScene = new Scene(linksPane, 400, 300);
        linksStage.setScene(linkScene);
        linksStage.show();
    }

    /**
     * Creates the Gauss-Jordan tab.
     * User will be able to select their matrix size.
     * Leads to the calculator window.
     */
    public void initializeGaussJordanTab() {
        // create the Gauss-Jordan screen
        BorderPane gaussPane = new BorderPane();

        // set the title at the top/center
        BorderPane gaussTitlePane = new BorderPane();
        gaussTitlePane.setPadding(new Insets(10, 10, 10, 10));
        Label gaussLabel = new Label("Gauss-Jordan matrix solver");
        gaussLabel.setId("moduleTitle");
        gaussTitlePane.setCenter(gaussLabel);

        // show a short description about Gauss-Jordan method
        String message = "\n\nComputes the Reduced Row Echelon Form of a matrix to "
                + "solve a system of linear equations.";
        Text goalOfGJ = new Text(message);
        goalOfGJ.setId("descriptionText");
        gaussTitlePane.setBottom(goalOfGJ);
        gaussPane.setTop(gaussTitlePane);

        // set the matrix dimension pane in the center
        GridPane gjMatrixPane = new GridPane();
        gjMatrixPane.setAlignment(Pos.CENTER);
        gjMatrixPane.setHgap(10);
        gjMatrixPane.setVgap(10);
        gjMatrixPane.setPadding(new Insets(20, 20, 20, 20));

        // instruction label
        Label gjMatrixInstructions = new Label("Please enter matrix dimensions:");
        gjMatrixInstructions.setId("matrixDimText");

        // create first choice box to choose number of rows
        ChoiceBox<String> box1 = new ChoiceBox<>();
        box1.getItems().add("2");
        box1.getItems().add("3");
        box1.getItems().add("4");
        box1.getItems().add("5");
        box1.getItems().add("6");
        box1.setValue("2"); // default/initial value

        // between choice boxes for aesthetics
        Text xText = new Text("x");

        // create second choice box for number of columns
        ChoiceBox<String> box2 = new ChoiceBox<>();
        box2.getItems().add("3");
        box2.getItems().add("4");
        box2.getItems().add("5");
        box2.getItems().add("6");
        box2.getItems().add("7");
        box2.setValue("3"); // default/initial value

        // create submission button
        Button gjSubmitButton = new Button("Submit");
        gjSubmitButton.setId("button");
        gjSubmitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                displayGaussJordanMatrixStage(box1, box2);
            }
        });

        // add all elements
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

    /**
     * Displays the Gauss-Jordan calculator window.
     * The user enters their matrix here
     * @param box1 choice box with number of rows
     * @param box2 choice box with number of columns
     */
    public void displayGaussJordanMatrixStage(ChoiceBox<String> box1, ChoiceBox<String> box2) {
        // get the row/column values
        String stringedNumRows = box1.getValue();
        String stringedNumColumns = box2.getValue();

        // convert them to int
        int numRows = Integer.parseInt(stringedNumRows);
        int numColumns = Integer.parseInt(stringedNumColumns);

        // matrixpane will store all of the elements in the scene
        BorderPane matrixPane = new BorderPane();

        // initialize the enter matrix message
        BorderPane topPane = new BorderPane();
        topPane.setPadding(new Insets(10, 10, 10, 10));
        Label enterMatrixLabel = new Label("Please enter the matrix.");
        enterMatrixLabel.setId("matrixDimText");
        topPane.setLeft(enterMatrixLabel);
        matrixPane.setTop(topPane);

        GridPane matrixTable = new GridPane();
        matrixTable.setPadding(new Insets(10, 10, 10, 10));

        // create text fields so user can input entries
        // in other words, create the matrix
        // use row/col chosen by user
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                TextField field = new TextField();
                field.setPrefWidth(40);
                field.setPrefHeight(40);
                field.setEditable(true);

                matrixTable.add(field, j, i);
            }
        }

        // Button which will initiate contact with the server when clicked
        Button calculateButton = new Button("Calculate Gauss-Jordan");
        calculateButton.setId("calculateButton");
        BorderPane solutionPane = new BorderPane();
        solutionPane.setPadding(new Insets(10, 10, 10, 10));
        calculateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // connect to the server to issue command
                connectToServer();
                networkOutput.println("GAUSSJORD " + stringedNumRows + " " + stringedNumColumns);

                // get all elements in the matrix, send them to the server
                // server is reading after the command
                int i = 0;
                int j = 0;
                for (Node field: matrixTable.getChildren()) {
                    if (i >= numRows) {
                        break;
                    }

                    try {
                        networkOutput.println(((TextField) field).getText());
                        j++;

                        if (j == numColumns) {
                            i++;
                            j = 0;
                        }
                    } catch (ClassCastException e) {
                        System.err.println("Problem casting matrix");
                    }
                }

                // get the response from the server, which is the solution
                String solution = "";
                try {
                    String line;

                    while ((line = networkInput.readLine()) != null) {
                        solution += line + "\n";
                    }
                } catch (IOException error) {
                    System.err.println("Problem reading solution from server");
                }

                closeSocket();

                // display solution
                Text solutionText = new Text();
                solutionText.setId("matrixDimText");
                solutionText.setText(solution);
                solutionPane.setLeft(solutionText);
            }
        });

        // add button
        matrixTable.add(calculateButton, numColumns+3, numRows+3);

        matrixPane.setCenter(matrixTable);
        matrixPane.setBottom(solutionPane);

        Stage tableMatrix = new Stage();
        tableMatrix.setTitle("Gauss-Jordan solver: Please enter matrix");

        // display this scene
        Scene matrixScene = new Scene(matrixPane, 500, 600);
        matrixScene.getStylesheets().add(pathToStyleSheet);
        tableMatrix.setScene(matrixScene);
        tableMatrix.show();
    }

    /**
     * Creates the Multiplication tab.
     * User will be able to select their matrix size.
     * Leads to the calculator window.
     */
    public void initializeMultTab() {
        // create the multiplication screen
        BorderPane multPane = new BorderPane();

        // set the title at the top/center
        BorderPane multTitlePane = new BorderPane();
        multTitlePane.setPadding(new Insets(10, 10, 10, 10));
        Label MultLabel = new Label("Matrix multiplication calculator");
        MultLabel.setId("moduleTitle");
        multTitlePane.setCenter(MultLabel);

        // show a short description about muliplication method
        String message = "\n\nCalculates the product between "
                + "two matrices." + "\n NOTE : MATRICES MUST BE VALID";
        Text goalOfMult = new Text(message);
        goalOfMult.setId("descriptionText");
        multTitlePane.setBottom(goalOfMult);
        multPane.setTop(multTitlePane);

        // set the matrix dimension pane in the center
        GridPane multMatrixPane = new GridPane();
        multMatrixPane.setAlignment(Pos.CENTER);
        multMatrixPane.setHgap(10);
        multMatrixPane.setVgap(10);
        multMatrixPane.setPadding(new Insets(20, 20, 20, 20));

        // instruction labels
        Label firstMatrixInstructions = new Label("Please enter the first matrix dimension:");
        firstMatrixInstructions.setId("matrixDimText");
        Label secondMatrixInstructions = new Label("Please enter the second matrix dimension:");
        secondMatrixInstructions.setId("matrixDimText");

        // create first choice box to choose number of rows
        ChoiceBox<String> box1 = new ChoiceBox<>();
        box1.getItems().add("2");
        box1.getItems().add("3");
        box1.getItems().add("4");
        box1.getItems().add("5");
        box1.setValue("2"); // default/initial value

        // create second choice box for number of columns
        ChoiceBox<String> box2 = new ChoiceBox<>();
        box2.getItems().add("2");
        box2.getItems().add("3");
        box2.getItems().add("4");
        box2.getItems().add("5");
        box2.setValue("2"); // default/initial value

        // create third choice box to choose number of rows
        ChoiceBox<String> box3 = new ChoiceBox<>();
        box3.getItems().add("2");
        box3.getItems().add("3");
        box3.getItems().add("4");
        box3.getItems().add("5");
        box3.setValue("2"); // default/initial value

        // create second choice box for number of columns
        ChoiceBox<String> box4 = new ChoiceBox<>();
        box4.getItems().add("2");
        box4.getItems().add("3");
        box4.getItems().add("4");
        box4.getItems().add("5");
        box4.setValue("2"); // default/initial value

        // between choice boxes for aesthetics
        Text xText = new Text("x");
        Text x2Text = new Text("x");

        // create submission button
        Button matrixSubmitButton = new Button("Submit");
        matrixSubmitButton.setId("button");
        matrixSubmitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                displayMultiplicationMatrixStage(box1, box2, box3, box4);
            }
        });

        // add all elements
        multMatrixPane.add(firstMatrixInstructions, 0, 1);
        multMatrixPane.add(box1, 1, 1);
        multMatrixPane.add(xText, 2, 1);
        multMatrixPane.add(box2, 3, 1);
        multMatrixPane.add(secondMatrixInstructions, 0, 2);
        multMatrixPane.add(box3, 1, 2);
        multMatrixPane.add(x2Text, 2, 2);
        multMatrixPane.add(box4, 3, 2);
        multMatrixPane.add(matrixSubmitButton, 5, 4);

        multPane.setCenter(multMatrixPane);

        Tab MultTab = new Tab("Multiplication", multPane);
        MultTab.setClosable(false);
        modulePane.getTabs().add(MultTab);
    }

    /**
     * Displays the multiplication calculator window.
     * The user enters their 2 matrices here
     * @param matrix1row num rows in matrix 1
     * @param matrix1col num columns in matrix 1
     * @param matrix2row num rows in matrix 2
     * @param matrix2col num columns in matrix 2
     */
    public void displayMultiplicationMatrixStage(ChoiceBox<String> matrix1row, ChoiceBox<String> matrix1col,
                                                 ChoiceBox<String> matrix2row, ChoiceBox<String> matrix2col) {
        // get the row/column values
        String m1RowStringed = matrix1row.getValue();
        String m1ColStringed = matrix1col.getValue();
        String m2RowStringed = matrix2row.getValue();
        String m2ColStringed = matrix2col.getValue();

        // convert them to int
        int m1numRows = Integer.parseInt(m1RowStringed);
        int m1numColumns = Integer.parseInt(m1ColStringed);
        int m2numRows = Integer.parseInt(m2RowStringed);
        int m2numColumns = Integer.parseInt(m2ColStringed);

        // matrixpane will store all of the elements in the scene
        BorderPane matrixPane = new BorderPane();

        // initialize the enter matrix message
        BorderPane topPane = new BorderPane();
        topPane.setPadding(new Insets(10, 10, 10, 10));
        Label enterMatrixLabel = new Label("Please enter the matrices.");
        enterMatrixLabel.setId("matrixDimText");
        topPane.setLeft(enterMatrixLabel);
        matrixPane.setTop(topPane);

        GridPane matrixTable = new GridPane();
        matrixTable.setPadding(new Insets(10, 10, 10, 10));

        // create text fields so user can input entries
        // in other words, create the matrix
        // use row/col chosen by user
        // create first matrix
        for (int i = 0; i < m1numRows; i++) {
            for (int j = 0; j < m1numColumns; j++) {
                TextField field = new TextField();
                field.setPrefWidth(40);
                field.setPrefHeight(40);
                field.setEditable(true);

                matrixTable.add(field, j, i);
            }
        }

        Label xLabel = new Label("X");
        xLabel.setId("bigX");
        matrixTable.add(xLabel, m1numColumns + 1, m1numRows/2);

        // create second matrix a bit to the right
        for (int i = 0; i < m2numRows; i++) {
            for (int j = 0; j < m2numColumns; j++) {
                TextField field = new TextField();
                field.setPrefWidth(40);
                field.setPrefHeight(40);
                field.setEditable(true);

                matrixTable.add(field, j+4, i);
            }
        }

        // Button which will initiate contact with the server when clicked
        Button calculateButton = new Button("Calculate Multiplication");
        calculateButton.setId("calculateButton");
        BorderPane solutionPane = new BorderPane();
        solutionPane.setPadding(new Insets(10, 10, 10, 10));
        calculateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // connect to the server to issue command
                connectToServer();
                networkOutput.println("MULTIPLICATION " + m1RowStringed + " " + m1ColStringed
                                        + " " + m2RowStringed + " " + m2ColStringed);

                // get all elements in matrix 1, send them to the server
                // server is reading after the command
                int k = 0;
                int numElementsTotal = m1numRows * m1numColumns + m2numRows * m2numColumns;

                // go through all elements, only sending those in textfields
                for (Node field: matrixTable.getChildren()) {
                    if (k > numElementsTotal) {
                        break;
                    }
                    try {
                        networkOutput.println(((TextField) field).getText());
                    } catch (ClassCastException error) {
                        continue;
                    }
                    k++;
                }

                // get the response from the server, which is the values of
                // the solution matrix
                double[][] answerMatrix = new double[m1numRows][m2numColumns];

                try {
                    String line;
                    double currentElement;
                    int m = 0;
                    int n = 0;

                    while ((line = networkInput.readLine()) != null) {
                        if (m > m1numRows) {
                            break;
                        }

                        currentElement = Double.parseDouble(line);
                        answerMatrix[m][n] = currentElement;

                        n++;

                        if (n == m2numColumns) {
                            m++;
                            n = 0;
                        }
                    }
                } catch (IOException error) {
                    System.err.println("Problem reading solution from server");
                }

                closeSocket();

                // display solution
                GridPane matrixSolutionPane = new GridPane();
                Label solTitle = new Label("Solution:");
                solTitle.setId("matrixDimText");
                matrixSolutionPane.add(solTitle, 0, 0);

                for (int index = 0; index < m1numRows; index++) {
                    for (int jndex = 0; jndex < m2numColumns; jndex++) {
                        TextField field = new TextField(String.valueOf(answerMatrix[index][jndex]));
                        field.setPrefWidth(40);
                        field.setPrefHeight(40);
                        field.setEditable(false);

                        matrixSolutionPane.add(field, jndex + 1, index+2);
                    }
                }

                solutionPane.setLeft(matrixSolutionPane);
            }
        });

        // add button
        matrixTable.add(calculateButton, m1numRows + m2numRows + 3, m1numColumns +3);

        matrixPane.setCenter(matrixTable);
        matrixPane.setBottom(solutionPane);

        Stage tableMatrix = new Stage();
        tableMatrix.setTitle("Matrix multiplication: Please enter matrices");

        // display this scene
        Scene matrixScene = new Scene(matrixPane, 750, 600);
        matrixScene.getStylesheets().add(pathToStyleSheet);
        tableMatrix.setScene(matrixScene);
        tableMatrix.show();
    }

    /**
     * Launch the program.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
