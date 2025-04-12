import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
/**
 * A simple drawing tool application using Java Swing
 * With shape drawing capabilities and save functionality
 */
public class DrawingTool extends JFrame {
    private ArrayList<Point> points = new ArrayList<>();
    private List<Shape> shapes = new ArrayList<>();
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    
    // Drawing modes
    private static final int PENCIL_MODE = 0;
    private static final int RECTANGLE_MODE = 1;
    private static final int OVAL_MODE = 2;
    private static final int LINE_MODE = 3;
    
    private int currentMode = PENCIL_MODE;
    
    // For shape drawing
    private int startX, startY;
    private Shape currentShape;
    
    public DrawingTool() {
  setTitle("Drawing Tool with Shapes and Save");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create drawing panel
        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Draw points (pencil strokes)
                for (int i = 0; i < points.size() - 1; i++) {
                    Point p1 = points.get(i);
                    Point p2 = points.get(i + 1);
                    
                    g2d.setColor(p1.color);
                    g2d.setStroke(new BasicStroke(p1.size));
                    
                    if (p1.connected) {
                        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                    } else {
                        g2d.fillOval(p1.x - p1.size/2, p1.y - p1.size/2, p1.size, p1.size);
                    }
                }
                
                // Draw the last point
                if (!points.isEmpty()) {
                    Point last = points.get(points.size() - 1);
                    g2d.setColor(last.color);
                    g2d.fillOval(last.x - last.size/2, last.y - last.size/2, last.size, last.size);
                }
                
                // Draw all shapes
                for (Shape shape : shapes) {
                    g2d.setColor(shape.color);
                    g2d.setStroke(new BasicStroke(shape.lineWidth));
                    
                    if (shape.type == RECTANGLE_MODE) {
                        if (shape.filled) {
                            g2d.fillRect(shape.x, shape.y, shape.width, shape.height);
                        } else {
                            g2d.drawRect(shape.x, shape.y, shape.width, shape.height);
                        }
                    } else if (shape.type == OVAL_MODE) {
                        if (shape.filled) {
                            g2d.fillOval(shape.x, shape.y, shape.width, shape.height);
                        } else {
                            g2d.drawOval(shape.x, shape.y, shape.width, shape.height);
                        }
                    } else if (shape.type == LINE_MODE) {
                        g2d.drawLine(shape.x, shape.y, shape.x + shape.width, shape.y + shape.height);
                    }
                }
                
                // Draw current shape being drawn
                if (currentShape != null) {
                    g2d.setColor(currentShape.color);
                    g2d.setStroke(new BasicStroke(currentShape.lineWidth));
                    
                    if (currentShape.type == RECTANGLE_MODE) {
                        if (currentShape.filled) {
                            g2d.fillRect(currentShape.x, currentShape.y, currentShape.width, currentShape.height);
                        } else {
                            g2d.drawRect(currentShape.x, currentShape.y, currentShape.width, currentShape.height);
                        }
                    } else if (currentShape.type == OVAL_MODE) {
                        if (currentShape.filled) {
                            g2d.fillOval(currentShape.x, currentShape.y, currentShape.width, currentShape.height);
                        } else {
                            g2d.drawOval(currentShape.x, currentShape.y, currentShape.width, currentShape.height);
                        }
                    } else if (currentShape.type == LINE_MODE) {
                        g2d.drawLine(currentShape.x, currentShape.y, 
                                     currentShape.x + currentShape.width, 
                                     currentShape.y + currentShape.height);
                    }
                }
            }
        };
        
        drawingPanel.setBackground(Color.WHITE);
        
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentMode == PENCIL_MODE) {
                    points.add(new Point(e.getX(), e.getY(), currentColor, brushSize, false));
                } else {
                    // For shapes, record the starting point
                    startX = e.getX();
                    startY = e.getY();
                    
                    // Initialize a shape that will be updated during drag
                    currentShape = new Shape(
                        startX, startY, 0, 0, 
                        currentColor, brushSize, currentMode, false
                    );
                }
                drawingPanel.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentMode != PENCIL_MODE && currentShape != null) {
                    // Add the final shape to our list
                    shapes.add(currentShape);
                    currentShape = null;
                    drawingPanel.repaint();
                }
            }
        });
        
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentMode == PENCIL_MODE) {
                    points.add(new Point(e.getX(), e.getY(), currentColor, brushSize, true));
                } else if (currentShape != null) {
                    // Update the shape dimensions based on drag
                    int x = Math.min(startX, e.getX());
                    int y = Math.min(startY, e.getY());
                    int width = Math.abs(startX - e.getX());
                    int height = Math.abs(startY - e.getY());
                    
                    if (currentMode == LINE_MODE) {
                        // For lines, we keep the original starting point
                        currentShape.x = startX;
                        currentShape.y = startY;
                        currentShape.width = e.getX() - startX;
                        currentShape.height = e.getY() - startY;
                    } else {
                        currentShape.x = x;
                        currentShape.y = y;
                        currentShape.width = width;
                        currentShape.height = height;
                    }
                }
                drawingPanel.repaint();
            }
        });
        
        // Create control panel
        JPanel controlPanel = new JPanel();
        
        // Color buttons
        JButton blackBtn = new JButton("Black");
        blackBtn.addActionListener(e -> currentColor = Color.BLACK);
        
        JButton redBtn = new JButton("Red");
        redBtn.addActionListener(e -> currentColor = Color.RED);
        
        JButton blueBtn = new JButton("Blue");
        blueBtn.addActionListener(e -> currentColor = Color.BLUE);
        
        JButton greenBtn = new JButton("Green");
        greenBtn.addActionListener(e -> currentColor = Color.GREEN);
        
        // Clear button
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> {
            points.clear();
            shapes.clear();
            currentShape = null;
            drawingPanel.repaint();
        });
        
        // Save button
        JButton saveBtn = new JButton("Save Drawing");
        saveBtn.addActionListener(e -> saveDrawing(drawingPanel));
        
        // Brush size control
        JLabel sizeLabel = new JLabel("Brush Size: " + brushSize);
        JSlider sizeSlider = new JSlider(1, 20, brushSize);
        sizeSlider.addChangeListener(e -> {
            brushSize = sizeSlider.getValue();
            sizeLabel.setText("Brush Size: " + brushSize);
        });
        
        // Drawing tool buttons
        JPanel toolPanel = new JPanel();
        toolPanel.setBorder(BorderFactory.createTitledBorder("Drawing Tools"));
        
        JToggleButton pencilBtn = new JToggleButton("Pencil");
        pencilBtn.setSelected(true);
        pencilBtn.addActionListener(e -> currentMode = PENCIL_MODE);
        
        JToggleButton rectangleBtn = new JToggleButton("Rectangle");
        rectangleBtn.addActionListener(e -> currentMode = RECTANGLE_MODE);
        
        JToggleButton ovalBtn = new JToggleButton("Oval");
        ovalBtn.addActionListener(e -> currentMode = OVAL_MODE);
        
        JToggleButton lineBtn = new JToggleButton("Line");
        lineBtn.addActionListener(e -> currentMode = LINE_MODE);
        
        // Create a button group so only one tool can be selected at a time
        ButtonGroup toolGroup = new ButtonGroup();
        toolGroup.add(pencilBtn);
        toolGroup.add(rectangleBtn);
        toolGroup.add(ovalBtn);
        toolGroup.add(lineBtn);
        
        toolPanel.add(pencilBtn);
        toolPanel.add(rectangleBtn);
        toolPanel.add(ovalBtn);
        toolPanel.add(lineBtn);
        
        // Fill checkbox
        JCheckBox fillShapesCheck = new JCheckBox("Fill Shapes");
        fillShapesCheck.addActionListener(e -> {
            if (currentShape != null) {
                currentShape.filled = fillShapesCheck.isSelected();
            }
        });
        toolPanel.add(fillShapesCheck);
        
