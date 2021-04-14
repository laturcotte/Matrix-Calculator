/**
 * Gauss-Jordan solver.
 *
 * Author: Liam Turcotte
 * Last modification: February 15 2021
 */
package main.java.matrix.modules;

//import java.util.Scanner;
import main.java.matrix.Matrix;

/**
 * Calculates the Gauss-Jordan method for a matrix.
 * Reduces to RREF, solves linear system.
 *
 * @author Liam Turcotte
 */
public class GaussJordan {
    private static Matrix matrix;
    private static int numRows;
    private static int numColumns;

    /**
     * Main solve method
     * @param matrixEntered initial matrix
     * @return solved matrix
     */
    public static Matrix solve(Matrix matrixEntered) {
        matrix = matrixEntered;
        numRows = matrix.getRowSize();
        numColumns = matrix.getColSize();

        conductGaussJordan();
        return matrix;
    }

    /**
     * main calculating function
     */
    private static void conductGaussJordan() {
        int stepNum = 1; // used to count steps

        // iterate for all rows
        for (int i = 0; i < numRows; i++) {
            // pivot
            //System.out.println("\nStep " + stepNum + ": create pivot in row " + (i+1) + ". ");
            ensureFirstNonZeroEntry(i);
            //System.out.println(matrix);
            stepNum++;

            // prepare rows for elementary ops
            //System.out.print("\nStep " + stepNum + ": ensure that all entries in column " + (i+1));
            //System.out.println(" are either zeros or ones for elementary operations");
            //System.out.println("I: " + i);
            createOnesInColumn(i, i);
            //System.out.println(matrix);
            stepNum++;

            // create 0s underneath the leading 1
            // the giver row is the same as the column index. so like when working with column 1, giver is row 1.
            // which means that row 2 and 3 will be affected by row 1
            // since we can assume each row will either have a 0 or 1 in current column, it's simply a subtraction
            for (int j = i + 1; j < numRows; j++) {
                if (matrix.getEntryAt(j, i) != 0) {
                    //System.out.print("\nStep " + stepNum + ": ");
                    twoRowOperation(j, i, 1, 1);
                    //System.out.println(matrix);
                    stepNum++;
                }
            }

            // also create 0s above the leading 1
            for (int j = i - 1; j >= 0; j--) {
                if (matrix.getEntryAt(j, i) != 0) {
                    //System.out.print("\nStep " + stepNum + ": ");
                    twoRowOperation(j, i, 1, 1);
                    //System.out.println(matrix);
                    stepNum++;
                }
            }

            correctIntValues();
        }

        // cleanup; make sure first entry of every row is leading 1
        for (int i = 0; i < numRows; i++) {
            //System.out.println("\nStep " + stepNum + ": ensure leading 1 in row " + (i+1));
            ensureLeadingOne(i);
            correctIntValues();
            stepNum++;
        }

        // cleanup; go through every column that has a leading 1 and make sure it is the only non-zero val in that column
        // i.e. go through each row (can now assume there's leading 1 or row of 0s (before last val)). once get to first
        // value (don't need to check it's 1, can assume (or check lol)), check all entries in that column.
        // START FROM LAST ROW
        for (int i = numRows - 1; i >= 0; i--) {
            //System.out.println("\nStep " + stepNum + ": ensure no other non-zero entries in same column as leading 1 in row " + (i+1));
            cleanUpColumns(i);
            correctIntValues();
            //System.out.println(matrix);
            stepNum++;
        }

        //System.out.println("System is now in reduced row echelon form."); // no need to check, for sure good
        matrix.setState("System is now in reduced row echelon form.");

        // check if system is consistent or not, provide solution
        if (checkZeroSolutions()) {
            return;
        }
        if (checkInfiniteSolutions()) {
            solveInfiniteSystem();
            return;
        }
        if (checkOneSolution()) {
            return;
        }
    }

    private static void ensureFirstNonZeroEntry(int rowIndex) {
        // finds the first non-zero entry for a specific column.
        // rows and columns are associated (i.e. don't look at rows above)
        // assume that row above already has leading 1
        // if first non-zero entry (first being leftmost) is not in the highest possible row, swap highest row
        // with the one with first non-zero

        // check the first possible row. if not a 0, then we're fine
        if (matrix.getEntryAt(rowIndex, rowIndex) != 0) {
            return;
        }

        // first possible row has a 0, swap with first row that doesn't
        for (int i = rowIndex + 1; i < numRows; i++) {
            if (matrix.getEntryAt(i, rowIndex) != 0) {
                // swap with current row
                //System.out.println("Swap row " + rowIndex + " with row " + i + " to have a pivot");
                matrix.swapRows(i, rowIndex);
                return;
            }
        }
    }

