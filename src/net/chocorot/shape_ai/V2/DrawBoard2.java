package net.chocorot.shape_ai.V2;

import net.chocorot.shape_ai.Matrix;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;


public class DrawBoard2 extends JFrame {
    private static DrawBoard2 instance;

    private int prevX, prevY;  // Previous mouse coordinates
    private int curX, curY;    // Current mouse coordinates
    private final DrawingPanel drawingPanel;

    public static int screenSize = 600;
    public static int drawBoardSize = 20;
    public static int n1B = 1;
    public static int n2B = -2;
    public static int n3B = 0;
    public static int n4B = -1;
    public static int nrB = 1;

    // Scale factor to scale 100x100 pixels to 600x600
    private final double scaleFactor = (double) screenSize / drawBoardSize;
    public long[][] pixelMatrix = new long[1][drawBoardSize * drawBoardSize];  // 1D array to store 100x100 pixel values (1D matrix)

    static AdvanceAI ai = new AdvanceAI();

    // Constructor to set up the window
    public DrawBoard2() {
        instance = this;
        setTitle("Draw Board 2");
        setSize(screenSize, screenSize);  // Set the window size to 600x600 pixels
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Initialize drawing panel
        drawingPanel = new DrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(screenSize, screenSize));  // Set size of drawing area to 600x600

        // Add drawing panel to the frame
        add(drawingPanel, BorderLayout.CENTER);