// Add controls to panel
controlPanel.setLayout(new BorderLayout());

JPanel colorPanel = new JPanel();
colorPanel.add(blackBtn);
colorPanel.add(redBtn);
colorPanel.add(blueBtn);
colorPanel.add(greenBtn);

JPanel actionPanel = new JPanel();
actionPanel.add(clearBtn);
actionPanel.add(saveBtn); // Add the save button from the main branch
actionPanel.add(sizeLabel);
actionPanel.add(sizeSlider);

controlPanel.add(toolPanel, BorderLayout.NORTH);
controlPanel.add(colorPanel, BorderLayout.CENTER);
controlPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Set up the main layout
        setLayout(new BorderLayout());
        add(drawingPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Saves the current drawing as a PNG image file
     * 
     * @param panel The panel containing the drawing to save
     */
    private void saveDrawing(JPanel panel) {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Fill with white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
        
        // Paint the panel content to the image
        panel.paint(g2d);
        g2d.dispose();
        
        // Create file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Drawing");
        
        // Set default filename and filter
        fileChooser.setSelectedFile(new File("drawing.png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Add .png extension if not present
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(this, 
                    "Drawing saved successfully to: " + file.getAbsolutePath(),
                    "Save Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving image: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Custom Point class that includes color and connection information
    private static class Point {
        int x, y;
        Color color;
        int size;
        boolean connected;
        
        Point(int x, int y, Color color, int size, boolean connected) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
            this.connected = connected;
        }
    }
    
    // Shape class for rectangles, ovals, and lines
    private static class Shape {
        int x, y, width, height;
        Color color;
        int lineWidth;
        int type;  // RECTANGLE_MODE, OVAL_MODE, or LINE_MODE
        boolean filled;
        
        Shape(int x, int y, int width, int height, Color color, int lineWidth, int type, boolean filled) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.lineWidth = lineWidth;
            this.type = type;
            this.filled = filled;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DrawingTool app = new DrawingTool();
            app.setVisible(true);
        });
    }
}