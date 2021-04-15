# Matrix-Calculator

This is a Matrix calculation suite implementing the client/server architecture. 
The user can choose which operation to make on their matrix and the multi-threaded server will perfrom the calculations and return the results to the client.
The client will connect to the server, the latter will perform the calculations and return them, then the client will disconnect from the server.

The calculator suite can currently perform 6 operations:

1. Gauss-Jordan Linear Solving
2. Matrix Pultiplication
3. Matrix Power (multiplying a matrix by itself)
4. Scalar Multiplication
5. Matrix Addtion
6. Matrix Transposition

## How to Run

1. Make sure that you have Gradle 6.8.1 installed on your machine.
2. Clone the project repository onto your machine.
3. Open a terminal and go to the Matrix-Calculator folder.
4. Write "gradle startServer" into the terminal to start the server.
5. Open a new terminal and go to Matrix-Calculator.
6. Write "gradle run" to start the client.
7. Appreciate the welcome page.
8. Click on a tab to start the desired operation.
9. Follow the instructions given to you.


## Resources Used

* JavaFX
