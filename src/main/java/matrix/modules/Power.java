package main.java.matrix.modules;

import main.java.matrix.Matrix;
import main.java.matrix.modules.Multiplication;

public class Power {

    public static void main(String[] args) {
        double[][] testMatrix =  {{4, 5, 6}, {4, 5, 6}, {7, 8, 9}};
        Matrix matrix = new Matrix(testMatrix);

        System.out.println("Pre-exponential:");
        System.out.println(matrix);
        System.out.println("Post-exponential");
        System.out.println(Power(matrix,3));
    }

    public static Matrix Power(Matrix matrix, int power) {

        Matrix matrixOut = matrix;

        for (int i = 1; i < power; i++) {
            matrixOut = Multiplication.Multiply(matrixOut, matrix);
        }
        return matrixOut;
    }
}
