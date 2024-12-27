package net.chocorot.shape_ai.V4;

import net.chocorot.shape_ai.Matrix;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DrawBoard4 extends JFrame {
    private static DrawBoard4 instance;

    private int prevX, prevY;  // Previous mouse coordinates
    private int curX, curY;    // Current mouse coordinates
    private final DrawingPanel drawingPanel;

    public static int screenSize = 800;
    public static int drawBoardSize = 100;
    public static int batchSize = 440;
    double threshold = 0.5;
    public int brushSize = 5;
    public static String weightLoc = "res/V4/";

    // Scale factor to scale 100x100 pixels to 600x600
    private final double scaleFactor = (double) screenSize / drawBoardSize;
    public double[][] pixelMatrix = new double[1][drawBoardSize * drawBoardSize];  // 1D array to store 100x100 pixel values (1D matrix)

    static AdvanceAI ai = new AdvanceAI();

    JDialog resultDialog = new JDialog(this, "Auto Train Progress", false);  // true for modal
    JLabel resultLabel = new JLabel("Result:\t0", SwingConstants.CENTER);
    JLabel resultHighestText = new JLabel("Highest:\t0", SwingConstants.CENTER);
    JLabel resultLowestText = new JLabel("Lowest:\t0", SwingConstants.CENTER);

    double resultHighest = 0.5;
    double resultLowest = 0.5;

    // Constructor to set up the window
    public DrawBoard4() {
        instance = this;
        setTitle("Draw Board 4");
        setSize(screenSize, screenSize);  // Set the window size to 600x600 pixels
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
//        setResizable(false);

        // Initialize drawing panel
        drawingPanel = new DrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(screenSize, screenSize));  // Set size of drawing area to 600x600

        // Add drawing panel to the frame
        add(drawingPanel, BorderLayout.CENTER);

        // Add the menu bar
        setJMenuBar(createMenuBar());

        // Set up the result dialog
        resultDialog.setSize(300, 120);
        resultDialog.setLayout(new GridLayout(3, 1));

        resultDialog.add(resultLabel);
        resultDialog.add(resultHighestText);
        resultDialog.add(resultLowestText);
        resultDialog.setVisible(true);

        // Set up the key binding for Ctrl + V (Paste)
        setupKeyBindings();
    }

    // Create the menu bar with File, Edit, Help, and Action menus
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create 'Action' and 'Data' menus
        JMenu actionMenu = new JMenu("Action");
        JMenu dataMenu = new JMenu("Data");
        JMenu windowMenu = new JMenu("Window");

        // Add 'Reset' option in 'Action' menu
        JMenuItem resetMenuItem = new JMenuItem("Reset");
        resetMenuItem.addActionListener(e -> drawingPanel.clear());  // Reset the drawing panel

        // Add 'Paste' option in 'Edit' menu
        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.addActionListener(e -> {
            try {
                pasteImageFromClipboard();
            } catch (IOException | UnsupportedFlavorException ex) {
                throw new RuntimeException(ex);
            }
        });  // Paste image from clipboard

        // Add 'Train' option in 'Action' menu
        JMenuItem trainMenuItem = new JMenuItem("Train");
        trainMenuItem.addActionListener(e -> {
            trainAction();
        });

        // Add 'Auto Train' option in 'Action' menu
        JMenuItem autoTrainMenuItem = new JMenuItem("Auto Train");
        autoTrainMenuItem.addActionListener(e -> {
            autoTrain();
        });

        // Add 'Test' option in 'Action' menu
        JMenuItem testMenuItem = new JMenuItem("Test");
        testMenuItem.addActionListener(e -> {
            testAction();
        });

        // Add 'Reset Weight' option in 'Data' menu
        JMenuItem resetWeightMenuItem = new JMenuItem("Reset Weight");
        resetWeightMenuItem.addActionListener(e -> {
            int select = JOptionPane.showConfirmDialog(DrawBoard4.getInstance(),
                    "Reset Weight?", "Reset weight",
                    JOptionPane.YES_NO_OPTION);
            if (select == JOptionPane.YES_OPTION) {
                ai.initWeight();
                ai.setupWeight();
            }
        });


        // Add 'Reset ' option in 'Data' menu
