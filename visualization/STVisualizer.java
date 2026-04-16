package visualization;

import datastructure.ST;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;

public class STVisualizer extends JPanel {
    private final ST<Integer, String> st;
    private final Queue<Operation> operationQueue = new LinkedList<>();
    private final Timer animationTimer;
    private final int nodeRadius = 20;
    private final int levelHeight = 60;
    private final int stepDelay = 900;

    private static class Operation {
        private final Integer key;
        private final String value;

        private Operation(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public STVisualizer() {
        this.st = new ST<>();
        setBackground(Color.WHITE);
        initializeSampleTree();
        animationTimer = new Timer(stepDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processNextOperation();
            }
        });
    }

    private void initializeSampleTree() {
        st.put(50, "M");
        st.put(30, "C");
        st.put(70, "R");
        st.put(20, "B");
        st.put(40, "E");
        st.put(60, "P");
        st.put(80, "Z");
        repaint();
    }

    public void enqueuePut(Integer key, String val) {
        operationQueue.add(new Operation(key, val));
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    public void enqueueDelete(Integer key) {
        enqueuePut(key, null);
    }

    public void put(Integer key, String val) {
        if (val == null || val.isEmpty()) {
            st.delete(key);
        } else {
            st.put(key, val);
        }
        repaint();
    }

    private void processNextOperation() {
        if (operationQueue.isEmpty()) {
            animationTimer.stop();
            return;
        }

        Operation operation = operationQueue.poll();
        if (operation.value == null || operation.value.isEmpty()) {
            st.delete(operation.key);
        } else {
            st.put(operation.key, operation.value);
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
        JButton demoButton = new JButton("Demo");
        JButton resetButton = new JButton("Reset");

        controlPanel.add(new JLabel("Key:"));
        controlPanel.add(keyField);
        controlPanel.add(new JLabel("Value:"));
        controlPanel.add(valField);
        controlPanel.add(putButton);
        controlPanel.add(deleteButton);
        controlPanel.add(demoButton);
        controlPanel.add(resetButton);

        frame.add(controlPanel, BorderLayout.SOUTH);

        putButton.addActionListener(e -> {
            try {
                int key = Integer.parseInt(keyField.getText());
                String val = valField.getText();
                visualizer.enqueuePut(key, val);
                keyField.setText("");
                valField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Key must be an integer.");
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                int key = Integer.parseInt(keyField.getText());
                visualizer.enqueueDelete(key);
                keyField.setText("");
                valField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Key must be an integer.");
            }
        });

        demoButton.addActionListener(e -> {
            visualizer.enqueuePut(25, "F");
            visualizer.enqueuePut(35, "G");
            visualizer.enqueueDelete(30);
            visualizer.enqueuePut(65, "Q");
            visualizer.enqueueDelete(70);
        });

        resetButton.addActionListener(e -> {
            visualizer.operationQueue.clear();
            if (visualizer.animationTimer.isRunning()) {
                visualizer.animationTimer.stop();
            }
            visualizer.st.delete(20);
            visualizer.st.delete(40);
            visualizer.st.delete(60);
            visualizer.st.delete(80);
            visualizer.st.delete(30);
            visualizer.st.delete(70);
            visualizer.st.delete(50);
            visualizer.st.put(50, "M");
            visualizer.st.put(30, "C");
            visualizer.st.put(70, "R");
            visualizer.st.put(20, "B");
            visualizer.st.put(40, "E");
            visualizer.st.put(60, "P");
            visualizer.st.put(80, "Z");
            visualizer.repaint();
        });

        frame.setVisible(true);
    }
}