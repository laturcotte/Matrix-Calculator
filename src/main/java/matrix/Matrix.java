/**
 * Matrix toolbox
 * 
 * Main contributor: Samuel Bazinet
 * Last modification: February 15 2021
 */
package main.java.matrix;

import java.util.Scanner;

public class Matrix {

    private double matrix[][];
    private int row;
    private int col;

    /**
     * Constructor for Matrix class that lets the user input the elements <p>
     * Author: Samuel Bazinet
     * @param row : the amount of rows in the matrix
     * @param col : the amount of columns in the matrix
     */
    public Matrix(int row, int col) {
        // User input
        this.row = row;
        this.col = col;
        matrix = new double[row][col];

        this.setMat();

    }

    /**
     * Constructor for Matrix class that creates a 0 matrix <p>
     * Author: Samuel Bazinet
     * @param row : the amount of rows in the matrix
     * @param col : the amount of columns in the matrix
     * @param def : used to differentiate this constructor from the user filled one
     */
    public Matrix(int row, int col, boolean def) {
        // default input
        this.row = row;
        this.col = col;
        matrix = new double[row][col];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    /**
     * Constructor for Matrix class that converts an array to a Matrix <p>
     * Author: Samuel Bazinet
     * @param ar : the array to be converted
     */
    public Matrix(double[][] ar) {
        // array to matrix

        this.row = ar.length;
        this.col = ar[0].length;

        matrix = ar;
    }

    /**
     * setMat() is used to fill an array using user inputed values from the terminal <p>
     * Author: Samuel Bazinet
     */
    private void setMat() {
        Scanner scan = new Scanner(System.in);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                System.out.printf("Enter [%d][%d]: ", i, j);
                matrix[i][j] = scan.nextDouble();
            }
        }
        //scan.close();
    }

    /**
     * Change the content of one of the entries of the matrix <p>
     * Author: Samuel Bazinet 
     * @param rowIndex
     * @param colIndex
     * @param newEntry
     */
    public void setEntry(int rowIndex, int colIndex, double newEntry) {
        matrix[rowIndex][colIndex] = newEntry;
    }

    /**
     * Change an entire row of the matrix <p>
     * Author: Samuel Bazinet
     * @param row
     * @param newRow
     */
    public void setRow(int row, double[] newRow) {
        for (int i = 0; i < this.col; i++) {
            matrix[row][i] = newRow[i];
        }
    }

    /**
     * Change an entire column of the matrix <p>
     * Author: Samuel Bazinet
     * @param col
     * @param newCol
     */
    public void setCol(int col, double[] newCol) {
        for (int i = 0; i < this.row; i++) {
            matrix[i][col] = newCol[i];
        }
    }

    public int getRowSize() {return this.row;}
    public int getColSize() {return this.col;}

    /**
     * Get a row of the matrix as an array <p>
     * Author: Samuel Bazinet
     * @param row
     * @return
     */
    public double[] getRow(int row) {
        return matrix[row];
    }

    /**
     * Get a column of the matrix as an array <p>
     * Author: Samuel Bazinet
     * @param col
     * @return
     */
    public double[] getCol(int col) {
        double out[] = new double[this.row];

        for (int i = 0; i < row; i++) {
            out[i] = matrix[i][col];
        }

        return out;
    }

    public double getEntryAt(int rowIndex, int colIndex) {
        return matrix[rowIndex][colIndex];
    }


    public double[][] toArr() {
        return matrix;
    }

    /**
     * Author: Liam Turcotte
     * @param row1Index
     * @param row2Index
     * @return
     */
    public void swapRows(int row1Index, int row2Index) {
        double row1Copy[] = new double[row];

        // copy the first row
        for (int i = 0; i < row; i++) {
            row1Copy[i] = matrix[row1Index][i];
        }

        // put row 2 in row 1's place
        for (int i = 0; i < col; i++) {
            matrix[row1Index][i] = matrix[row2Index][i];
        }

        // swap 2nd row value with 1st row (from copy)
        for (int i = 0; i < col; i++) {
            matrix[row2Index][i] = row1Copy[i];
        }
    }

    /**
     * Swap 2 columns of the matrix <p>
     * Author: Samuel Bazinet
     * @param col1Index
     * @param col2Index
     */
    public void swapColumns(int col1Index, int col2Index) {
        // copy the first column
        double[] temp = this.getCol(col1Index);

        // put the second column in the first one's place
        this.setCol(col1Index, this.getCol(col2Index));

        // put the first column in the second one's place
        this.setCol(col2Index, temp);

    }

    /**
     * Author: Liam Turcotte
     * @return
     */
    public String toString() {
        /*
        String out = "[";
        for (int i = 0; i < row; i++) {
            out += "[ ";
            for (int j = 0; j < col; j++) {
                out += this.getEntryAt(i, j) + " ";
            }
            out += "]";
            if (i != row -1) {
                out += "\n";
            } 
        }
        out += "]";
        return out; */
        String out = "";
        double difference = 0.0;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                difference = getClosenessToInt(matrix[i][j]);
                if (difference > -0.000000001 && difference < 0.000000001) {
                    // number is an integer, convert to int/long to remove .0 decimal
                    // f. ex. -1.0 becomes -1
                    out += Math.round(matrix[i][j]) + "\t";
                }
                else {
                    out += matrix[i][j] + "\t";
                }
            }
            out += "\n";
        }
        out += "\n";

        return out;
    }

    /**
     * Author: Liam Turcotte
     * @param val
     * @return
     */
    public static double getClosenessToInt(double val) {
        return Math.round(val) - val;
    }

    /**
     * Author: Liam Turcotte
     * @param val
     * @return
     */
    public static double findInverseNum(double val) {
        // find inverse of val. in other words, find inverse so that val * inverse = 1, or inverse = 1/val
        if (val == 0) {
            return 0.0;
        }

        return (1.0 / val);
    }
}
