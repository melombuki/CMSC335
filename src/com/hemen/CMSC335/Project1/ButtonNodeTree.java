package com.hemen.CMSC335.Project1;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class ButtonNodeTree extends JComponent{
    public Node root = null;
    
    public ButtonNodeTree() {
    }
    
    // This class stores a second tree representing the game state.
    //  It is a model of the actual game tree for the GUI only.
    public class Node {
        public ArrayList<Node> children;
        private Component item;
        
        // Constructor for inner class
        public Node(Component item) {
            this.item = item;
            children = new ArrayList<Node>();
        }
        
        // Adds a child node to this object
        public Node add(Component child) {
            Node n = new Node(child);
            children.add(n);
            return n;
        }
        
        // Getters and Setters
        /**
         * @return item
         */
        public Component getItem() {
            return item;
        }
        
        /**
         * @return children
         */
        public ArrayList<Node> getChildren() {
            return children;
        }
    }
    
    // This is a recursive search method that searches the tree
    //  for a node with the matching index (component's name).
    private Component searchByIndex(Node root, String index) {
        if(root.item.getName().equals(index))
            return root.item;
        
        for(Node child : root.children)
            searchByIndex(child, index);
        
        return null;
    }
    
    // This is a helper method to the recursive search by index
    //  method. It will search the entire tree no matter which
    //  node it is called from.
    public Component searchByIndex(String index) {
        if(root == null)
            return null;
        
        return searchByIndex(root, index);
    }
    
    // Recursive helper method to clear all nodes in the tree
    public void clearAll() {
        if(root == null)
            return;
        
        clear(root);
        root = null;
    }
    
    // Recursive method to clear all nodes in tree
    private void clear(Node node) {
        if(node.children.isEmpty()) {
            return;
        }
        
        for(Node n : node.children) {
            clear(n);
            n = null;
        }
    }
    
    // Custom painting is done here.
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Get the original color and save it
        Color originalColor = g2d.getColor();
        
        // Draw the lines in green
        g2d.setColor(Color.GREEN);
        
        // Draw the actual lines
        drawGraphLines(root, g2d);
        
        // Go back to the original color
        g2d.setColor(originalColor);
    }
    
    // Draws the lines from parent to children in the Node tree
    private void drawGraphLines(ButtonNodeTree.Node root, Graphics2D g2d) {
        // Bail if there is no root
        if(root == null)
            return;
        
        // Bail if there are no children to add
        if(root.children == null || root.children.size() == 0)
            return;
        
        // Line beginning and end points
        int x1, y1, x2, y2;
        
        // Get the middle bottom point of the parent
        x1 = root.getItem().getX() + root.getItem().getWidth()/2;        
        y1 = root.getItem().getParent().getY() + root.getItem().getParent().getHeight();
        
        // Get the middle top of each child element and draw the line
        for(ButtonNodeTree.Node child : root.children) {
            x2 = child.getItem().getX() + child.getItem().getWidth()/2;
            y2 = child.getItem().getParent().getY();
            
            g2d.drawLine(x1, y1,x2, y2);
            
            drawGraphLines(child, g2d);
        }
    }
    
    // Sets the root from a component
    public void setRoot(Component c) {
        root = new Node(c);
    }
}
