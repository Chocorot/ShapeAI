package net.chocorot.shape_ai.V1;

import net.chocorot.shape_ai.Matrix;

import javax.swing.*;

public class AI {

    private static final int drawBoardSize = DrawBoard.drawBoardSize;
    private static final int bias = DrawBoard.bias;

    public static void testResult(long[][] pixelMatrix) {
        long[][] weightMatrix = Matrix.read("res/V1/weight.dat");
        double result = Matrix.multiply(pixelMatrix, weightMatrix, drawBoardSize*drawBoardSize);
        System.out.print("Output: ");
        System.out.print(result);
        System.out.print(" | ");
        System.out.print("Bias: ");
        System.out.print(bias);
        System.out.print(" | ");
        System.out.println(result<=bias ? "Square" : "Circle");


        JOptionPane.showMessageDialog(DrawBoard.getInstance(), "Is this a " + (result<=bias ? "Square" : "Circle"), "Result", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to calculate the pixel matrix (black=1, white=0) as a 1D array
    public static void calculateMatrix(long[][] pixelMatrix) {

        long[][] weightMatrix = Matrix.read("res/V1/weight.dat");
        double result = Matrix.multiply(pixelMatrix, weightMatrix, drawBoardSize*drawBoardSize);
        System.out.print("Output: ");
        System.out.print(result);
        System.out.print(" | ");
        System.out.print("Bias: ");
        System.out.print(bias);
        System.out.print(" | ");
        System.out.println(result<=bias ? "Square" : "Circle");

        String[] options = new String[] {"Square", "Circle", "Cancel"};
        int select = JOptionPane.showOptionDialog(
                DrawBoard.getInstance(),
                "AI Result: " + (result<=bias ? "Square" : "Circle") + "\nSelect the correct shape",
                "What shape is this? ",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);
        // Square = 0, Circle = 1
        while (select == 1 && result<=bias) { // Human: Circle,  AI: Square
            long[][] convertPixelMatrix = Matrix.reflect(pixelMatrix);
            weightMatrix = Matrix.plus(convertPixelMatrix, weightMatrix);
            Matrix.save(weightMatrix, "res/V1/weight.dat");
            result = Matrix.multiply(pixelMatrix, weightMatrix, drawBoardSize*drawBoardSize);
            System.out.print("Output: ");
            System.out.print(result);
            System.out.print(" | ");
            System.out.print("Bias: ");
            System.out.print(bias);
            System.out.print(" | ");
            System.out.println(result<=bias ? "Square" : "Circle");

        }
        while(select == 0 && result>bias){ // Human: Square,  AI: Not Square
            long[][] convertPixelMatrix = Matrix.reflect(pixelMatrix);
            weightMatrix = Matrix.subtract(weightMatrix, convertPixelMatrix);
            Matrix.save(weightMatrix, "res/V1/weight.dat");
            result = Matrix.multiply(pixelMatrix, weightMatrix, drawBoardSize*drawBoardSize);
            System.out.print("Output: ");
            System.out.print(result);
            System.out.print(" | ");
            System.out.print("Bias: ");
            System.out.print(bias);
            System.out.print(" | ");
            System.out.println(result<=bias ? "Square" : "Circle");
        }
    }



}
