package net.chocorot.shape_ai.V2;

import net.chocorot.shape_ai.Matrix;

import javax.swing.*;

public class AdvanceAI {
    private static final int drawBoardSize = DrawBoard2.drawBoardSize;
    long[][] nh = new long[1][4];
    long nr;
    int n1B = DrawBoard2.n1B, n2B = DrawBoard2.n2B, n3B = DrawBoard2.n3B, n4B = DrawBoard2.n4B, nrB = DrawBoard2.nrB;

    public void neuronTraining(long[][] pixelMatrix)  {
        long[][] n1Weight = Matrix.read("res/V2/n1-weight.dat");
        long[][] n2Weight = Matrix.read("res/V2/n2-weight.dat");
        long[][] n3Weight = Matrix.read("res/V2/n3-weight.dat");
        long[][] n4Weight = Matrix.read("res/V2/n4-weight.dat");
        long[][] nrWeight = Matrix.read("res/V2/nr-weight.dat");

        nr = neuronResult(pixelMatrix);

        // Square = 0, Circle = 1
        String[] options = new String[] {"Square", "Circle", "Cancel"};
        int select = JOptionPane.showOptionDialog(
                DrawBoard2.getInstance(),
                "AI Result: " + (nr<=nrB ? "Square" : "Circle") + "\nSelect the correct shape",
                "What shape is this? ",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        // 0 for square 1 for circle

        while (select == 1 && nr<=nrB) { // Human: Circle,  AI: Square , Doesn't fire when it should, add
            while (nh[0][0]<=n1B) {
                n1Weight = Matrix.plus(n1Weight, Matrix.reflect(pixelMatrix));
                Matrix.save(n1Weight, "res/V2/n1-weight.dat");
                nh[0][0] = Matrix.multiply(pixelMatrix, n1Weight, drawBoardSize*drawBoardSize);
                printData(nh, nr);
            }
            while (nh[0][1]<=n2B) {
                n2Weight = Matrix.plus(n2Weight, Matrix.reflect(pixelMatrix));
                Matrix.save(n2Weight, "res/V2/n2-weight.dat");
                nh[0][1] = Matrix.multiply(pixelMatrix, n2Weight, drawBoardSize*drawBoardSize);
                printData(nh, nr);
            }
            while (nh[0][2]<=n3B) {
                n3Weight = Matrix.plus(n3Weight, Matrix.reflect(pixelMatrix));
                Matrix.save(n3Weight, "res/V2/n3-weight.dat");
                nh[0][2] = Matrix.multiply(pixelMatrix, n3Weight, drawBoardSize*drawBoardSize);
                printData(nh, nr);
            }
            while (nh[0][3]<=n4B) {
                n4Weight = Matrix.plus(n4Weight, Matrix.reflect(pixelMatrix));
                Matrix.save(n4Weight, "res/V2/n4-weight.dat");
                nh[0][3] = Matrix.multiply(pixelMatrix, n4Weight, drawBoardSize*drawBoardSize);
                printData(nh, nr);
            }

            nrWeight = Matrix.plus(nrWeight, Matrix.reflect(nh));
            Matrix.save(nrWeight, "res/V2/nr-weight.dat");
            nr = Matrix.multiply(nh, nrWeight, 4);
            printData(nh, nr);
        }

        while (select == 0 && nr>nrB) { // Human: Square,  AI: Circle , fire when it shouldn't, subtract
            while (nh[0][0]>n1B) {
                n1Weight = Matrix.subtract(n1Weight, Matrix.reflect(pixelMatrix));
                Matrix.save(n1Weight, "res/V2/n1-weight.dat");
                nh[0][0] = Matrix.multiply(pixelMatrix, n1Weight, drawBoardSize*drawBoardSize);
                printData(nh, nr);
            }
            while (nh[0][1]>n2B) {
                n2Weight = Matrix.subtract(n2Weight, Matrix.reflect(pixelMatrix));
                Matrix.save(n2Weight, "res/V2/n2-weight.dat");
                nh[0][1] = Matrix.multiply(pixelMatrix, n2Weight, drawBoardSize*drawBoardSize);
                printData(nh, nr);
            }
            while (nh[0][2]>n3B) {
                n3Weight = Matrix.subtract(n3Weight, Matrix.reflect(pixelMatrix));
                Matrix.save(n3Weight, "res/V2/n3-weight.dat");
                nh[0][2] = Matrix.multiply(pixelMatrix, n3Weight, drawBoardSize*drawBoardSize);
                printData(nh, nr);
            }
            while (nh[0][3]>n4B) {
                n4Weight = Matrix.subtract(n4Weight, Matrix.reflect(pixelMatrix));
                Matrix.save(n4Weight, "res/V2/n4-weight.dat");
                nh[0][3] = Matrix.multiply(pixelMatrix, n4Weight, drawBoardSize*drawBoardSize);
                printData(nh, nr);
            }

            nrWeight = Matrix.subtract(nrWeight, Matrix.reflect(nh));
            Matrix.save(nrWeight, "res/V2/nr-weight.dat");
            nr = Matrix.multiply(nh, nrWeight, 4);
            printData(nh, nr);
        }
    }

    public long neuronResult(long[][] pixelMatrix) {
        long[][] n1Weight = Matrix.read("res/V2/n1-weight.dat");
        long[][] n2Weight = Matrix.read("res/V2/n2-weight.dat");
        long[][] n3Weight = Matrix.read("res/V2/n3-weight.dat");
        long[][] n4Weight = Matrix.read("res/V2/n4-weight.dat");
        long[][] nrWeight = Matrix.read("res/V2/nr-weight.dat");

        nh[0][0] = Matrix.multiply(pixelMatrix, n1Weight, drawBoardSize*drawBoardSize);
        nh[0][1] = Matrix.multiply(pixelMatrix, n2Weight, drawBoardSize*drawBoardSize);
        nh[0][2] = Matrix.multiply(pixelMatrix, n3Weight, drawBoardSize*drawBoardSize);
        nh[0][3] = Matrix.multiply(pixelMatrix, n4Weight, drawBoardSize*drawBoardSize);
        nr = Matrix.multiply(nh, nrWeight, nh[0].length);
        printData(nh, nr);
        return nr;
    }

    private void printData(long[][] nh ,long nr) {
        System.out.print("Nr: ");
        System.out.print(nr);
        System.out.print(", N1: ");
        System.out.print(nh[0][0]);
        System.out.print(", N2: ");
        System.out.print(nh[0][1]);
        System.out.print(", N3: ");
        System.out.print(nh[0][2]);
        System.out.print(", N4: ");
        System.out.print(nh[0][3]);
        System.out.print(" | ");
        System.out.print("Nr Bias: ");
        System.out.print(nrB);
        System.out.print(" | ");
        System.out.println(nr<=nrB ? "Square" : "Circle");
    }

    public void initWeight() {
        long[][] hiddenWeight = new long[drawBoardSize*drawBoardSize][1];
        long[][] resultWeight = new long[4][1];
        for (int i = 0; i<(drawBoardSize * drawBoardSize); i++) {
            hiddenWeight[i][0] = 0;
        }
        for (int i = 0; i<(4); i++) {
            resultWeight[i][0] = 0;
        }
        Matrix.save(hiddenWeight, "res/V2/n1-weight.dat");
        Matrix.save(hiddenWeight, "res/V2/n2-weight.dat");
        Matrix.save(hiddenWeight, "res/V2/n3-weight.dat");
        Matrix.save(hiddenWeight, "res/V2/n4-weight.dat");
        Matrix.save(resultWeight, "res/V2/nr-weight.dat");
        System.out.println("Weight is being reset");
    }

}
