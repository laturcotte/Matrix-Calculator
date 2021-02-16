package main.java.matrix.modules;

import main.java.matrix.Matrix;

public class Transpose {

    public static void main(String[] args) {

        double[][] testMatrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        Matrix matrix = new Matrix(testMatrix);

        System.out.println("Pre-transpose:");
        System.out.println(matrix);
        System.out.println("Post-transpose");
        System.out.println(transpose(matrix));
    }

    public static Matrix transpose(Matrix matrix) {

        int numRows = matrix.getRowSize();
        int numCols = matrix.getColSize();

        Matrix matrixOut = new Matrix(numCols, numRows, true);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                matrixOut.setEntry(j, i, matrix.getEntryAt(i, j));
            }
        }

        return matrixOut;

    }

}
