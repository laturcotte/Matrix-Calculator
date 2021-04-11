package main.java.matrix.modules;

import main.java.matrix.Matrix;

public class Multiplication {

    public static void main(String[] args) {
        double[][] testMatrix1 =  {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}};
        double[][] testMatrix2 =  {{2, 4, 6}, {1, 2, 3}, {5, 7, 9}, {1, 3, 5}};
        Matrix matrix1 = new Matrix(testMatrix1);
        Matrix matrix2 = new Matrix(testMatrix2);

        System.out.println("Pre-multiplication:");
        System.out.println(matrix1);
        System.out.println(matrix2);
        System.out.println("Post-multiplication");
        System.out.println(Multiply(matrix1,matrix2));
    }

    public static Matrix Multiply(Matrix matrix1, Matrix matrix2){

        int rowSize = matrix1.getRowSize();
        int colSize = matrix2.getColSize();
        int midSize = matrix1.getColSize();
        int curVal;

        Matrix matrixOut = new Matrix(rowSize, colSize, true);

        for(int i = 0; i < rowSize ; i++) {
            for(int j = 0; j < colSize ; j++) {
                curVal = 0;
                for(int r = 0; r < midSize; r++) {
                    curVal += matrix1.getEntryAt(i, r) * matrix2.getEntryAt(r, j);
                }
                matrixOut.setEntry(i, j, curVal);
            }
        }

        return matrixOut;
    }

}