    private static void createOnesInColumn(int colIndex, int startingRow) {
        // from starting row, create all 1s in that column below
        for (int i = startingRow; i < numRows; i++) {
            double currentEntry = matrix.getEntryAt(i, colIndex);
            if (currentEntry != 0 && currentEntry != 1) {
                double inverse = Matrix.findInverseNum(currentEntry);
                oneRowOperation(i, inverse);
            }
        }

        // also create all 1s above
        for (int i = startingRow - 1; i >= 0; i--) {
            double currentEntry = matrix.getEntryAt(i, colIndex);
            if (currentEntry != 0 && currentEntry != 1) {
                double inverse = Matrix.findInverseNum(currentEntry);
                oneRowOperation(i, inverse);
            }
        }
    }

    private static void oneRowOperation(int rowIndex, double value) {
        // multiplying a row by a non-zero constant
        for (int i = 0; i < numColumns; i++) {
            matrix.setEntry(rowIndex, i, matrix.getEntryAt(rowIndex, i) * value);
        }
    }

    private static void twoRowOperation(int receiverRow, int giverRow, double receiverVal, double giverVal) {
        // receiver gets changed by giver
        // ex. form R2: 3*R2 - 2*R1 -> values in R2 after operation are 3*R2 before op minus 2*R1 vals
        //System.out.println("Elementary operation: row " + (receiverRow+1) + ": row " + (receiverRow+1) + " - row " + (giverRow+1));

        // hold the values of receiver row with constant multiplied, do the same with giver
        double receiverWithOp[] = new double[numColumns];
        double giverWithOp[] = new double[numColumns];
        for (int i = 0; i < numColumns; i++) {
            receiverWithOp[i] = receiverVal * matrix.getEntryAt(receiverRow, i);
            giverWithOp[i] = giverVal * matrix.getEntryAt(giverRow, i);
        }

        // Assume that constants are set in a way that we can always do direct subtraction
        double newReceiverRow[] = new double[numColumns];
        for (int i = 0; i < numColumns; i++) {
            newReceiverRow[i] = receiverWithOp[i] - giverWithOp[i];
        }

        // replace receiver row with new values
        for (int i = 0; i < numColumns; i++) {
            matrix.setEntry(receiverRow, i, newReceiverRow[i]);
        }
    }

