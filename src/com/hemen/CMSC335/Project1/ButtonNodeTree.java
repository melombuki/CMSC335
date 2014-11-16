/*
 * Filename: ButtonNodeTree.java
 * Date: 1 Nov. 2014
 * Last Modified: 11 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class creates and fills a JPanel with a visual
 *  representation of a tree of any game object. It adds
 *  JButtons for each element in the tree. Anything can be the root.
 *  Lines are drawn between nodes to show the tree structure. The lines are
 *  redrawn as needed to maintain proper visual representation
 *  as the window is resized or moved through the paint method.
 *  The name of each JButton stores the index of the game object.
 */

package com.hemen.CMSC335.Project1;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ButtonNodeTree extends JPanel {
    private ActionListener listener;
    private Node root = null;
    
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
    
    // Constructor
    public ButtonNodeTree(ActionListener listener) {
    	this.listener = listener;
    	
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(Color.BLACK);
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
        // Bail if there is nothing to clear
        if(root == null)
            return;
        
        // Remove the components from the JPanel
        this.removeAll(); 
        
        // Recursive call to clear all children
        clear(root);       
        
        // Dump reference to the root
        root = null;
    }
    
    // This method is a recursive method to clear all nodes in tree.
    private void clear(Node node) {
        while(!node.children.isEmpty()) {
            clear(node.children.get(0));
            node.children.remove(0);
        }
    }
    
    // This method is where custom painting is done.
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        super.paintComponent(g);
        
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
    
    //Used to create the root level and creates all others with recursive call
    private void createTree(ButtonNodeTree.Node root, int level) {
        // Add the root item to the box with left and right padding
        Box box = (Box)this.getComponent(((level + 1) * 3) - 2);
        
        box.add(Box.createHorizontalGlue()); //add left glue padding
        box.add(root.getItem());
        box.add(Box.createHorizontalGlue()); //add right glue padding
        
        // Stop here if there are no children to add
        if(root.children == null || root.children.size() == 0)
            return;  
        
        // Create a new box for the next level down in the tree
        if(this.getComponents().length < ((level + 1) * 3) + 1) {
            Box childBox = new Box(BoxLayout.X_AXIS);
        
            // Add the child box to the panel with top and bottom padding
            add(Box.createVerticalGlue()); //add top glue padding
            add(childBox);
            add(Box.createVerticalGlue()); //add bottom glue padding
        }
        
        // Increment level because there are children
        level++;
        
        // Recursive call
        for(ButtonNodeTree.Node child : root.getChildren()) {
            createTree(child, level);
        }
    }
    
    // This method is called to create the GUI Node tree representation
    //  from the tree structure of the cave object.
    public void initTree(GameObject gRoot) {
        JButton button;
        
        if(gRoot instanceof Cave) {
            
            addCave((Cave)gRoot, root);
            
        } else if(gRoot instanceof Party) {
            
            addParty((Party)gRoot, root);
            
        } else if(gRoot instanceof Creature) {

            addCreature((Creature)gRoot, root);
            
        } else if(gRoot instanceof Treasure) {
            // Create the root object representation
            button = new JButton(((Treasure) gRoot).getType());
            button.setName(Integer.toString(((Treasure) gRoot).getIndex())); //index for cave is 0
            button.addActionListener(listener);
            setRoot(button);
            
        } else if(gRoot instanceof Artifact) {
            // Create the root object representation
            button = new JButton(((Artifact) gRoot).getName());
            button.setName(Integer.toString(((Artifact) gRoot).getIndex())); //index for cave is 0
            button.addActionListener(listener);
            setRoot(button);
        }

        // Create the root box element with 0 top padding and resizing bottom padding
        add(new Box.Filler(new Dimension(0,5), new Dimension(0,5), new Dimension(0,5)));
        //Constructor: Box.Filler(Dimension min, Dimension pref, Dimension max)
        add(new Box(BoxLayout.X_AXIS));
        add(Box.createVerticalGlue()); //add bottom glue padding
        
        // Create the tree
        int level = 0; //current level while creating the tree
        createTree(root, level);
    }
    
    // Creates the tree structure from a cave
    private void addCave(Cave cave, Node node) {
        JButton button;
        
        // Create the root cave object's representation
        button = new JButton(cave.getName());
        button.setName(Integer.toString(cave.getIndex())); //index for cave is 0
        button.addActionListener(listener);
        button.setToolTipText(cave.toString());
        setRoot(button);
        node  = root;
        
        for(Treasure treasure : cave.getTreasures())
            addTreasure(treasure, node);
        
        for(Artifact artifact : cave.getArtifacts())
            addArtifact(artifact, node);
        
        for(Party party : cave.getParties())
            addParty(party, node);
    }
    
    // Creates a tree structure from a treasure. Handles as root
    //  or if it is a child of the root.
    private void addTreasure(Treasure treasure, Node node) {
        JButton button;

        // Add the treasure to the tree
        button = new JButton(treasure.getType());
        button.setName(Integer.toString(treasure.getIndex()));
        button.addActionListener(listener);
        button.setToolTipText(treasure.toString());
        node.add(button);
    }
     
    //Creates a tree structure form an Artifact. Handles as root
    //  or if it is a child of the root.
    private void addArtifact(Artifact artifact, Node node) {
        JButton button;
        
        // Add the artifacts to the tree
        button = new JButton(artifact.getName());
        button.setName(Integer.toString(artifact.getIndex()));
        button.addActionListener(listener);
        button.setToolTipText(artifact.toString());
        node.add(button);
    }
    
    // Creates a tree structure from a party. Handles as root
    //  or if it is a child of the root.
    private void addParty(Party party, Node node) {
        JButton button;
        Node partyNode;
        button = new JButton(party.getName());
        button.setName(Integer.toString(party.getIndex())); //index for cave is 0
        button.addActionListener(listener);
        button.setToolTipText(party.toString());
        
        // There is already a root, so just add a new party node
        if(root == null) {
            setRoot(button);
            node = root;
        }
        // This is to be the new root
        else {
            partyNode = node.add(button);
            node = partyNode;
        }

        // Add it's creatures to the tree
        for(Creature creature : party.getCreatures())
            addCreature(creature, node);
    }
    
    // Creates a tree structure from a Creature. Handles as root
    //  or if it is a child of the root.
    private void addCreature(Creature creature, Node node) {
        JButton button;
        Node creatureNode;
        
        // Add each party's creatures to the tree
        button = new JButton(creature.getName());
        button.setName(Integer.toString(creature.getIndex()));
        button.addActionListener(listener);
        button.setToolTipText(creature.toString());
        
        // There is already a root, so just add a new party node
        if(root == null) {
            setRoot(button);
            node = root;
        }
        // This is to be the new root
        else {
            creatureNode = node.add(button);
            node = creatureNode;
        }
        
        // Add it's treasures to the tree
        for(Treasure treasure : creature.getTreasures())
                addTreasure(treasure, node);
            
        // Add it's artifacts to the tree
        for(Artifact artifact : creature.getArtifacts())
            addArtifact(artifact, node);
    }
    
    // Sets the root from a component
    public void setRoot(Component c) {
        root = new Node(c);
    }
    
    // Checks if root is set to null or not
    public boolean isEmpty() {
        return (root == null) ? true : false;
    }
}
