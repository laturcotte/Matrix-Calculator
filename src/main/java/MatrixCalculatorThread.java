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
            if (dim.length == 2) {
                arrayMatrix = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
            } else {
                out.println("400 Not enough arguments provided");
                return true;
            }

            String inContent = null;
            // We fill the matrix
            for (int i = 0; i < arrayMatrix.length; i++) {
                for (int j = 0; j < arrayMatrix[i].length; i++) {

                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    arrayMatrix[i][j] = Double.parseDouble(inContent);

                }
            }

            Matrix solvedMatrix = GaussJordan.solve(new Matrix(arrayMatrix));
            out.println(solvedMatrix.getSolution());
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
            if (dim.length == 2) {
                arrayMatrix = new double[Integer.parseInt(dim[0])][Integer.parseInt(dim[1])];
                scalar = Double.parseDouble(dim[2]);
            } else {
                out.println("400 Not enough arguments provided");
                return true;
            }

            String inContent = null;
            // We fill the matrix
            for (int i = 0; i < arrayMatrix.length; i++) {
                for (int j = 0; j < arrayMatrix[i].length; i++) {

                    try {
                        inContent = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (null == inContent) {
                        out.println("400 no number provided");
                        return true;
                    }

                    arrayMatrix[i][j] = Double.parseDouble(inContent);

                }
            }

            Matrix outMatrix = ScalarMultiplication.scalarMultiplication(new Matrix(arrayMatrix), scalar);

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
