import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;

/**
 * A simple drawing tool application using Java Swing
 * With save functionality
 */
public class DrawingTool extends JFrame {
    private ArrayList<Point> points = new ArrayList<>();
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    
    public DrawingTool() {
        setTitle("Drawing Tool with Save");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create drawing panel
        JPanel drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
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
            }
        };
        
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY(), currentColor, brushSize, false));
                drawingPanel.repaint();
            }
        });
        
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY(), currentColor, brushSize, true));
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
        
        // Add controls to panel
        controlPanel.add(blackBtn);
        controlPanel.add(redBtn);
        controlPanel.add(blueBtn);
        controlPanel.add(greenBtn);
        controlPanel.add(clearBtn);
        controlPanel.add(saveBtn);
        controlPanel.add(sizeLabel);
        controlPanel.add(sizeSlider);
        
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DrawingTool app = new DrawingTool();
            app.setVisible(true);
        });
    }
}