    private static void correctIntValues() {
        // sometimes, since working with doubles, what should be integer values turn
        // into decimals (f. ex. 1 or 1.0 turns into 0.99999999999999972). this corrects that
        double difference = 0.0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                difference = Matrix.getClosenessToInt(matrix.getEntryAt(i, j));
                if (difference > -0.000000001 && difference < 0.000000001) {
                    // safe to say that this is a whole number/integer
                    matrix.setEntry(i, j, Math.round(matrix.getEntryAt(i, j)));
                }
            }
        }
    }

    private static void ensureLeadingOne(int rowIndex) {
        // go through row, make sure that first non-zero value (up to cols - 1) is a 1
        // if not, turn it into 1 by multiplying row by reciprocal
        // if it's row of 0s, do nothing
        // check if there is a leading 1 in that COLUMN. if not, produce 1 with single-row operation
        for (int i = 0; i < numColumns - 1; i++) {
            double currentEntry = matrix.getEntryAt(rowIndex, i);
            if (currentEntry != 0.0) {
                if (currentEntry != 1.0) {
                    double inverse = Matrix.findInverseNum(currentEntry);
                    oneRowOperation(rowIndex, inverse);
                }

                break;
            }
        }
    }

    private static void cleanUpColumns(int rowIndex) {
        // go through entire row (stop before last value) to find leading 1/first non-zero entry
        // once find it, loop up and down to create 0s

        // look for leading 1
        for (int i = 0; i < numColumns - 1; i++ ) {
            // check if non-zero
            if (matrix.getEntryAt(rowIndex, i) != 0.0) {
                createOnesInColumn(i, rowIndex);

                // clean up below
                for (int j = rowIndex + 1; j < numRows; j++) {
                    if (matrix.getEntryAt(j, i) != 0) {
                        twoRowOperation(j, rowIndex,1,1);
                    }
                }

                // clean up above
                for (int j = rowIndex - 1; j >= 0; j--) {
                    if (matrix.getEntryAt(j, i) != 0) {
                        twoRowOperation(j, rowIndex,1,1);
                    }
                }

                break;
            }
        }
    }

    private static boolean checkZeroSolutions() {
        // checks if system has zero solutions.
        // i.e. a row of zeros except for the last entry
        int zeroCount = 0;

        // count zeros for each row. stop before last digit
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns - 1; j++) {
                if (matrix.getEntryAt(i, j) == 0.0) {
                    zeroCount++;
                }
            }

            if (zeroCount == numColumns - 1) {
                // if last entry of row is not 0, then there are 0 solutions
                if (matrix.getEntryAt(i, numColumns - 1) != 0.0) {
                    //System.out.println("The system is inconsistent; there are zero solutions.");
                    matrix.setState(matrix.getState() + "\nThe system is inconsistent; there are zero solutions.");
                    return true;
                }

                zeroCount = 0;
            }
            else {
                zeroCount = 0;
            }
        }

        return false;
    }

    private static boolean checkInfiniteSolutions() {
        // infinite solutions: 1 of 2 scenarios (assume not inconsistent system since check for 0 sol before)
        // 1. more variables than equations (rows)
        // 2. at least 1 row with all zeros, and num of rows <= num of variables
        int zeroCount = 0;

        // more variables than equations
        if ((numColumns - 1) > numRows) {
            //System.out.println("System is consistent with infinite solutions.");
            matrix.setState(matrix.getState() + "\nSystem is consistent with infinite solutions.");
            return true;
        }

        // count zeros for each row
        // if any row has all zero for its entries, there are infinite solutions
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                if (matrix.getEntryAt(i, j) == 0.0) {
                    zeroCount++;
                }
            }

            if (zeroCount == numColumns) {
                // infinite solutions
                //System.out.println("System is consistent with infinite solutions.");
                matrix.setState(matrix.getState() + "\nSystem is consistent with infinite solutions.");
                return true;
            }
            else {
                zeroCount = 0;
            }
        }

        return false;
    }

    private static boolean checkOneSolution() {
        // if gets here, system is neither inconsistent or consistent and infinite. this is for finding values
        // every row will have a leading 1. no column will have a non-zero value except for a single 1.
        // will be 1s in main diagonal, simply attributing it to value on right
        // there might be zero rows below (if more rows than variables), so break if got all variables
        double difference = 0;

        //System.out.println("System has a unique solution:");
        matrix.setState(matrix.getState() + "\nSystem has a unique solution:");
        for (int i = 0; i < numRows; i++) {
            if (i >= numColumns - 1) {
                break;
            }
            difference = Matrix.getClosenessToInt(matrix.getEntryAt(i, numColumns-1));
            if (difference > -0.000000001 && difference < 0.000000001) {
                // number is an integer, convert to int/long to remove .0 decimal
                // f. ex. -1.0 becomes -1
                //System.out.println("x" + (i+1) + ": " + Math.round(matrix.getEntryAt(i, numColumns-1)));
                matrix.setSolution(matrix.getSolution() + "x" + (i+1) + ": " + Math.round(matrix.getEntryAt(i, numColumns-1)) + "\n");
            }
            else {
                //System.out.println("x" + (i+1) + ": " + matrix.getEntryAt(i, numColumns-1));
                matrix.setSolution(matrix.getSolution() + "x" + (i+1) + ": " + matrix.getEntryAt(i, numColumns-1) + "\n");
            }
        }

        return true;
    }

    private static void solveInfiniteSystem() {
        // separate the free variables from those that can be "defined"
        // any column with nothing but a single 1 (leading 1) can be defined,
        // any column with all zeros or more than 1 non-zero entry is free variable

        // true: can be defined. false: free variable
        boolean variables[] = new boolean[numColumns - 1];

        // check for each variable if it can be "defined" or if it's free
        for (int i = 0; i < variables.length; i++) {
            variables[i] = isOnlyOneInColumn(i);
        }

        // now that the free variables are defined, it's a matter of defining the non-free variables
        // for each non-free:
        // 1. check column for leading 1, get row with it
        // 2. check row
        // ex. 1 0 -3 4 5 -> x1 = 5 - 4*x4 + 3*x3.
        // 1. identify variable we want (x1, or element at index 0)
        // 2. start RHS with constant (b), here it's 5 (at index col-1)
        // 3. go through rest of row starting at index x_start + 1. if not col-1, add it to expression as xN (n being
        // index+1), with coefficient being negative of what it is in row
        // f. ex., when find 3 at index 2, then put -3 * x3 in expression
        int rowNum;
        String definedVariableExpressions[] = new String[numColumns - 1];

        // get expressions for defined variables, print free variables
        for (int i = 0; i < variables.length; i++) {
            if (variables[i]) {
                rowNum = findRowWithLeadingOne(i);
                definedVariableExpressions[i] = getInfiniteExpression(rowNum, i);
            }
            else {
                //System.out.println("x" + (i+1) + ": free");
                matrix.setSolution(matrix.getSolution() + "x" + (i+1) + ": free" + "\n");
            }
        }

        // display defined variables
        for (int i = 0; i < variables.length; i++) {
            if (variables[i]) {
                //System.out.println(definedVariableExpressions[i]);
                matrix.setSolution(matrix.getSolution() + definedVariableExpressions[i] + "\n");
            }
        }
    }

    private static boolean isOnlyOneInColumn(int colIndex) {
        // check if the value in column is a 1. that's the only case that it's a free var
        // may have some columns with a 1 and another number, or just one number but it is not a 1. these are free vars
        boolean hasOne = false;
        int numCount = 0;

        for (int i = 0; i < numRows; i++) {
            if (matrix.getEntryAt(i, colIndex) != 0) {
                if (numCount > 0) {
                    return false;
                }
                else if (matrix.getEntryAt(i, colIndex) != 1) {
                    return false;
                }
                else {
                    hasOne = true;
                    numCount++;
                }
            }
        }

        if (hasOne && numCount == 1) {
            return true;
        }

        return false;
    }

    private static int findRowWithLeadingOne(int colIndex) {
        for (int i = 0; i < numRows; i++) {
            if (matrix.getEntryAt(i, colIndex) == 1.0) {
                return i;
            }
        }

        return -1; // should never get to this line (but could add check in calling function), like catch ...Error
    }

    private static String getInfiniteExpression(int rowIndex, int colIndex) {
        // ex. 1 0 -3 4 5 -> x1 = 5 - 4*x4 + 3*x3.
        // 1. identify variable we want (x1, or element at index 0)
        // 2. start RHS with constant (b), here it's 5 (at index col-1)
        // 3. go through rest of row starting at index x_start + 1. if not col-1, add it to expression as xN (n being
        // index+1), with coefficient being negative of what it is in row
        // f. ex., when find 3 at index 2, then put -3 * x3 in expression

        // step 1
        String expression = "x" + String.valueOf(colIndex+1); // 1

        double currentValue = matrix.getEntryAt(rowIndex, numColumns-1);
        double difference = Matrix.getClosenessToInt(currentValue);

        // step 2
        if (currentValue != 0.0) {
            if (difference > -0.000000001 && difference < 0.000000001) {
                // number is an integer, convert to int/long to remove .0 decimal
                // f. ex. -1.0 becomes -1
                expression += " = " + String.valueOf(Math.round(currentValue));
            }
            else {
                expression += " = " + String.valueOf(currentValue);
            }
        }
        else {
            expression += " = ";
        }

        // step 3
        for (int i = colIndex + 1; i < numColumns - 1; i++) {
            currentValue = matrix.getEntryAt(rowIndex, i);

            if (currentValue == 0.0) {
                // don't display anything for current variable since it has 0 coefficient in this row
                continue;
            }

            difference = Matrix.getClosenessToInt(currentValue);

            // maybe put the current value < 0 if/else stuff in short function
            if (difference > -0.000000001 && difference < 0.000000001) {
                // number is an integer, convert to int/long to remove .0 decimal
                // f. ex. -1.0 becomes -1
                if (currentValue < 0) {
                    if (currentValue == -1.0) {
                        // don't display if it's 1 or -1 since it's a useless coefficient
                        expression += " + x" + String.valueOf(i+1);
                    }
                    else {
                        expression += " + " + String.valueOf(Math.round(-currentValue))+ "*x" + String.valueOf(i+1);
                    }
                }
                else {
                    if (currentValue == 1.0) {
                        expression += " - x" + String.valueOf(i+1);
                    }
                    else {
                        expression += " - " + String.valueOf(Math.round(currentValue))+ "*x" + String.valueOf(i+1);
                    }
                }
            }
            else {
                // if number is not an integer, can't be 1 or -1 so don't handle
                if (currentValue < 0) {
                    expression += " + " + String.valueOf(-currentValue)+ "*x" + String.valueOf(i+1);
                }
                else {
                    expression += " - " + String.valueOf(currentValue) + "*x" + String.valueOf(i+1);
                }
            }
        }

        // check in case that the constant value is 0. if it is, then the string will currently
        String trueExpression = "";
        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);
            if (currentChar == '-' || currentChar == '+') {
                // can assume that i - 3 will be within bounds since there will always be at minimum xi and a space
                // at the start of the string
                // if there is no constant, then there will be two spaces between = and +/- just because of the way
                // the formatting works
                if (expression.charAt(i - 3) == '=') {
                    // remove the two extra spaces
                    trueExpression = trueExpression.substring(0, trueExpression.length() - 2);
                    continue;
                }
            }

            trueExpression += currentChar;
        }

        return trueExpression;
    }
}


