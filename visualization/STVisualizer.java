package visualization;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import javax.swing.*;

public class STVisualizer extends JPanel {
    private Node root;
    private final Queue<AnimationStep> animationQueue = new LinkedList<>();
    private final Timer animationTimer;
    private final int nodeRadius = 20;
    private final int levelHeight = 60;
    private final int stepDelay = 800;
    
    // Visualization state
    private Node highlightedNode = null;
    private Node targetNode = null;
    private String currentMessage = "";
    private final Set<Integer> searchPathKeys = new HashSet<>();
    
    // Store operation parameters for execution
    private Integer pendingKey = null;
    private String pendingValue = null;
    private boolean pendingIsDelete = false;

    // Custom Node class that we control completely
    private static class Node {
        private final int key;
        private String val;
        private Node left, right;
        private int N;

        public Node(int key, String val) {
            this.key = key;
            this.val = val;
            this.N = 1;
        }
        
        public int getKey() { return key; }
        public String getVal() { return val; }
        public Node getLeft() { return left; }
        public Node getRight() { return right; }
    }

    private static class AnimationStep {
        enum StepType {
            SEARCH_START, SEARCH_LEFT, SEARCH_RIGHT, FOUND, NOT_FOUND,
            INSERT_NODE, DELETE_LEAF, DELETE_ONE_CHILD, 
            DELETE_TWO_CHILDREN, REPLACE_WITH_MIN, COMPLETE
        }
        
        StepType type;
        Integer key;
        Node currentNode;
        String message;
        
        AnimationStep(StepType type, Integer key, Node currentNode, String message) {
            this.type = type;
            this.key = key;
            this.currentNode = currentNode;
            this.message = message;
        }
    }

    public STVisualizer() {
        setBackground(Color.WHITE);
        initializeSampleTree();
        
        animationTimer = new Timer(stepDelay, e -> processNextAnimationStep());
    }

    private void initializeSampleTree() {
        root = null;
        putImmediate(50, "M");
        putImmediate(30, "C");
        putImmediate(70, "R");
        putImmediate(20, "B");
        putImmediate(40, "E");
        putImmediate(60, "P");
        putImmediate(80, "Z");
        repaint();
    }
    
    // Immediate put without animation (for initialization)
    private void putImmediate(int key, String val) {
        root = putImmediateHelper(root, key, val);
    }
    
