package com.hemen.CMSC335.Project1;

import java.awt.Component;
import java.util.ArrayList;

public class ButtonNodeTree {
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
    
    // Sets the root from a component
    public void setRoot(Component c) {
        root = new Node(c);
    }
}
