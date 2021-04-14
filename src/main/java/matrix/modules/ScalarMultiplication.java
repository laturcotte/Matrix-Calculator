/**
 * Scalar multiplication operator
 * 
 * Author: Samuel Bazinet
 * Last modification: February 15 2021
 */

package main.java.matrix.modules;

import main.java.matrix.Matrix;

public class ScalarMultiplication {

    public static void main(String[] args) {

        double[][] testMatrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        Matrix matrix = new Matrix(testMatrix);

        System.out.println("Pre-multiplication:");
        System.out.println(matrix);
        System.out.println("Post-multiplication * 5");
        System.out.println(scalarMultiplication(matrix, 5));

    }

    /**
     * Multiplies a matrix by a scalar 
     * @param matrix 
     * @param scalar
     * @return A matrix multiplied by a scalar
     */
    public static Matrix scalarMultiplication(Matrix matrix, double scalar) {
        
        int numRows = matrix.getRowSize();
        int numCols = matrix.getColSize();

        Matrix matrixOut = new Matrix(numRows, numCols, true);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                matrixOut.setEntry(i, j, matrix.getEntryAt(i, j) * scalar);
            }
        }

        return matrixOut;

    }
    
}
