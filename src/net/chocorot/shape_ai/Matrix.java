package net.chocorot.shape_ai;

import java.io.*;

public class Matrix {

    // Function to multiply two matrices
    public static long multiply(long[][] rowVector, long[][] columnVector, int size) {
        // Check if the dimensions are correct
        if (rowVector[0].length != size || columnVector.length != size || columnVector[0].length != 1) {
            throw new IllegalArgumentException("Invalid matrix dimensions.");
        }

        // Perform multiplication
        long result = 0;
        for (int i = 0; i < size; i++) {
            result += rowVector[0][i] * columnVector[i][0];
        }

        return result;
    }

    // Function to add two matrices
    public static long[][] plus(long[][] matrixA, long[][] matrixB) {
        // Validate dimensions
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.\n"+matrixA.length+"\n"+matrixB.length+"\n"+matrixA[0].length+"\n"+matrixB[0].length);
        }

        // Create a result matrix with the same dimensions
        long[][] result = new long[matrixA.length][matrixA[0].length];

        // Perform addition
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixA[0].length; j++) {
                result[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }

        return result; // Return the result matrix
    }

    // Function to subtract two matrices
    public static long[][] subtract(long[][] matrixA, long[][] matrixB) {
        // Validate dimensions
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }

        // Create a result matrix with the same dimensions
        long[][] result = new long[matrixA.length][matrixA[0].length];

        // Perform addition
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixA[0].length; j++) {
                result[i][j] = matrixA[i][j] - matrixB[i][j];
            }
        }

        return result; // Return the result matrix
    }

    // Function to save matrix to a file
    public static void save(long[][] matrix, String filename) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(matrix);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Function to read matrix from a file
    public static long[][] read(String filename){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (long[][]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Function to convert the row to column
    public static long[][] reflect(long[][] rowVector) {
        // Validate the input is a 1xN matrix
        if (rowVector.length != 1) {
            throw new IllegalArgumentException("Input must be a 1xN matrix." + rowVector.length);
        }

        // Step 1: Get the number of columns in the row vector
        int n = rowVector[0].length;

        // Step 2: Create a Nx1 matrix (column vector)
        long[][] columnVector = new long[n][1];

        // Step 3: Copy values from the row vector to the column vector
        for (int i = 0; i < n; i++) {
            columnVector[i][0] = rowVector[0][i];
        }

        return columnVector; // Return the column vector
    }
}
