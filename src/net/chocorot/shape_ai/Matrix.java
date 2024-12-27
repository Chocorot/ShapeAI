package net.chocorot.shape_ai;

import java.io.*;
import java.util.stream.IntStream;

public class Matrix {

    // Function to multiply two matrices
    public static long multiply(long[][] rowVector, long[][] columnVector, int size) {
        // Check if the dimensions are correct
        if (rowVector[0].length != size || columnVector.length != size || columnVector[0].length != 1) {
            throw new IllegalArgumentException("Invalid matrix dimensions.");
        }

        long result = IntStream.range(0, size).parallel().mapToLong(i -> rowVector[0][i] * columnVector[i][0]).sum();
//        System.out.println(result);
        // Perform multiplication in parallel
        return result;
    }
    public static double[][] multiply(double[][] matrixA, double[][] matrixB) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int rowsB = matrixB.length;
        int colsB = matrixB[0].length;

        // Check if the dimensions are correct
        if (colsA != rowsB) {
            throw new IllegalArgumentException("Invalid matrix dimensions. "+ rowsA + " " + colsA + " " + rowsB + " " + colsB);
        }

        double[][] result = new double[rowsA][colsB];
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        // Perform multiplication in parallel
        return result;
    }
    public static double[][] multiply(double matrixA, double[][] matrixB) {
        int rowsA = 1;
        int colsA = 1;
        int rowsB = matrixB.length;
        int colsB = matrixB[0].length;


        double[][] result = new double[rowsB][colsB];
        for (int i = 0; i < rowsB; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA * matrixB[k][j];
                }
            }
        }
        // Perform multiplication in parallel
        return result;
    }
    public static double[][] multiply(double[][] matrixB, double matrixA) {
        int rowsA = 1;
        int colsA = 1;
        int rowsB = matrixB.length;
        int colsB = matrixB[0].length;

        // Check if the dimensions are correct
        if (colsA != rowsB) {
            throw new IllegalArgumentException("Invalid matrix dimensions.");
        }

        double[][] result = new double[rowsA][colsB];
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA * matrixB[k][j];
                }
            }
        }
        // Perform multiplication in parallel
        return result;
    }

    // Function to add two matrices
    public static long[][] plus(long[][] matrixA, long[][] matrixB) {
        // Validate dimensions
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }

        // Create a result matrix with the same dimensions
        long[][] result = new long[matrixA.length][matrixA[0].length];

        // Perform the addition in parallel for both rows and columns
        IntStream.range(0, matrixA.length).parallel().forEach(i -> {
            IntStream.range(0, matrixA[0].length).parallel().forEach(j -> {
                result[i][j] = matrixA[i][j] + matrixB[i][j];
            });
        });

        return result; // Return the result matrix after the computation is complete
    }
    public static double[][] plus(double[][] matrixA, double[][] matrixB) {
        // Validate dimensions
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions. ("+matrixA.length + "x" + matrixA[0].length + ") + ("+ matrixB.length + "x" + matrixB[0].length + ")");
        }

        // Create a result matrix with the same dimensions
        double[][] result = new double[matrixA.length][matrixA[0].length];

        // Perform the addition in parallel for both rows and columns
        IntStream.range(0, matrixA.length).parallel().forEach(i -> {
            IntStream.range(0, matrixA[0].length).parallel().forEach(j -> {
                result[i][j] = matrixA[i][j] + matrixB[i][j];
            });
        });

        return result; // Return the result matrix after the computation is complete
    }

    // Function to subtract two matrices
    public static long[][] subtract(long[][] matrixA, long[][] matrixB) {
        // Validate dimensions
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions.");
        }

        // Create a result matrix with the same dimensions
        long[][] result = new long[matrixA.length][matrixA[0].length];

        // Perform the subtraction in parallel for both rows and columns
        IntStream.range(0, matrixA.length).parallel().forEach(i -> {
            IntStream.range(0, matrixA[0].length).parallel().forEach(j -> {
                result[i][j] = matrixA[i][j] - matrixB[i][j];
            });
        });

        return result; // Return the result matrix after the computation is complete
    }
    public static double[][] subtract(double[][] matrixA, double[][] matrixB) {
        // Validate dimensions
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions. " + matrixA.length + " " + matrixA[0].length + " " + matrixB.length + " " + matrixB[0].length);
        }

        // Create a result matrix with the same dimensions
        double[][] result = new double[matrixA.length][matrixA[0].length];

        // Perform the subtraction in parallel for both rows and columns
        IntStream.range(0, matrixA.length).parallel().forEach(i -> {
            IntStream.range(0, matrixA[0].length).parallel().forEach(j -> {
                result[i][j] = matrixA[i][j] - matrixB[i][j];
            });
        });

        return result; // Return the result matrix after the computation is complete
    }
    public static double[][] subtract(double[][] matrixA, double matrixB) {
        // Create a result matrix with the same dimensions
        double[][] result = new double[matrixA.length][matrixA[0].length];

        // Perform the subtraction in parallel for both rows and columns
        IntStream.range(0, matrixA.length).parallel().forEach(i -> {
            IntStream.range(0, matrixA[0].length).parallel().forEach(j -> {
                result[i][j] = matrixA[i][j] - matrixB;
            });
        });

        return result; // Return the result matrix after the computation is complete
    }
    public static double[][] subtract(double matrixA, double[][] matrixB) {
        // Create a result matrix with the same dimensions
        double[][] result = new double[matrixB.length][matrixB[0].length];

        // Perform the subtraction in parallel for both rows and columns
        IntStream.range(0, matrixB.length).parallel().forEach(i -> {
            IntStream.range(0, matrixB[0].length).parallel().forEach(j -> {
                result[i][j] = matrixA - matrixB[i][j];
            });
        });

        return result; // Return the result matrix after the computation is complete
    }

    // Function to save matrix to a file
    public static void save(long[][] matrix, String filename) {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(matrix);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save(double matrix, String filename) {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(matrix);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save(double[] matrix, String filename) {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(matrix);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save(double[][] matrix, String filename) {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(matrix);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void save(double[][][] matrix, String filename) {
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(matrix);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Function to read matrix from a file
    public static long[][] readV2(String filename){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (long[][]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static double readV0(String filename){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (double) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return 0;
        }
    }
    public static double[] readV1(String filename){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (double[]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
    public static double[][] readV2D(String filename){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (double[][]) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
    public static double[][][] readV3(String filename){
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (double[][][]) in.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
            return null;
        }
    }

    // Function to convert the row to column
    public static long[][] transpose(long[][] rowVector) {
        // Validate the input is a 1xN matrix
        if (rowVector.length != 1) {
            throw new IllegalArgumentException("Input must be a 1xN matrix. Provided: " + rowVector.length);
        }

        // Step 1: Get the number of columns in the row vector
        int n = rowVector[0].length;

        // Step 2: Create a Nx1 matrix (column vector)
        long[][] columnVector = new long[n][1];

        // Step 3: Use a single System.arraycopy call to copy the entire row
        for (int i = 0; i < n; i++) {
            columnVector[i][0] = rowVector[0][i];
        }

        return columnVector;
    }
    public static double[][] transpose(double[][] matrix) {
        // Get the number of rows and columns in the original matrix
        int rows = matrix.length;
        int cols = matrix[0].length;

        // Create a new matrix to hold the transposed version
        double[][] transposed = new double[cols][rows];

        // Loop through the original matrix and fill the transposed matrix
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }

        return transposed;
    }

    public static double sigmoid(double matrix) {
        return 1.0 / (1.0 + Math.exp(-matrix));
    }
    public static double[][] sigmoid(double[][] matrix) {
        double[][] result = new double[matrix.length][matrix[0].length];
        for (int j = 0; j < matrix.length; j++) {
            for (int i = 0; i < matrix[j].length; i++) {
                result[j][i] = sigmoid(matrix[j][i]);
            }
        }
        return result;
    }
    public static double[][] sigmoidDerivative(double[][] prediction) {
        double[][] result = new double[prediction.length][prediction[0].length];
        for (int j = 0; j < prediction.length; j++) {
            for (int i = 0; i < prediction[j].length; i++) {
                result[j][i] = sigmoidDerivative(prediction[j][i]);
            }
        }
        return result;
    }
    public static double sigmoidDerivative(double prediction) {
        return (prediction) * (1-prediction);
    }


    // Function to perform Hadamard product of two matrices
    public static double[][] dot(double[][] matrixA, double[][] matrixB) {
        // Check if matrices are of the same dimension
        if (matrixA.length == 0 || matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions ("+matrixA.length+"x"+matrixA[0].length+") . ("+matrixB.length+"x"+matrixB[0].length+")");
        }

        // Initialize the result matrix with the same dimensions
        double[][] result = new double[matrixA.length][matrixA[0].length];

        // Calculate Hadamard product
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixA[i].length; j++) {
                result[i][j] = matrixA[i][j] * matrixB[i][j];
            }
        }

        return result;
    }

    public static double mean(double[][] matrix) {
        int length = matrix.length * matrix[0].length;
        double sum = 0;
        for (double[] doubles : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                sum += doubles[j];
            }
        }
        return sum/length;
    }

    public static double[][] square(double[][] matrix) {
        double[][] square = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                square[i][j] = matrix[i][j] * matrix[i][j];
            }
        }
        return square;
    }
}
