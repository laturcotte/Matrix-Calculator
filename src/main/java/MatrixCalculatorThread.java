package main.java;

import java.io.*;
import java.net.*;
import java.util.*;
import main.java.matrix.modules.*;
import main.java.matrix.Matrix;

public class MatrixCalculatorThread extends Thread {

    protected Socket socket       = null;
	protected PrintWriter out     = null;
	protected BufferedReader in   = null;

    /**
     * This is the constructor for the MatrixCalculatorThread
     * @param socket : The client socket
     */
    public MatrixCalculatorThread(Socket socket) {

        // We set up the thread
        super();
        this.socket = socket;

        try{
            // We get the reader and writer reading to send and receive commands and info
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOException while opening a read/wrtie connection");
        }

    }

    /**
     * Run is the method giving a thread the tasks it needs to do
     */
    public void run() {

        boolean endOfSession = false;
        
        // We set up a loop to process every command coming from the client
        while (!endOfSession) {
            endOfSession = processCommand();
        }
        
        // We close the socket when the session is over
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }    

    }

    /**
     * This processes the commans issued by the client
     * @return The end of session state
     */
    protected boolean processCommand() {

        String message = null;
        
        // We store what the client is sending to the thread
        try {
            message = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading comman from socket");
            // We set the endOfSession to true so that the connection can be ended
            return true;
        }

        if (null == message) {
            // We end the session since we did not receive anything from the client
            return true;
        }

        // We split the message received into a command and arguments, so that the thread can handle it
        StringTokenizer st = new StringTokenizer(message);
        String command = st.nextToken();
        String args = null;

        if (st.hasMoreTokens()) {
            args = message.substring(command.length()+1, message.length());
        }

        // We call processCommand to handle the command
        return processCommand(command, args);

    }

    /**
     * This processes the commands received from the client
     * @param command : The command received
     * @param arguments : The possible arguments following the command
     * @return : The state of the session
     */
    protected boolean processCommand(String command, String arguments) {

        // This command processes the GAUSSJORD command
        if (command.equalsIgnoreCase("GAUSSJORD")) {

            String[] dim = arguments.split(" ");
            double[][] arrayMatrix = null;

            // We get and set the size of the matrix
            if (dim.length <= 2) {
                arrayMatrix = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
            } else {
                out.println("400 Not right amount of argument provided");
                return true;
            }

            String inContent = null;
            // We fill the matrix
            for (int i = 0; i < arrayMatrix.length; i++) {
                for (int j = 0; j < arrayMatrix[i].length; j++) {

                    // We wait for the client to send each member of the matrix
                    // One entry at a time, row by row
                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // We make sure that the client sent something
                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    arrayMatrix[i][j] = Double.parseDouble(inContent);

                }
            }

            // We return the solution of the linear system
            Matrix solvedMatrix = GaussJordan.solve(new Matrix(arrayMatrix));
            out.println(solvedMatrix.getState() + "\n" + solvedMatrix.getSolution());
            /*
            for (int i = 0; i < solvedMatrix.getRowSize(); i++) {
                for (int j = 0; j < solvedMatrix.getColSize(); j++) {
                    out.println(solvedMatrix.getEntryAt(i, j));
                }
            } 
            */

            return true;

        } else if (command.equalsIgnoreCase("SCALARMUL")) {

            String[] dim = arguments.split(" ");
            double[][] arrayMatrix = null;
            double scalar = 0;

            // We get and set the size of the matrix
            if (dim.length == 3) {
                arrayMatrix = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
                scalar = Double.parseDouble(dim[2]);
            } else {
                out.println("400 Not right amount of argument provided");
                return true;
            }

            String inContent = null;
            // We fill the matrix
            for (int i = 0; i < arrayMatrix.length; i++) {
                for (int j = 0; j < arrayMatrix[i].length; j++) {

                    // We wait for the client to send each member of the matrix
                    // One entry at a time, row by row
                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    // We make sure that the client sent something
                    arrayMatrix[i][j] = Double.parseDouble(inContent);

                }
            }

            // We apply the scalar multiplication
            Matrix outMatrix = ScalarMultiplication.scalarMultiplication(new Matrix(arrayMatrix), scalar);
            
            // We return the elements of the matrix
            for (int i = 0; i < outMatrix.getRowSize(); i++) {
                for (int j = 0; j < outMatrix.getColSize(); j++) {
                    out.println(outMatrix.getEntryAt(i, j));
                }
            } 

            return true;

        } else if (command.equalsIgnoreCase("TRANSPOSE")) {
            
            String[] dim = arguments.split(" ");
            double[][] arrayMatrix = null;

            // We get and set the size of the matrix
            if (dim.length == 2) {
                arrayMatrix = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
            } else {
                out.println("400 Not right amount of argument provided");
                return true;
            }

            String inContent = null;
            // We fill the matrix
            for (int i = 0; i < arrayMatrix.length; i++) {
                for (int j = 0; j < arrayMatrix[i].length; j++) {

                    // We wait for the client to send each member of the matrix
                    // One entry at a time, row by row
                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // We make sure that the client sent something
                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    arrayMatrix[i][j] = Double.parseDouble(inContent);

                }
            }

            // We compute the transpose the matrix
            Matrix outMatrix = Transpose.transpose(new Matrix(arrayMatrix));
            
            // We return the elements of the matrix
            for (int i = 0; i < outMatrix.getRowSize(); i++) {
                for (int j = 0; j < outMatrix.getColSize(); j++) {
                    out.println(outMatrix.getEntryAt(i, j));
                }
            } 

            return true;

        } else if (command.equalsIgnoreCase("MULTIPLICATION")) {

            String[] dim = arguments.split(" ");
            double[][] arrayMatrix1 = null;
            double[][] arrayMatrix2 = null;

            // We get and set the size of the matrices
            if (dim.length == 4) {
                arrayMatrix1 = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
                arrayMatrix2 = new double[Integer.parseInt(dim[2])][Integer.parseInt(dim[3])];
            } else {
                out.println("400 Not right amount of argument provided");
                return true;
            }

            String inContent = null;
            // We fill the matrix1
            for (int i = 0; i < arrayMatrix1.length; i++) {
                for (int j = 0; j < arrayMatrix1[i].length; j++) {

                    // We wait for the client to send each member of the matrix
                    // One entry at a time, row by row
                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // We make sure that the client sent something
                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    arrayMatrix1[i][j] = Double.parseDouble(inContent);

                }
            }
            // We fill the matrix2
            for (int i = 0; i < arrayMatrix2.length; i++) {
                for (int j = 0; j < arrayMatrix2[i].length; j++) {

                    // We wait for the client to send each member of the matrix
                    // One entry at a time, row by row
                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // We make sure that the client sent something
                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    arrayMatrix2[i][j] = Double.parseDouble(inContent);

                }
            }

            // We compute the transpose the matrix
            Matrix outMatrix = Multiplication.Multiply(new Matrix(arrayMatrix1), new Matrix(arrayMatrix2));
            
            // We return the elements of the matrix
            for (int i = 0; i < outMatrix.getRowSize(); i++) {
                for (int j = 0; j < outMatrix.getColSize(); j++) {
                    out.println(outMatrix.getEntryAt(i, j));
                }
            } 

            return true;

        } else if (command.equalsIgnoreCase("ADDITION")) {

            String[] dim = arguments.split(" ");
            double[][] arrayMatrix1 = null;
            double[][] arrayMatrix2 = null;
            boolean add;

            // We get and set the size of the matrices
            if (dim.length == 3) {
                arrayMatrix1 = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
                arrayMatrix2 = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
                add = Boolean.parseBoolean(dim[2]);
            } else {
                out.println("400 Not right amount of argument provided");
                return true;
            }

            String inContent = null;
            // We fill the matrix1
            for (int i = 0; i < arrayMatrix1.length; i++) {
                for (int j = 0; j < arrayMatrix1[i].length; j++) {

                    // We wait for the client to send each member of the matrix
                    // One entry at a time, row by row
                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // We make sure that the client sent something
                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    arrayMatrix1[i][j] = Double.parseDouble(inContent);

                }
            }
            // We fill the matrix2
            for (int i = 0; i < arrayMatrix2.length; i++) {
                for (int j = 0; j < arrayMatrix2[i].length; j++) {

                    // We wait for the client to send each member of the matrix
                    // One entry at a time, row by row
                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // We make sure that the client sent something
                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    arrayMatrix2[i][j] = Double.parseDouble(inContent);

                }
            }

            // We compute the transpose the matrix
            Matrix outMatrix;
            if (add) {
                outMatrix = Addition.Add(new Matrix(arrayMatrix1), new Matrix(arrayMatrix2));
            } else {
                outMatrix = Addition.Sub(new Matrix(arrayMatrix1), new Matrix(arrayMatrix2));
            }
            // We return the elements of the matrix
            for (int i = 0; i < outMatrix.getRowSize(); i++) {
                for (int j = 0; j < outMatrix.getColSize(); j++) {
                    out.println(outMatrix.getEntryAt(i, j));
                }
            } 

            return true;

        } else if (command.equalsIgnoreCase("POWER")) {

            String[] dim = arguments.split(" ");
            double[][] arrayMatrix = null;
            int power = 0;

            // We get and set the size of the matrix
            if (dim.length == 3) {
                arrayMatrix = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
                power = Integer.parseInt(dim[2]);
            } else {
                out.println("400 Not right amount of argument provided");
                return true;
            }

            String inContent = null;
            // We fill the matrix
            for (int i = 0; i < arrayMatrix.length; i++) {
                for (int j = 0; j < arrayMatrix[i].length; j++) {

                    // We wait for the client to send each member of the matrix
                    // One entry at a time, row by row
                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    // We make sure that the client sent something
                    arrayMatrix[i][j] = Double.parseDouble(inContent);

                }
            }

            // We apply the scalar multiplication
            Matrix outMatrix = Power.Power(new Matrix(arrayMatrix), power);
            
            // We return the elements of the matrix
            for (int i = 0; i < outMatrix.getRowSize(); i++) {
                for (int j = 0; j < outMatrix.getColSize(); j++) {
                    out.println(outMatrix.getEntryAt(i, j));
                }
            } 

            return true;
        } else {

            // We send out an error message if the command is unrecognized
            out.println("400 Unrecognized Command: " + command);
            return true;

        }

    }
    
}
