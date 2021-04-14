package main.java.matrix.modules;

import main.java.matrix.Matrix;

public class Addition {

    public static void main(String[] args) {
    double[][] testMatrix1 =  {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
    double[][] testMatrix2 =  {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
    Matrix matrix1 = new Matrix(testMatrix1);
    Matrix matrix2 = new Matrix(testMatrix2);

        System.out.println("Pre-addition:");
        System.out.println(matrix1);
        System.out.println(matrix2);
        System.out.println("Post-addition");
        System.out.println(Add(matrix1,matrix2));
        System.out.println("Post-subtraction");
        System.out.println(Sub(matrix1, matrix2));
    }

    public static Matrix Add(Matrix matrix1, Matrix matrix2) {

        int rowSize = matrix1.getRowSize();
        int colSize = matrix1.getColSize();

        Matrix matrixOut = new Matrix(rowSize, colSize, true);

        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                matrixOut.setEntry(i, j, matrix1.getEntryAt(i, j) + matrix2.getEntryAt(i,j));
            }
        }

        return matrixOut;

    }

    public static Matrix Sub(Matrix matrix1, Matrix matrix2) {
        matrix2.negative();
        return Add(matrix1, matrix2);
    }
}