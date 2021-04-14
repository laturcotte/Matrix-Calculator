package main.java;

import java.io.*;
import java.net.*;

public class MatrixCalculatorServer {

    protected  Socket clientSocket = null;
	protected  ServerSocket serverSocket = null;
	protected  MatrixCalculatorThread[] handler = null;
	protected  int numClients = 0;
    public static int SERVER_PORT = 41411;
    public static int MAX_CLIENTS = 100;

    public MatrixCalculatorServer() {

        try {
            // Connecting the server
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("---------------------------");
			System.out.println("Chat Server Application is running");
			System.out.println("---------------------------");
			System.out.println("Listening to port: "+SERVER_PORT);
			handler = new MatrixCalculatorThread[MAX_CLIENTS];

            while(true) {

                clientSocket = serverSocket.accept();
                System.out.println("Client #"+(numClients+1)+" connected.");
				handler[numClients] = new MatrixCalculatorThread(clientSocket);
				handler[numClients].start();
				numClients++;

            }


        } catch (IOException e) {
            System.err.println("IOException while creating server connection");
        }

    }

    public static void main(String[] args) {
        MatrixCalculatorServer mcs = new MatrixCalculatorServer();
    }
    
}
