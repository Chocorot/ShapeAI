package net.chocorot.shape_ai;

import java.util.stream.IntStream;

public class Playground {
    public static long multiplyParallel(long[][] rowVector, long[][] columnVector, int size) {
        // Check if the dimensions are correct
        if (rowVector[0].length != size || columnVector.length != size || columnVector[0].length != 1) {
            throw new IllegalArgumentException("Invalid matrix dimensions.");
        }

        // Perform multiplication in parallel
        return IntStream.range(0, size).parallel().mapToLong(i -> rowVector[0][i] * columnVector[i][0]).sum();
    }

    public static void main(String[] args) {
        long[][] rowVector = {{1, 2, 3}};
        long[][] columnVector = {{4}, {5}, {6}};
        int size = 3;

        long result = Matrix.multiply(rowVector, columnVector, size);

        System.out.println("Result of multiplication: " + result);  // Output: 32
    }
}