    private Node putImmediateHelper(Node x, int key, String val) {
        if (x == null) return new Node(key, val);
        if (key < x.key) x.left = putImmediateHelper(x.left, key, val);
        else if (key > x.key) x.right = putImmediateHelper(x.right, key, val);
        else x.val = val;
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    private int size(Node x) {
        if (x == null) return 0;
        return x.N;
    }

    public void animatePut(int key, String val) {
        animationQueue.clear();
        searchPathKeys.clear();
        highlightedNode = null;
        targetNode = null;
        currentMessage = "Starting put(" + key + ", " + val + ")";
        pendingKey = key;
        pendingValue = val;
        pendingIsDelete = false;
        
        // Generate animation steps for put operation
        generatePutAnimationSteps(key, val);
        
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    private void generatePutAnimationSteps(int key, String val) {
        // First, record the search path
        Node current = root;
        
        animationQueue.add(new AnimationStep(
            AnimationStep.StepType.SEARCH_START, key, root,
            "Searching for key " + key + " starting from root"
        ));
        
        while (current != null) {
            
            if (key < current.key) {
                animationQueue.add(new AnimationStep(
                    AnimationStep.StepType.SEARCH_LEFT, key, current,
                    key + " < " + current.key + ", going left"
                ));
                current = current.left;
            } else if (key > current.key) {
                animationQueue.add(new AnimationStep(
                    AnimationStep.StepType.SEARCH_RIGHT, key, current,
                    key + " > " + current.key + ", going right"
                ));
                current = current.right;
            } else {
                animationQueue.add(new AnimationStep(
                    AnimationStep.StepType.FOUND, key, current,
                    "Found key " + key + "! Updating value to " + val
                ));
                break;
            }
        }
        
        if (current == null) {
            animationQueue.add(new AnimationStep(
                AnimationStep.StepType.NOT_FOUND, key, null,
                "Reached null. Inserting new node with key " + key
            ));
        }
        
        // Add the actual insertion/update step
        animationQueue.add(new AnimationStep(
            AnimationStep.StepType.INSERT_NODE, key, null,
            "Inserting node " + key + ":" + val
        ));
        
        animationQueue.add(new AnimationStep(
            AnimationStep.StepType.COMPLETE, key, null,
            "Operation complete"
        ));
    }

    public void animateDelete(int key) {
        animationQueue.clear();
        searchPathKeys.clear();
        highlightedNode = null;
        targetNode = null;
        currentMessage = "Starting delete(" + key + ")";
        pendingKey = key;
        pendingValue = null;
        pendingIsDelete = true;
        
        // Generate animation steps for delete operation
        generateDeleteAnimationSteps(key);
        
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    private void generateDeleteAnimationSteps(int key) {
        Node current = root;
        
        animationQueue.add(new AnimationStep(
            AnimationStep.StepType.SEARCH_START, key, root,
            "Searching for key " + key + " to delete"
        ));
        
        // Find the node to delete
        while (current != null && current.key != key) {
            if (key < current.key) {
                animationQueue.add(new AnimationStep(
                    AnimationStep.StepType.SEARCH_LEFT, key, current,
                    key + " < " + current.key + ", going left"
                ));
                current = current.left;
            } else {
                animationQueue.add(new AnimationStep(
                    AnimationStep.StepType.SEARCH_RIGHT, key, current,
                    key + " > " + current.key + ", going right"
                ));
                current = current.right;
            }
        }
        
        if (current == null) {
            animationQueue.add(new AnimationStep(
                AnimationStep.StepType.NOT_FOUND, key, null,
                "Key " + key + " not found. Nothing to delete."
            ));
            animationQueue.add(new AnimationStep(
                AnimationStep.StepType.COMPLETE, key, null,
                "Operation complete"
            ));
            return;
        }
        
        animationQueue.add(new AnimationStep(
            AnimationStep.StepType.FOUND, key, current,
            "Found node to delete: " + key
        ));
        
        // Determine deletion case
        if (current.left == null && current.right == null) {
            animationQueue.add(new AnimationStep(
                AnimationStep.StepType.DELETE_LEAF, key, current,
                "Node " + key + " is a leaf. Removing it."
            ));
        } else if (current.left == null) {
            animationQueue.add(new AnimationStep(
                AnimationStep.StepType.DELETE_ONE_CHILD, key, current,
                "Node " + key + " has only right child. Replacing with right child."
            ));
        } else if (current.right == null) {
            animationQueue.add(new AnimationStep(
                AnimationStep.StepType.DELETE_ONE_CHILD, key, current,
                "Node " + key + " has only left child. Replacing with left child."
            ));
        } else {
            animationQueue.add(new AnimationStep(
                AnimationStep.StepType.DELETE_TWO_CHILDREN, key, current,
                "Node " + key + " has two children. Finding successor..."
            ));
            
            // Find minimum in right subtree
            Node successor = current.right;
            
            while (successor.left != null) {
                animationQueue.add(new AnimationStep(
                    AnimationStep.StepType.SEARCH_LEFT, key, successor,
                    "Finding minimum in right subtree: visiting " + successor.key
                ));
                successor = successor.left;
            }
            
            animationQueue.add(new AnimationStep(
                AnimationStep.StepType.REPLACE_WITH_MIN, key, successor,
                "Replacing " + key + " with successor " + successor.key
            ));
        }
        
        animationQueue.add(new AnimationStep(
            AnimationStep.StepType.COMPLETE, key, null,
            "Delete operation complete"
        ));
    }

    private void processNextAnimationStep() {
        if (animationQueue.isEmpty()) {
            animationTimer.stop();
            highlightedNode = null;
            searchPathKeys.clear();
            currentMessage = "";
            pendingKey = null;
            pendingValue = null;
            repaint();
            return;
        }
        
        AnimationStep step = animationQueue.poll();
        currentMessage = step.message;
        
        switch (step.type) {
            case SEARCH_START -> {
                highlightedNode = step.currentNode;
                searchPathKeys.clear();
            }
            case SEARCH_LEFT -> {
                if (step.currentNode != null) {
                    searchPathKeys.add(step.currentNode.key);
                }
                highlightedNode = null;
            }
            case SEARCH_RIGHT -> {
                if (step.currentNode != null) {
                    searchPathKeys.add(step.currentNode.key);
                }
                highlightedNode = null;
            }
            case FOUND -> {
                highlightedNode = step.currentNode;
                if (step.currentNode != null) {
                    searchPathKeys.add(step.currentNode.key);
                }
            }
            case NOT_FOUND -> {
                highlightedNode = null;
            }
            case INSERT_NODE -> {
                // Actually perform the insertion/update
                if (pendingKey != null) {
                    root = performPut(root, pendingKey, pendingValue);
                    searchPathKeys.clear();
                    highlightedNode = null;
                }
            }
            case DELETE_LEAF, DELETE_ONE_CHILD, DELETE_TWO_CHILDREN -> {
                targetNode = step.currentNode;
            }
            case REPLACE_WITH_MIN -> {
                targetNode = step.currentNode;
            }
            case COMPLETE -> {
                // Perform the actual delete operation
                if (pendingIsDelete && pendingKey != null) {
                    root = performDelete(root, pendingKey);
                }
                highlightedNode = null;
                targetNode = null;
                searchPathKeys.clear();
            }
        }
        
        repaint();
    }

    private Node performPut(Node x, int key, String val) {
        if (x == null) return new Node(key, val);
        if (key < x.key) x.left = performPut(x.left, key, val);
        else if (key > x.key) x.right = performPut(x.right, key, val);
        else x.val = val;
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    private Node performDelete(Node x, int key) {
        if (x == null) return null;
        if (key < x.key) x.left = performDelete(x.left, key);
        else if (key > x.key) x.right = performDelete(x.right, key);
        else {
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        return min(x.left);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.N = size(x.left) + size(x.right) + 1;
        return x;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw message at top
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        if (!currentMessage.isEmpty()) {
            g.drawString(currentMessage, 10, 20);
        }
        
        if (root == null) return;
        drawNode(g, root, getWidth() / 2, 60, getWidth() / 4);
    }

    private void drawNode(Graphics g, Node node, int x, int y, int xOffset) {
        if (node == null) return;

        // Draw left child
        if (node.left != null) {
            int childX = x - xOffset;
            int childY = y + levelHeight;
            g.setColor(Color.GRAY);
            g.drawLine(x, y + nodeRadius, childX, childY - nodeRadius);
            drawNode(g, node.left, childX, childY, xOffset / 2);
        }

        // Draw right child
        if (node.right != null) {
            int childX = x + xOffset;
            int childY = y + levelHeight;
            g.setColor(Color.GRAY);
            g.drawLine(x, y + nodeRadius, childX, childY - nodeRadius);
            drawNode(g, node.right, childX, childY, xOffset / 2);
        }

        // Determine node color based on state
        Color nodeColor = Color.CYAN;
        if (node == highlightedNode) {
            nodeColor = Color.YELLOW;
        } else if (node == targetNode) {
            nodeColor = Color.RED;
        } else if (searchPathKeys.contains(node.key)) {
            nodeColor = Color.ORANGE;
        }

        // Draw current node
        g.setColor(nodeColor);
        g.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
        g.setColor(Color.BLACK);
        g.drawOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

        // Display key and value
        String text = node.key + ":" + node.val;
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g.drawString(text, x - textWidth / 2, y + textHeight / 4);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("BST Symbol Table Visualization with Step-by-Step Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);

        STVisualizer visualizer = new STVisualizer();
        frame.add(visualizer, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JTextField keyField = new JTextField(5);
        JTextField valField = new JTextField(5);
        JButton putButton = new JButton("Animate Put");
        JButton deleteButton = new JButton("Animate Delete");
        JButton demoButton = new JButton("Demo Sequence");
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
                if (val.isEmpty()) val = "?";
                visualizer.animatePut(key, val);
                keyField.setText("");
                valField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Key must be an integer.");
            }
        });

        deleteButton.addActionListener(e -> {
            try {
                int key = Integer.parseInt(keyField.getText());
                visualizer.animateDelete(key);
                keyField.setText("");
                valField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Key must be an integer.");
            }
        });

        demoButton.addActionListener(e -> {
            // Demonstrate various operations with delays between them
            visualizer.animatePut(25, "F");
            
            // Schedule additional operations after the first one
            Timer delayTimer = new Timer(3000, ev -> visualizer.animatePut(35, "G"));
            delayTimer.setRepeats(false);
            delayTimer.start();
            
            Timer delayTimer2 = new Timer(6000, ev -> visualizer.animateDelete(30));
            delayTimer2.setRepeats(false);
            delayTimer2.start();
            
            Timer delayTimer3 = new Timer(9000, ev -> visualizer.animatePut(65, "Q"));
            delayTimer3.setRepeats(false);
            delayTimer3.start();
            
            Timer delayTimer4 = new Timer(12000, ev -> visualizer.animateDelete(70));
            delayTimer4.setRepeats(false);
            delayTimer4.start();
        });

        resetButton.addActionListener(e -> {
            visualizer.animationQueue.clear();
            visualizer.animationTimer.stop();
            visualizer.initializeSampleTree();
            visualizer.highlightedNode = null;
            visualizer.targetNode = null;
            visualizer.searchPathKeys.clear();
            visualizer.currentMessage = "";
            visualizer.pendingKey = null;
            visualizer.pendingValue = null;
            visualizer.repaint();
        });

        frame.setVisible(true);
    }
}