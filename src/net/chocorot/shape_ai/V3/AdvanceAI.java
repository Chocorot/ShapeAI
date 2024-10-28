package net.chocorot.shape_ai.V3;

import net.chocorot.shape_ai.Matrix;

import javax.swing.*;

public class AdvanceAI {
    private static final int drawBoardSize = DrawBoard3.drawBoardSize;
    long[][] n0 = new long[1][n0B.length];
    long[][] n1 = new long[1][n1B.length];
    long nR;
    public static int[] n0B = DrawBoard3.n0B;
    public static int[] n1B = DrawBoard3.n1B;
    public static int nRB = DrawBoard3.nRB;

    long[][][] n0Weights = new long[n0B.length][][];
    long[][][] n1Weights = new long[n1B.length][][];
    long[][] nRWeight;

    public AdvanceAI() {
//        initWeight();
        try {
            for (int i = 0; i < n0Weights.length; i++) {
                n0Weights[i] = Matrix.read(DrawBoard3.weightLoc + "n0_" + i + "-weight.dat");
            }
            for (int i = 0; i < n1Weights.length; i++) {
                n1Weights[i] = Matrix.read(DrawBoard3.weightLoc + "n1_" + i + "-weight.dat");
            }
            nRWeight = Matrix.read(DrawBoard3.weightLoc + "nR-weight.dat");
        } catch (Exception ignored) {
            System.out.println("Weight data error");
        }
    }

    public void neuronTraining(long[][] pixelMatrix, int select) {
        nR = neuronResult(pixelMatrix);

        // 0 for square 1 for circle
        while (select == 1 && nR <= nRB) { // Human: Circle,  AI: Square , Doesn't fire when it should, add
            for (int i = 0; i < n1B.length; i++) { // Correct every n1 weight
                while (n1[0][i] <= n1B[i]) { // doesn't fire when should
                    for (int j = 0; j < n0B.length; j++) { // Correct every n0 weight, n0 will be correct before n1
                        while (n0[0][j] <= n0B[j]) { // doesn't fire when should
                            n0Weights[j] = Matrix.plus(n0Weights[j], Matrix.reflect(pixelMatrix));
                            Matrix.save(n0Weights[j], DrawBoard3.weightLoc + "n0_" + j + "-weight.dat");
                            n0[0][j] = Matrix.multiply(pixelMatrix, n0Weights[j], drawBoardSize * drawBoardSize);
                            printData(n0, n1, nR);
                        }
                        printData(n0, n1, nR);
                    }
                    n1Weights[i] = Matrix.plus(n1Weights[i], Matrix.reflect(n0));
                    Matrix.save(n1Weights[i], DrawBoard3.weightLoc + "n1_" + i + "-weight.dat");
                    n1[0][i] = Matrix.multiply(n0, n1Weights[i], n0[0].length);
                    printData(n0, n1, nR);
                }

            }

            nRWeight = Matrix.plus(nRWeight, Matrix.reflect(n1));
            Matrix.save(nRWeight, DrawBoard3.weightLoc + "nR-weight.dat");
            nR = Matrix.multiply(n1, nRWeight, n1[0].length);
            printData(n0, n1, nR);
        }

        while (select == 0 && nR > nRB) { // Human: Square,  AI: Circle , fire when it shouldn't, subtract
            for (int i = 0; i < n1B.length; i++) { // Correct every n1 weight
                while (n1[0][i] > n1B[i]) { // fire when shouldn't
                    for (int j = 0; j < n0B.length; j++) { // Correct every n0 weight, n0 will be correct before n1
                        while (n0[0][j] > n0B[j]) { // fire when shouldn't
                            n0Weights[j] = Matrix.subtract(n0Weights[j], Matrix.reflect(pixelMatrix));
                            Matrix.save(n0Weights[j], DrawBoard3.weightLoc + "n0_" + j + "-weight.dat");
                            n0[0][j] = Matrix.multiply(pixelMatrix, n0Weights[j], drawBoardSize * drawBoardSize);
                            printData(n0, n1, nR);
                        }
                        printData(n0, n1, nR);
                    }
                    n1Weights[i] = Matrix.subtract(n1Weights[i], Matrix.reflect(n0));
                    Matrix.save(n1Weights[i], DrawBoard3.weightLoc + "n1_" + i + "-weight.dat");
                    n1[0][i] = Matrix.multiply(n0, n1Weights[i], n0[0].length);
                    printData(n0, n1, nR);
                }

            }

            nRWeight = Matrix.subtract(nRWeight, Matrix.reflect(n1));
            Matrix.save(nRWeight, DrawBoard3.weightLoc + "/nR-weight.dat");
            nR = Matrix.multiply(n1, nRWeight, n1[0].length);
            printData(n0, n1, nR);
        }
    }

    public long neuronResult(long[][] pixelMatrix) {
        for (int i = 0; i < n0[0].length; i++) {
            n0[0][i] = Matrix.multiply(pixelMatrix, n0Weights[i], drawBoardSize * drawBoardSize);
        }
        for (int i = 0; i < n1[0].length; i++) {
            n1[0][i] = Matrix.multiply(n0, n1Weights[i], n0[0].length);
        }
        nR = Matrix.multiply(n1, nRWeight, n1[0].length);
        printData(n0, n1, nR);
        return nR;
    }

    private void printData(long[][] n0, long[][] n1, long nR) {
        System.out.print("NR: ");
        System.out.print(nR);
        for (int i = 0; i < n0[0].length; i++) {
            System.out.print(", N0-"+i+": ");
            System.out.print(n0[0][i]);
        }
        for (int i = 0; i < n1[0].length; i++) {
            System.out.print(", N1-"+i+": ");
            System.out.print(n1[0][i]);
        }
        System.out.print(" | ");
        System.out.print("NR Bias: ");
        System.out.print(nRB);
        System.out.print(" | ");
        System.out.println(nR <= nRB ? "Square" : "Circle");
    }

    public void initWeight() {
        long[][][] n0Weight = new long[n0B.length][drawBoardSize * drawBoardSize][1];
        long[][][] n1Weight = new long[n1B.length][n0B.length][1];
        long[][] resultWeight = new long[n1B.length][1];
        for (int i = 0; i < n0Weight.length; i++) {
            for (int j = 0; j < n0Weight[i].length; j++) {
                n0Weight[i][j][0] = 0;
            }
            Matrix.save(n0Weight[i], DrawBoard3.weightLoc + "n0_" + i + "-weight.dat");
        }
        for (int i = 0; i < n1Weight.length; i++) {
            for (int j = 0; j < n1Weight[i].length; j++) {
                n1Weight[i][j][0] = 0;
            }
            Matrix.save(n1Weight[i], DrawBoard3.weightLoc + "n1_" + i + "-weight.dat");
        }
        for (int i = 0; i < resultWeight.length; i++) {
            resultWeight[i][0] = 0;
        }
        Matrix.save(resultWeight, DrawBoard3.weightLoc + "nR-weight.dat");
        JOptionPane.showConfirmDialog(DrawBoard3.getInstance(), "The weight has been reset.", "Weight Reset", JOptionPane.DEFAULT_OPTION);
        System.out.println("Weight is being reset");
    }

}
