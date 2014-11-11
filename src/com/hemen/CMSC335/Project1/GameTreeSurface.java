/*
 * Filename: GameTreeSurface.java
 * Date: 1 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class creates and fills a JPanel with a visual
 *  representation of the game state from the input file. It adds
 *  JButtons for each element in the game. The cave is the root.
 *  Lines are drawn to show the tree structure. The lines are
 *  redrawn as needed to maintain proper visual representation
 *  as the window is resized or moved through the paint method.
 *  The name of each JButton stores the index of the game object.
 */

package com.hemen.CMSC335.Project1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.hemen.CMSC335.Project1.ButtonNodeTree.Node;

@SuppressWarnings("serial")
public class GameTreeSurface extends JPanel {
    
    private ArrayList<ButtonNodeTree> buttonNodeTrees;
    private ActionListener listener;
    
    // Constructor for top level class
    public GameTreeSurface(ActionListener listener) {
        buttonNodeTrees = new ArrayList<ButtonNodeTree>();
        this.listener = listener;
        
        setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(Color.BLACK);
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
    
    // Custom painting is done here.
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        super.paintComponent(g);
        
        // Get the original color and save it
        Color originalColor = g2d.getColor();
        
        // Draw the lines in green
        g2d.setColor(Color.GREEN);
        
        // Draw the actual lines
        for(ButtonNodeTree bnt : buttonNodeTrees)
            drawGraphLines(bnt.root, g2d);
        
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
    
    // Testing - multiple roots in one view
    public void updateTreeView(ArrayList<GameObject> roots) {
        // Ensure the tree nodes are reset
        for(ButtonNodeTree bnt : buttonNodeTrees)
            if(bnt.root != null)
                bnt.clearAll();
        
        buttonNodeTrees.clear();
        
        // Remove all components from this JPanel
        this.removeAll();
        
        for(GameObject g : roots) {
            buttonNodeTrees.add(new ButtonNodeTree());
            updateTree(g, buttonNodeTrees.get(buttonNodeTrees.size()-1));
        }
    }
    
    // Testing - multiple roots in one view
    public void updateTreeView(GameObject root) {
        // Ensure the tree nodes are reset
        for(ButtonNodeTree bnt : buttonNodeTrees)
            if(bnt.root != null)
                bnt.clearAll();
        
        buttonNodeTrees.clear();
        
        // Remove all components from this JPanel
        this.removeAll();
    
        buttonNodeTrees.add(new ButtonNodeTree());
        updateTree(root, buttonNodeTrees.get(buttonNodeTrees.size()-1));

    }
    
    // This method is called to create the GUI Node tree representation
    //  from the tree structure of the cave object.
    private void updateTree(GameObject gRoot, ButtonNodeTree bnt) {
        JButton button;
        
        if(gRoot instanceof Cave) {
            
            addCave((Cave)gRoot, bnt.root, bnt);
            
        } else if(gRoot instanceof Party) {
            
            addParty((Party)gRoot, bnt.root, bnt);
            
        } else if(gRoot instanceof Creature) {

            addCreature((Creature)gRoot, bnt.root, bnt);
            
        } else if(gRoot instanceof Treasure) {
            // Create the root object representation
            button = new JButton(((Treasure) gRoot).getType());
            button.setName(Integer.toString(((Treasure) gRoot).getIndex())); //index for cave is 0
            button.addActionListener(listener);
            bnt.setRoot(button);
            
        } else if(gRoot instanceof Artifact) {
            // Create the root object representation
            button = new JButton(((Artifact) gRoot).getName());
            button.setName(Integer.toString(((Artifact) gRoot).getIndex())); //index for cave is 0
            button.addActionListener(listener);
            bnt.setRoot(button);
        }

        // Create the root box element with 0 top padding and resizing bottom padding
        add(new Box.Filler(new Dimension(0,5), new Dimension(0,5), new Dimension(0,5)));
        //Constructor: Box.Filler(Dimension min, Dimension pref, Dimension max)
        add(new Box(BoxLayout.X_AXIS));
        add(Box.createVerticalGlue()); //add bottom glue padding
        
        // Create the tree
        int level = 0; //current level while creating the tree
        createTree(bnt.root, level);
        
        // Force the new objects to be redrawn
        this.validate();
        this.repaint();
    }
    
    //TODO: add headers to each block
    private void addCave(Cave cave, Node node, ButtonNodeTree bnt) {
        JButton button;
        
        // Create the root cave object's representation
        button = new JButton(cave.getName());
        button.setName(Integer.toString(cave.getIndex())); //index for cave is 0
        button.addActionListener(listener);
        button.setToolTipText(cave.toString());
        bnt.setRoot(button);
        node  = bnt.root;
        
        for(Treasure treasure : cave.getTreasures())
            addTreasure(treasure, node);
        
        for(Artifact artifact : cave.getArtifacts())
            addArtifact(artifact, node);
        
        for(Party party : cave.getParties())
            addParty(party, node, bnt);
    }
    
    private void addTreasure(Treasure treasure, Node node) {
        JButton button;

        // Add the treasure to the tree
        button = new JButton(treasure.getType());
        button.setName(Integer.toString(treasure.getIndex()));
        button.addActionListener(listener);
        button.setToolTipText(treasure.toString());
        node.add(button);
    }
     
    private void addArtifact(Artifact artifact, Node node) {
        JButton button;
        
        // Add the artifacts to the tree
        button = new JButton(artifact.getName());
        button.setName(Integer.toString(artifact.getIndex()));
        button.addActionListener(listener);
        button.setToolTipText(artifact.toString());
        node.add(button);
    }
    
    private void addParty(Party party, Node node, ButtonNodeTree bnt) {
        JButton button;
        Node partyNode;
        button = new JButton(party.getName());
        button.setName(Integer.toString(party.getIndex())); //index for cave is 0
        button.addActionListener(listener);
        button.setToolTipText(party.toString());
        
        // There is already a root, so just add a new party node
        if(bnt.root == null) {
            bnt.setRoot(button);
            node = bnt.root;
        }
        else {
            partyNode = node.add(button);
            node = partyNode;
        }

        // Add it's creatures to the tree
        for(Creature creature : party.getCreatures())
            addCreature(creature, node, bnt);
    }
    
    private void addCreature(Creature creature, Node node, ButtonNodeTree bnt) {
        JButton button;
        Node creatureNode;
        
        // Add each party's creatures to the tree
        button = new JButton(creature.getName());
        button.setName(Integer.toString(creature.getIndex()));
        button.addActionListener(listener);
        button.setToolTipText(creature.toString());
        
        // There is already a root, so just add a new party node
        if(bnt.root == null) {
            bnt.setRoot(button);
            node = bnt.root;
        }
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
}