//        JMenuItem resetAllMenuItem = new JMenuItem("Reset All");
//        resetAllMenuItem.addActionListener(e -> {
//            int select = JOptionPane.showConfirmDialog(DrawBoard4.getInstance(),
//                    "Reset All?", "Reset All",
//                    JOptionPane.YES_NO_OPTION);
//            if (select == JOptionPane.YES_OPTION) {
//                ai.initWeight();
//                ai.setupWeight();
//            }
//        });

        JMenuItem saveAndQuitMenuItem = new JMenuItem("Save and Exit");
        saveAndQuitMenuItem.addActionListener(e -> {
            int select = JOptionPane.showConfirmDialog(DrawBoard4.getInstance(),
                    "Exit Program?", "Save and Exit",
                    JOptionPane.YES_NO_OPTION);
            if (select == JOptionPane.YES_OPTION) {
                ai.saveWeights();
                this.dispose();
            }
        });

        actionMenu.add(testMenuItem);
        actionMenu.add(trainMenuItem);
        actionMenu.add(autoTrainMenuItem);
        actionMenu.add(pasteMenuItem);
        actionMenu.add(resetMenuItem);
        dataMenu.add(resetWeightMenuItem);
//        dataMenu.add(resetBiasMenuItem);
//        dataMenu.add(resetAllMenuItem);
        windowMenu.add(saveAndQuitMenuItem);

        // Add menus to the menu bar
        menuBar.add(actionMenu);
        menuBar.add(dataMenu);
        menuBar.add(windowMenu);

        return menuBar;
    }

    // Set up key bindings to trigger Ctrl + V for pasting
    private void setupKeyBindings() {
        // Get the InputMap and ActionMap from the drawing panel's component
        InputMap inputMap = drawingPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = drawingPanel.getActionMap();

        // Bind Ctrl + V to a paste action
        KeyStroke pasteKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(pasteKeyStroke, "paste");
        KeyStroke pasteKeyStroke2 = KeyStroke.getKeyStroke(KeyEvent.VK_V, 0);
        inputMap.put(pasteKeyStroke, "paste");
        inputMap.put(pasteKeyStroke2, "paste");

        // Define the action to be performed on "paste"
        actionMap.put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    pasteImageFromClipboard();  // Trigger paste when Ctrl + V is pressed
                } catch (IOException | UnsupportedFlavorException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Bind Ctrl + S to a calculate action
        KeyStroke calculateKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(calculateKeyStroke, "calculate");
        KeyStroke calculateKeyStroke2 = KeyStroke.getKeyStroke(KeyEvent.VK_W, 0);
        inputMap.put(calculateKeyStroke2, "calculate");

        // Define the action to be performed on "calculate"
        actionMap.put("calculate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trainAction();
            }
        });

        // Bind Ctrl + R to a calculate action
        KeyStroke resetKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK);
        KeyStroke resetKeyStroke2 = KeyStroke.getKeyStroke(KeyEvent.VK_R, 0);
        inputMap.put(resetKeyStroke, "reset");
        inputMap.put(resetKeyStroke2, "reset");

        // Define the action to be performed on "reset"
        actionMap.put("reset", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawingPanel.clear();
            }
        });

        // Bind Ctrl + T to a calculate action
        KeyStroke testKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(testKeyStroke, "test");
        KeyStroke testKeyStroke2 = KeyStroke.getKeyStroke(KeyEvent.VK_T, 0);
        inputMap.put(testKeyStroke2, "test");

        // Define the action to be performed on "test"
        actionMap.put("test", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testAction();
            }
        });
    }

    // Main class to run the app
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DrawBoard4 app = new DrawBoard4();
            app.setVisible(true);
        });
    }

    // Custom JPanel for drawing
    class DrawingPanel extends JPanel {
        private Image image;  // To store the drawing
        private Graphics2D g2d;  // To perform the drawing

        public DrawingPanel() {
            setDoubleBuffered(false);

            // Mouse listener for clicking and pressing
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    prevX = (int) (e.getX() / scaleFactor);  // Scale mouse coordinates down
                    prevY = (int) (e.getY() / scaleFactor);
                    drawCircle(prevX, prevY);
                }
            });

            // Mouse motion listener for dragging
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    curX = (int) (e.getX() / scaleFactor);  // Scale mouse coordinates down
                    curY = (int) (e.getY() / scaleFactor);

                    if (g2d != null) {
                        drawCircle(curX, curY);  // Draw the line
                        repaint();  // Redraw the panel
                        prevX = curX;
                        prevY = curY;
                    }
                }
            });
        }

        // Method to draw a filled circle (brush effect) at the given coordinates
        private void drawCircle(int x, int y) {
            if (g2d != null) {
                g2d.fillOval(x - brushSize / 2, y - brushSize / 2, brushSize, brushSize);  // Draw a circle with center (x, y)
            }
        }

        // Initialize the drawing area
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) {
                // Create the 100x100 drawing area
                image = createImage(drawBoardSize, drawBoardSize);
                g2d = (Graphics2D) image.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2d.setPaint(Color.BLACK);  // Default color
                clear();  // Clear the drawing area
            }

            // Scale the 100x100 drawing to fit the 600x600 panel
            Graphics2D g2 = (Graphics2D) g;
            g2.scale(scaleFactor, scaleFactor);  // Scale up the drawing by 6x
            g2.drawImage(image, 0, 0, this);
        }

        // Clears the drawing area
        public void clear() {
            g2d.setPaint(Color.WHITE);  // Background color
            g2d.fillRect(0, 0, getSize().width, getSize().height);
            g2d.setPaint(Color.BLACK);  // Foreground color
            repaint();
        }

        // Method to paste an image into the drawing panel
        public void pasteImage(Image clipboardImage) {
            // Scale the clipboard image to fit the 100x100 drawing area
            Image scaledImage = clipboardImage.getScaledInstance(drawBoardSize, drawBoardSize, Image.SCALE_SMOOTH);

            // Draw the scaled image onto the drawing panel
            g2d.drawImage(scaledImage, 0, 0, this);
            repaint();  // Repaint the panel to show the pasted image
        }

        // Get the current drawing as a BufferedImage (for calculating the matrix)
        public BufferedImage getImageAsBufferedImage() {
            BufferedImage bufferedImage = new BufferedImage(drawBoardSize, drawBoardSize, BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferedImage.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            return bufferedImage;
        }
    }

    // Method to paste an image from the clipboard
    private void pasteImageFromClipboard() throws IOException, UnsupportedFlavorException {
        drawingPanel.clear();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            // Get the image from the clipboard
            Image clipboardImage = (Image) transferable.getTransferData(DataFlavor.imageFlavor);

            // Paste the image into the drawing panel
            drawingPanel.pasteImage(clipboardImage);
        } else {
            JOptionPane.showMessageDialog(this, "No image found in the clipboard.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testAction() {
        renderImage();

        double[][] result = ai.forward(pixelMatrix);
        SwingUtilities.invokeLater(() -> resultLabel.setText("Result:      " + new DecimalFormat("0.00000000000000000").format(result[0][0])));
        int prediction = (result[0][0] >= threshold) ? 1 : 0;
        JOptionPane.showMessageDialog(DrawBoard4.getInstance(), "Is this a " + (prediction == 0 ? "Square" : "Circle"), "Result", JOptionPane.INFORMATION_MESSAGE);
    }

    private void trainAction() {
        renderImage();

        // Square = 0, Circle = 1
        String[] options = new String[]{"Square", "Circle", "Cancel"};
        int select = JOptionPane.showOptionDialog(DrawBoard4.getInstance(), "Select the correct shape", "What shape is this? ", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (select == 0 || select == 1) {
            double[][] result = ai.forward(pixelMatrix);
            int prediction = (result[0][0] >= threshold) ? 1 : 0;
            while (prediction != select) {
//                ai.backpropagate(pixelMatrix, select);
                ai.saveWeights();
                result = ai.forward(pixelMatrix);
                System.out.println(result[0][0]);
                prediction = (result[0][0] >= threshold) ? 1 : 0;
            }
        }
    }

    volatile boolean training;

    private void autoTrain() {
        Image[] squareImages = loadImagesFromFolder(weightLoc + "example/square");
        Image[] circleImages = loadImagesFromFolder(weightLoc + "example/circle");

        training = true;

        // Set up the progress dialog
        JDialog dialog = new JDialog(this, "Auto Train Progress", false);  // true for modal
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());

        // Status label to show training progress
        JLabel statusLabel = new JLabel("Training is in progress...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(statusLabel, BorderLayout.CENTER);


        // Stop button to halt training
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton stopButton = new JButton("Stop");
        stopButton.setPreferredSize(new Dimension(80, 30));
        stopButton.addActionListener(e -> {
            training = false;
            dialog.dispose();
        });

        buttonPanel.add(stopButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);


        new Thread(() -> {
            int epochs = 1000;
            double totalLoss;

            for (int epoch = 0; epoch < epochs && training; epoch++) {
                totalLoss = 0.0;

                // Prepare combined dataset and labels
                List<Image> combinedImages = new ArrayList<>();
                List<Float> labels = new ArrayList<>();
                for (Image squareImage : squareImages) {
                    combinedImages.add(squareImage);
                    labels.add(0f);
                }
                for (Image circleImage : circleImages) {
                    combinedImages.add(circleImage);
                    labels.add(1f);
                }

                // Shuffle the dataset
                Collections.shuffle(combinedImages);
                // Process each mini-batch
                for (int batchStart = 0; batchStart < combinedImages.size(); batchStart += batchSize) {
                    int currentBatchSize = Math.min(batchSize, combinedImages.size() - batchStart);
                    double batchLoss = 0.0;
                    double[][] inputs = new double[batchSize][drawBoardSize * drawBoardSize];
                    double[][] inputLabels = new double[1][batchSize];

                    // Forward pass and accumulate gradients for the batch
                    if (!training)
                        return;
                    for (int i = 0; i < currentBatchSize; i++) {
                        int dataIndex = batchStart + i;

                        // Display the image and forward pass
                        drawingPanel.clear();
                        drawingPanel.pasteImage(combinedImages.get(dataIndex).getScaledInstance(drawBoardSize, drawBoardSize, Image.SCALE_AREA_AVERAGING));
                        renderImage();

                        double label = labels.get(dataIndex);
                        inputLabels[0][i] = label;
                        inputs[i] = pixelMatrix[0];

                    }
                    double[][] result = ai.forward(inputs);
                    // Accumulate gradients for each example in the batch
                    ai.backpropagate(inputs, inputLabels, result);
                    double loss = ai.loss(inputLabels, result);
                    SwingUtilities.invokeLater(() -> resultLabel.setText        ("Result:    " + new DecimalFormat("0.00000000000000000").format(Matrix.mean(result))));
                    SwingUtilities.invokeLater(() -> resultHighestText.setText  ("Highest:  " + new DecimalFormat("0.00000000000000000").format(resultHighest)));
                    SwingUtilities.invokeLater(() -> resultLowestText.setText   ("Lowest:   " + new DecimalFormat("0.00000000000000000").format(resultLowest)));
                    batchLoss += loss;

                    // Add batch loss to the total epoch loss
                    totalLoss += batchLoss;

                    // Optional: Clear the drawing panel between steps
                    drawingPanel.clear();
                    ai.saveWeights();
                }

                // Update UI with the current epoch and loss
                String statusText = "Epoch: " + epoch + " | Total Loss: " + new DecimalFormat("0.000000").format(totalLoss);
                SwingUtilities.invokeLater(() -> statusLabel.setText(statusText));

                // Early stopping condition based on loss
                if (totalLoss < 0.001) {
                    System.out.println("Training converged at Epoch " + epoch + ", stopping early.");
                    break;
                }
                // Optional sleep to simulate processing
//                try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

            }

            // Finish up and close dialog
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Training complete!");
                dialog.dispose();
            });

        }).start();

        ai.saveWeights();
    }


    // Method to load all images from a folder
    public static Image[] loadImagesFromFolder(String folderPath) {
        File folder = new File(folderPath);

        // Check if the folder exists and is a directory
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path.");
            return null;
        }

        // List to hold the images
        ArrayList<Image> imageList = new ArrayList<>();

        // Get all files in the folder
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                // Check if the file is an image (based on extension)
                if (isImageFile(file)) {
                    try {
                        // Read the image as BufferedImage, then cast to Image
                        BufferedImage bufferedImage = ImageIO.read(file);
                        if (bufferedImage != null) {
                            imageList.add(bufferedImage);
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading image: " + file.getName());
                    }
                }
            }
        }

        // Convert the list to an array of Image
        return imageList.toArray(new Image[0]);
    }

    public void renderImage() {
        // Get the current drawing as a BufferedImage
        BufferedImage image = drawingPanel.getImageAsBufferedImage();
        int index = 0;
        for (int y = 0; y < drawBoardSize; y++) {
            for (int x = 0; x < drawBoardSize; x++) {
                int rgb = image.getRGB(x, y);  // Get the pixel's color
                if (isBlack(rgb)) {
                    pixelMatrix[0][index] = 1;  // Black pixel
                } else {
                    pixelMatrix[0][index] = 0;  // White or other colors treated as white
                }
                index++;
            }
        }
    }

    // Helper method to determine if a pixel is black
    private boolean isBlack(int rgb) {
        Color color = new Color(rgb);
        return color.getRed() < 255 || color.getGreen() < 255 || color.getBlue() < 255;  // Check if RGB values are black
    }
    // Helper method to check if a file is an image
    private static boolean isImageFile(File file) {
        // Check the file extension (case insensitive)
        String[] imageExtensions = { "jpg", "jpeg", "png", "bmp", "gif" };
        String fileName = file.getName().toLowerCase();

        for (String extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    public static DrawBoard4 getInstance() {
        return instance;
    }

}