        // Add the menu bar
        setJMenuBar(createMenuBar());
        // Set up the key binding for Ctrl + V (Paste)
        setupKeyBindings();
    }

    // Create the menu bar with File, Edit, Help, and Action menus
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create 'File', 'Edit', 'Help', and 'Action' menus
        JMenu actionMenu = new JMenu("Action");
        JMenu testMenu = new JMenu("Test");


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

        // Add 'Calculate' option in 'Action' menu
        JMenuItem trainMenuItem = new JMenuItem("Train");
        trainMenuItem.addActionListener(e -> {
            // Get the current drawing as a BufferedImage
            BufferedImage image = drawingPanel.getImageAsBufferedImage();

            // Iterate through the 100x100 pixels of the drawing panel and store them in the 1D array
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
            ai.neuronTraining(pixelMatrix);
        });

        // Add 'Test' option in 'Action' menu
        JMenuItem testMenuItem = new JMenuItem("Test");
        testMenuItem.addActionListener(e -> {
            // Get the current drawing as a BufferedImage
            BufferedImage image = drawingPanel.getImageAsBufferedImage();

            // Iterate through the 100x100 pixels of the drawing panel and store them in the 1D array
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
            long result = ai.neuronResult(pixelMatrix);
            JOptionPane.showMessageDialog(this, "Is this a " + (result<=nrB ? "Square" : "Circle"), "Result", JOptionPane.INFORMATION_MESSAGE);
        });

        // Add 'pixel matrix' option in 'Test' menu
        JMenuItem pixelMenuItem = new JMenuItem("Pixel Matrix");
        pixelMenuItem.addActionListener(e -> {
            long[][] expandedPixelMatrix = new long[drawBoardSize][drawBoardSize];
            // Get the current drawing as a BufferedImage
            BufferedImage image = drawingPanel.getImageAsBufferedImage();

            // Iterate through the 100x100 pixels of the drawing panel
            for (int y = 0; y < drawBoardSize; y++) {
                for (int x = 0; x < drawBoardSize; x++) {
                    int rgb = image.getRGB(x, y);  // Get the pixel's color
                    if (isBlack(rgb)) {
                        expandedPixelMatrix[x][y] = 1;  // Black pixel
                    } else {
                        expandedPixelMatrix[x][y] = 0;  // White or other colors treated as white
                    }
                }
            }
            for (long[] i : expandedPixelMatrix) {
                System.out.println(Arrays.toString(i));
            }

        });

        // Add 'weight matrix' option in 'Test' menu
        JMenuItem weightMenuItem = new JMenuItem("Weight Matrix");
        weightMenuItem.addActionListener(e -> {
            showWeight();
        });

        actionMenu.add(testMenuItem);
        actionMenu.add(trainMenuItem);
        actionMenu.add(pasteMenuItem);
        actionMenu.add(resetMenuItem);

        testMenu.add(pixelMenuItem);
        testMenu.add(weightMenuItem);

        // Add menus to the menu bar
        menuBar.add(actionMenu);
        menuBar.add(testMenu);

        return menuBar;
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

    // Helper method to determine if a pixel is black
    private boolean isBlack(int rgb) {
        Color color = new Color(rgb);
        return color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0;  // Check if RGB values are black
    }

    private void showWeight() {
        System.out.println("Result Neurone");
        for (long[] i : reshapeMatrix(Matrix.readV2("res/V2/nr-weight.dat"), 4, 1)) {
            System.out.println(Arrays.toString(i));
        }
        System.out.println("\n\nFirst Hidden Neurone");
        for (long[] i : reshapeMatrix(Matrix.readV2("res/V2/n1-weight.dat"), drawBoardSize, drawBoardSize)) {
            System.out.println(Arrays.toString(i));
        }
        System.out.println("\n\nSecond Hidden Neurone");
        for (long[] i : reshapeMatrix(Matrix.readV2("res/V2/n2-weight.dat"), drawBoardSize, drawBoardSize)) {
            System.out.println(Arrays.toString(i));
        }
        System.out.println("\n\nThird Hidden Neurone");
        for (long[] i : reshapeMatrix(Matrix.readV2("res/V2/n3-weight.dat"), drawBoardSize, drawBoardSize)) {
            System.out.println(Arrays.toString(i));
        }
        System.out.println("\n\nFourth Hidden Neurone");
        for (long[] i : reshapeMatrix(Matrix.readV2("res/V2/n4-weight.dat"), drawBoardSize, drawBoardSize)) {
            System.out.println(Arrays.toString(i));
        }

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
                // Get the current drawing as a BufferedImage
                BufferedImage image = drawingPanel.getImageAsBufferedImage();

                // Iterate through the 100x100 pixels of the drawing panel and store them in the 1D array
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
                ai.neuronTraining(pixelMatrix);  // Trigger calculateMatrix when Ctrl + S is pressed
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
                // Get the current drawing as a BufferedImage
                BufferedImage image = drawingPanel.getImageAsBufferedImage();

                // Iterate through the 100x100 pixels of the drawing panel and store them in the 1D array
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
                long result = ai.neuronResult(pixelMatrix);
                JOptionPane.showMessageDialog(DrawBoard2.getInstance(), "Is this a " + (result<=nrB ? "Square" : "Circle"), "Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // Main class to run the app
    public static void main(String[] args) throws IOException {
//        ai.initWeight();
        SwingUtilities.invokeLater(() -> {
            DrawBoard2 app = new DrawBoard2();
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
                }
            });

            // Mouse motion listener for dragging
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    curX = (int) (e.getX() / scaleFactor);  // Scale mouse coordinates down
                    curY = (int) (e.getY() / scaleFactor);

                    if (g2d != null) {
                        g2d.drawLine(prevX, prevY, curX, curY);  // Draw the line
                        repaint();  // Redraw the panel
                        prevX = curX;
                        prevY = curY;
                    }
                }
            });
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

    public static long[][] reshapeMatrix(long[][] matrix1D, int rows, int cols) {
        // Check if reshape is possible
        if (matrix1D.length != rows * cols) {
            throw new IllegalArgumentException("Reshape is not possible with the given dimensions.");
        }

        // Create a new matrix of the desired shape
        long[][] reshapedMatrix = new long[rows][cols];

        // Fill the reshaped matrix with elements from the original 1D matrix
        for (int i = 0; i < matrix1D.length; i++) {
            reshapedMatrix[i / cols][i % cols] = matrix1D[i][0];
        }

        return reshapedMatrix;
    }

    public static DrawBoard2 getInstance() {
        return instance;
    }

}
