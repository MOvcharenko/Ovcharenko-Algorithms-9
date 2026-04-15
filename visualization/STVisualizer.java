package visualization;

import datastructure.ST;
import java.awt.*;
import javax.swing.*;

public class STVisualizer extends JPanel {
    private final ST<Integer, String> st;
    private final int nodeRadius = 20;
    private final int levelHeight = 60;

    public STVisualizer() {
        this.st = new ST<>();
        setBackground(Color.WHITE);
    }

    public void put(Integer key, String val) {
        if (val == null || val.isEmpty()) {
            st.delete(key);
        } else {
            st.put(key, val);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (st.isEmpty()) return;
        drawNode(g, st.getRoot(), getWidth() / 2, 30, getWidth() / 4);
    }

    private void drawNode(Graphics g, ST<Integer, String>.Node node, int x, int y, int xOffset) {
        if (node == null) return;

        // Draw left child
        if (node.getLeft() != null) {
            int childX = x - xOffset;
            int childY = y + levelHeight;
            g.setColor(Color.GRAY);
            g.drawLine(x, y + nodeRadius, childX, childY - nodeRadius);
            drawNode(g, node.getLeft(), childX, childY, xOffset / 2);
        }

        // Draw right child
        if (node.getRight() != null) {
            int childX = x + xOffset;
            int childY = y + levelHeight;
            g.setColor(Color.GRAY);
            g.drawLine(x, y + nodeRadius, childX, childY - nodeRadius);
            drawNode(g, node.getRight(), childX, childY, xOffset / 2);
        }

        // Draw current node
        g.setColor(Color.CYAN);
        g.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
        g.setColor(Color.BLACK);
        g.drawOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

        // Display key and value
        String text = node.getKey() + ":" + node.getVal();
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g.drawString(text, x - textWidth / 2, y + textHeight / 4);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("BST Symbol Table Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        STVisualizer visualizer = new STVisualizer();
        frame.add(visualizer, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JTextField keyField = new JTextField(5);
        JTextField valField = new JTextField(5);
        JButton putButton = new JButton("Put");
        JButton deleteButton = new JButton("Delete");

        controlPanel.add(new JLabel("Key:"));
        controlPanel.add(keyField);
        controlPanel.add(new JLabel("Value:"));
        controlPanel.add(valField);
        controlPanel.add(putButton);
        controlPanel.add(deleteButton);

        frame.add(controlPanel, BorderLayout.SOUTH);

        putButton.addActionListener(e -> {
            try {
                int key = Integer.parseInt(keyField.getText());
                String val = valField.getText();
                visualizer.put(key, val);
                keyField.setText("");
                valField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Key must be an integer.");
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                int key = Integer.parseInt(keyField.getText());
                visualizer.put(key, null); // Deleting by putting null
                keyField.setText("");
                valField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Key must be an integer.");
            }
        });

        frame.setVisible(true);
    }
}