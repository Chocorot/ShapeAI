package net.chocorot.shape_ai.V4;

import net.chocorot.shape_ai.Matrix;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class AdvanceAI {
    private final int drawBoardSize = DrawBoard4.drawBoardSize;
    private final String weightLoc = DrawBoard4.weightLoc;
    private int batchSize = DrawBoard4.batchSize;
    private final double learningRate = 1;

    int n0Size = 10;
    int n1Size = 7;
    int nRSize = 1;
    double[][] n0Weights = new double[drawBoardSize * drawBoardSize][n0Size]; //900x10
    double[][] n1Weights = new double[n0Size][n1Size]; //10x5
    double[][] nRWeights = new double[n1Size][nRSize]; //5x1
    double[][] n0DeltaWeights = new double[drawBoardSize * drawBoardSize][n0Size];
    double[][] n1DeltaWeights = new double[n0Size][n1Size];
    double[][] nRDeltaWeights = new double[n1Size][nRSize];
    double[][] n0Activation;
    double[][] n1Activation;
    double[][] nRActivation;

    public AdvanceAI() {
        setupWeight();
    }

//    public void neuronTraining(double[][] pixelMatrix, int select) {
//        // Create an executor service with a thread pool
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//        nR = neuronResult(pixelMatrix);
//
//        // 0 for square 1 for circle
//        Future<?> task1 = executor.submit(() -> {
//            while (select == 1 && nR <= nRB) { // Human: Circle,  AI: Square , Doesn't fire when it should, add
//
//
//
//
//                Future<?>[] n2Loop = new Future[n2[0].length];
//                for (int h = 0; h < n2B.length; h++) {
//                    final int fH = h;
//                    n2Loop[h] = executor.submit(() -> {
//                        while (n2[0][fH] <= n2B[fH]) {
//                            Future<?>[] n1Loop = new Future[n1[0].length];
//                            for (int i = 0; i < n1B.length; i++) { // Correct every n1 weight
//                                final int fI = i;
//                                n1Loop[i] = executor.submit(() -> {
//                                    while (n1[0][fI] <= n1B[fI]) { // doesn't fire when should
//                                        Future<?>[] n0Loop = new Future[n0[0].length];
//                                        for (int j = 0; j < n0B.length; j++) { // Correct every n0 weight, n0 will be correct before n1
//                                            final int fJ = j;
//                                            n0Loop[j] = executor.submit(() -> {
//                                                while (n0[0][fJ] <= n0B[fJ]) { // doesn't fire when should
//                                                    n0Weights[fJ] = Matrix.plus(n0Weights[fJ], Matrix.transpose(pixelMatrix));
//                                                    Matrix.save(n0Weights[fJ], DrawBoard4.weightLoc + "n0_" + fJ + "-weight.dat");
//                                                    n0[0][fJ] = Matrix.multiply(pixelMatrix, n0Weights[fJ], drawBoardSize * drawBoardSize);
//                                                    printData(n0, n1, n2, nR);
//                                                }
//                                            });
//                                        }
//                                        // Wait for all tasks in the first loop to finish
//                                        for (Future<?> future : n0Loop) {
//                                            try {
//                                                future.get(); // Wait for completion
//                                            } catch (Exception e) {throw new RuntimeException(e);}
//                                        }
//                                        n1Weights[fI] = Matrix.plus(n1Weights[fI], Matrix.transpose(n0));
//                                        Matrix.save(n1Weights[fI], DrawBoard4.weightLoc + "n1_" + fI + "-weight.dat");
//                                        n1[0][fI] = Matrix.multiply(n0, n1Weights[fI], n0[0].length);
//                                        printData(n0, n1, n2, nR);
//                                    }
//                                });
//
//                            }
//                            // Wait for all tasks in the first loop to finish
//                            for (Future<?> future : n1Loop) {
//                                try {
//                                    future.get(); // Wait for completion
//                                } catch (Exception e) {throw new RuntimeException(e);}
//                            }
//                            n2Weights[fH] = Matrix.plus(n2Weights[fH], Matrix.transpose(n1));
//                            Matrix.save(n2Weights[fH], DrawBoard4.weightLoc + "n2_" + fH + "-weight.dat");
//                            n2[0][fH] = Matrix.multiply(n1, n2Weights[fH], n1[0].length);
//                            printData(n0, n1, n2, nR);
//                        }
//                    });
//                }
//                // Wait for all tasks in the first loop to finish
//                for (Future<?> future : n2Loop) {
//                    try {
//                        future.get(); // Wait for completion
//                    } catch (Exception e) {throw new RuntimeException(e);}
//                }
//
//                nRWeights = Matrix.plus(nRWeights, Matrix.transpose(n2));
//                Matrix.save(nRWeights, DrawBoard4.weightLoc + "nR-weight.dat");
//                nR = Matrix.multiply(n2, nRWeights, n2[0].length);
//                printData(n0, n1, n2, nR);
//            }
//        });
//
//        // 0 for square 1 for circle
//        Future<?> task2 = executor.submit(() -> {
//            while (select == 0 && nR > nRB) { // Human: Circle,  AI: Square , Doesn't fire when it should, add
//
//                Future<?>[] n2Loop = new Future[n2[0].length];
//                for (int h = 0; h < n2B.length; h++) {
//                    final int fH = h;
//                    n2Loop[h] = executor.submit(() -> {
//                        while (n2[0][fH] > n2B[fH]) {
//                            Future<?>[] n1Loop = new Future[n1[0].length];
//                            for (int i = 0; i < n1B.length; i++) { // Correct every n1 weight
//                                final int fI = i;
//                                n1Loop[i] = executor.submit(() -> {
//                                    while (n1[0][fI] > n1B[fI]) { // doesn't fire when should
//                                        Future<?>[] n0Loop = new Future[n0[0].length];
//                                        for (int j = 0; j < n0B.length; j++) { // Correct every n0 weight, n0 will be correct before n1
//                                            final int fJ = j;
//                                            n0Loop[j] = executor.submit(() -> {
//                                                while (n0[0][fJ] > n0B[fJ]) { // doesn't fire when should
//                                                    n0Weights[fJ] = Matrix.subtract(n0Weights[fJ], Matrix.transpose(pixelMatrix));
//                                                    Matrix.save(n0Weights[fJ], DrawBoard4.weightLoc + "n0_" + fJ + "-weight.dat");
//                                                    n0[0][fJ] = Matrix.multiply(pixelMatrix, n0Weights[fJ], drawBoardSize * drawBoardSize);
//                                                    printData(n0, n1, n2, nR);
//                                                }
//                                            });
//                                        }
//                                        // Wait for all tasks in the first loop to finish
//                                        for (Future<?> future : n0Loop) {
//                                            try {
//                                                future.get(); // Wait for completion
//                                            } catch (Exception e) {throw new RuntimeException(e);}
//                                        }
//                                        n1Weights[fI] = Matrix.subtract(n1Weights[fI], Matrix.transpose(n0));
//                                        Matrix.save(n1Weights[fI], DrawBoard4.weightLoc + "n1_" + fI + "-weight.dat");
//                                        n1[0][fI] = Matrix.multiply(n0, n1Weights[fI], n0[0].length);
//                                        printData(n0, n1, n2, nR);
//                                    }
//                                });
//
//                            }
//                            // Wait for all tasks in the first loop to finish
//                            for (Future<?> future : n1Loop) {
//                                try {
//                                    future.get(); // Wait for completion
//                                } catch (Exception e) {throw new RuntimeException(e);}
//                            }
//                            n2Weights[fH] = Matrix.subtract(n2Weights[fH], Matrix.transpose(n1));
//                            Matrix.save(n2Weights[fH], DrawBoard4.weightLoc + "n2_" + fH + "-weight.dat");
//                            n2[0][fH] = Matrix.multiply(n1, n2Weights[fH], n1[0].length);
//                            printData(n0, n1, n2, nR);
//                        }
//                    });
//                }
//                // Wait for all tasks in the first loop to finish
//                for (Future<?> future : n2Loop) {
//                    try {
//                        future.get(); // Wait for completion
//                    } catch (Exception e) {throw new RuntimeException(e);}
//                }
//
//                nRWeights = Matrix.subtract(nRWeights, Matrix.transpose(n2));
//                Matrix.save(nRWeights, DrawBoard4.weightLoc + "nR-weight.dat");
//                nR = Matrix.multiply(n2, nRWeights, n2[0].length);
//                printData(n0, n1, n2, nR);
//            }
//        });
//
//        // Wait for both tasks (loops) to complete
//        try {
//            task1.get(); // Blocks until the first task finishes
//            task2.get(); // Blocks until the second task finishes
//        } catch (Exception e) {throw new RuntimeException(e);}
//
//        // Shutdown the executor service after both tasks are done
//        executor.shutdown();
//    }
//
//    public double neuronResult(double[][] pixelMatrix) {
//        // Create an executor service with a thread pool
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//        // First loop - parallel execution
//        Future<?>[] futuresFirstLoop = new Future[n0[0].length];
//
//        for (int i = 0; i < n0[0].length; i++) {
//            final int index = i; // effectively final for lambda usage
//            futuresFirstLoop[i] = executor.submit(() -> {
//                n0[0][index] = Matrix.multiply(pixelMatrix, n0Weights[index], drawBoardSize * drawBoardSize);
//            });
//        }
//
//        // Wait for all tasks in the first loop to finish
//        for (Future<?> future : futuresFirstLoop) {
//            try {
//                future.get(); // Wait for completion
//            } catch (Exception e) {
//                e.printStackTrace(); // Handle exceptions if necessary
//            }
//        }
//        // Second loop - parallel execution
//        Future<?>[] futuresSecondLoop = new Future[n1[0].length];
//
//        for (int i = 0; i < n1[0].length; i++) {
//            final int index = i; // effectively final for lambda usage
//            futuresSecondLoop[i] = executor.submit(() -> {
//                n1[0][index] = Matrix.multiply(n0, n1Weights[index], n0[0].length);
//            });
//        }
//
//        // Wait for all tasks in the second loop to finish
//        for (Future<?> future : futuresSecondLoop) {
//            try {
//                future.get(); // Wait for completion
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        // Third loop - parallel execution
//        Future<?>[] futuresThirdLoop = new Future[n2[0].length];
//
//        for (int i = 0; i < n2[0].length; i++) {
//            final int index = i; // effectively final for lambda usage
//            futuresThirdLoop[i] = executor.submit(() -> {
//                n2[0][index] = Matrix.multiply(n1, n2Weights[index], n1[0].length);
//            });
//        }
//
//        // Wait for all tasks in the third loop to finish
//        for (Future<?> future : futuresThirdLoop) {
//            try {
//                future.get(); // Wait for completion
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        executor.shutdown(); // Shut down the executor service
//
//        nR = Matrix.multiply(n1, nRWeights, n1[0].length);
//        printData(n0, n1, n2, nR);
//
//        return nR;
//    }

    // Forward pass through the network
    public double[][] forward(double[][] input) {
        // Calculate activations for each layer
        double[][] n0 = Matrix.multiply(input, n0Weights); //32x10
        n0Activation = Matrix.sigmoid(n0);
        double[][] n1 = Matrix.multiply(n0Activation, n1Weights); //32x5
        n1Activation = Matrix.sigmoid(n1);
        double[][] nr = Matrix.multiply(n1Activation, nRWeights); //32x1
        nRActivation = Matrix.sigmoid(nr);

        // Print activation data for debugging
//        printData(input, n0Activation, n1Activation, nrActivation);

        return nRActivation;  // Return final output
    }

    // Backpropagation to update weights
    public void backpropagate(double[][] input, double[][] target, double[][] output) {
        // Perform forward pass and get all activations

        double[][] nRError = Matrix.subtract(Matrix.transpose(target), output); //32x1
        double[][] nRDelta = Matrix.dot(nRError, Matrix.sigmoidDerivative(output)); //32x1

        double[][] n1Error = Matrix.multiply(nRDelta, Matrix.transpose(nRWeights)); //32x5
        double[][] n1Delta = Matrix.dot(n1Error, Matrix.sigmoidDerivative(n1Activation)); //32x5

        double[][] n0Error = Matrix.multiply(n1Delta, Matrix.transpose(n1Weights)); //32x10
        double[][] n0Delta = Matrix.dot(n0Error, Matrix.sigmoidDerivative(n0Activation)); //32x10

        n0DeltaWeights = Matrix.multiply(learningRate, Matrix.multiply(Matrix.transpose(input), n0Delta)); //900x10
        n1DeltaWeights = Matrix.multiply(learningRate, Matrix.multiply(Matrix.transpose(n0Activation), n1Delta)); //10x5
        nRDeltaWeights = Matrix.multiply(learningRate, Matrix.multiply(Matrix.transpose(n1Activation), nRDelta)); //5x1

    }

    // Binary cross-entropy loss function
    public double loss(double[][] target, double[][] prediction) {
        return Matrix.mean(Matrix.square(Matrix.subtract(target, Matrix.transpose(prediction))));

    }

    public static void main(String[] args) {
        AdvanceAI ai = new AdvanceAI();
        System.out.print(" " + ai.n0Weights.length + "x" + ai.n0Weights[0].length + " ");
    }

    private void printData(double[][] input, double[][] n0, double[][] n1, double[][] nR) {

        System.out.print("NR: ");
        System.out.print(nR[0][0]);
//        for (int i = 0; i < n1[0].length; i++) {
//            System.out.print(", N1-"+i+"-W: ");
//            System.out.print(Arrays.deepToString(n1Weights[i]));
//        }

//        for (int i = 0; i < n0[0].length; i++) {
//            System.out.print(", N0-" + i + ": ");
//            System.out.print(n0[0][i]);
//        }

//        for (int i = 0; i < n0B.length; i++) {
//            System.out.print(", N0-" + i + "-B: ");
//            System.out.print(n0B[i]);
//        }

//        for (int i = 0; i < n1[0].length; i++) {
//            System.out.print(", N1-"+i+": ");
//            System.out.print(n1[0][i]);
//        }

        System.out.print(" | ");
        System.out.print("NR Bias: ");
        System.out.print(" | ");
        System.out.println(nR[0][0] <= 0.5 ? "Square" : "Circle");
    }

    public void saveWeights() {
        n0Weights = Matrix.plus(n0Weights, Matrix.multiply((double) 1 / batchSize, n0DeltaWeights));
        n1Weights = Matrix.plus(n1Weights, Matrix.multiply((double) 1 / batchSize, n1DeltaWeights));
        nRWeights = Matrix.plus(nRWeights, Matrix.multiply((double) 1 / batchSize, nRDeltaWeights));
        Matrix.save(n0Weights, weightLoc + "n0-weight.dat");
        Matrix.save(n1Weights, weightLoc + "n1-weight.dat");
        Matrix.save(nRWeights, weightLoc + "nR-weight.dat");
    }

    // Method for Xavier Initialization with Uniform Distribution
    private double[][] xavierUniform(int nIn, int nOut) {
        Random random = new Random();
        double[][] weights = new double[nIn][nOut];
        double limit = Math.sqrt(6.0 / nIn); // Calculate the limit

        for (int i = 0; i < nIn; i++) {
            for (int j = 0; j < nOut; j++) {
                weights[i][j] = (random.nextDouble() * 2 * limit) - limit; // Sample from U(-limit, limit)
            }
        }
        return weights;
    }

    public void initWeight() {
        double[][] n0Weights = new double[drawBoardSize * drawBoardSize][n0Size];
        double[][] n1Weights = new double[n0Size][n1Size];
        double[][] nRWeights = new double[n1Size][nRSize];
        n0Weights = xavierUniform(n0Weights.length, n0Weights[0].length);
        Matrix.save(n0Weights, DrawBoard4.weightLoc + "n0-weight.dat");
        n1Weights = xavierUniform(n1Weights.length, n1Weights[0].length);
        Matrix.save(n1Weights, DrawBoard4.weightLoc + "n1-weight.dat");
        nRWeights = xavierUniform(nRWeights.length, nRWeights[0].length);
        Matrix.save(nRWeights, DrawBoard4.weightLoc + "nR-weight.dat");
        JOptionPane.showConfirmDialog(DrawBoard4.getInstance(), "The weight has been reset.", "Weight Reset", JOptionPane.DEFAULT_OPTION);
        System.out.println("Weight is being reset");
    }


    public void setupWeight() {
        // Create a JFrame to show progress
        JFrame frame = new JFrame("Loading Weights");
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        // Create a label to show status
        JLabel statusLabel = new JLabel("Initializing...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add components to the frame
        frame.setLayout(new BorderLayout());
        frame.add(statusLabel, BorderLayout.NORTH);
        frame.add(progressBar, BorderLayout.CENTER);
        frame.setSize(400, 90);
        frame.setLocationRelativeTo(null);  // Center the frame
        frame.setVisible(true);

        try {
            // Load n0-weight.dat
            statusLabel.setText("Loading n0 weights...");
            n0Weights = Matrix.readV2D(weightLoc + "n0-weight.dat");
            progressBar.setValue(25);

            // Load n1-weight.dat
            statusLabel.setText("Loading n1 weights...");
            n1Weights = Matrix.readV2D(weightLoc + "n1-weight.dat");
            progressBar.setValue(50);


            // Load nR-weight.dat
            statusLabel.setText("Loading nR weights...");
            nRWeights = Matrix.readV2D(weightLoc + "nR-weight.dat");
            progressBar.setValue(100);

            // Update status to done
            statusLabel.setText("Weights loaded successfully!");
        } catch (Exception e) {
            statusLabel.setText("Error loading weights!");
            int select = JOptionPane.showConfirmDialog(null, "Weight files not found.\nReset Weight?", "Reset Weight", JOptionPane.YES_NO_OPTION);
            if (select == 0) {
                initWeight();
                setupWeight();
            } else {
                throw new RuntimeException();
            }
        } finally {
            // Dispose the frame after a short delay
            Timer timer = new Timer(500, e -> frame.dispose());
            timer.setRepeats(false);  // Only execute once
            timer.start();
        }
    }


}
