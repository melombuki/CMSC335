/*
 * Filename: GameTreeSurface.java
 * Date: 1 Nov. 2014
 * Last Modified: 11 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class creates and fills a JPanel with a visual
 *  representation of the entire game state, any search of
 *  the game state, or any tree with any single game object
 *  as the root.
 */

package com.hemen.CMSC335.SCave;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class GameTreeSurface extends JPanel implements ActionListener {
    
    private ArrayList<ButtonNodeTree> buttonNodeTrees; //holds all of the button node trees
    private ActionListener actionListener;
    private MouseListener mouseListener;
    private JTree jTree;
    private Cave cave;
    
    private enum ViewOption { ButtonNodeTree, JTree };
    private ViewOption viewOption = ViewOption.JTree;

	// Constructor
    public GameTreeSurface(Cave cave, ActionListener actionListener, MouseListener mouseListener) {
        buttonNodeTrees = new ArrayList<ButtonNodeTree>();
//        jTree = new JTree();
        this.actionListener = actionListener;
        this.mouseListener = mouseListener;
        this.cave = cave;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(Color.BLACK);
        //updateTreeView(cave);
    }

    // Create multiple trees and display them all
    public void updateTreeView(ArrayList<GameObject> roots) {
    	
    	switch(viewOption) {
    	case ButtonNodeTree:
	        // Remove and cleanup all button node trees if there are any
    		clearButtonNodeTrees();
    		
    		// ButtonNodeTree relies on the BoxLayout to work properly
    		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	        
	        // Set up all of the new trees
	        for(GameObject g : roots) {
	        	ButtonNodeTree bnt = new ButtonNodeTree(actionListener, mouseListener);
	            buttonNodeTrees.add(bnt);
	            bnt.initTree(g);
	            add(bnt);
	        }
	        break;
    	case JTree:
    		// Remove and cleanup all button node trees if there are any
    		clearButtonNodeTrees();  
    		
    		// Use border layout to force JTree to take up all space in JPanel
    		setLayout(new BorderLayout());
    		
    		// If jTree has not been populated, do so now
    		if(jTree == null) {
        		// This is where the JTree will be built and then displayed :S
    		    
        		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Cave");
        		createJTreeNodes(root);
        		jTree = new JTree(root);
    		}
   		
    		// Add the JTree to the panel
            add(jTree, BorderLayout.CENTER);
            
            // Force it to be seen
            revalidate();
            repaint();
            
    		break;
    	}
        
        // Force objects to be drawn on start
        validate();
    }
    
    // This method populates the JTree with all of the game objects in the cave.
    private void createJTreeNodes(DefaultMutableTreeNode top) {
        
        // Add all of the loose Treasures in the cave
        for(Treasure caveTreasure : cave.getTreasures()) {
            top.add(new DefaultMutableTreeNode(caveTreasure.getType()));
        }
        
        // Add all of the loose Artifacts in the cave
        for(Artifact caveArtifact : cave.getArtifacts()) {
            top.add(new DefaultMutableTreeNode(caveArtifact.getType()));
        }
        
        // Add all of the Parties in the cave
        for(Party party : cave.getParties()) {
            DefaultMutableTreeNode childL1 = new DefaultMutableTreeNode(party.getName());
            top.add(childL1);
            
            // Add all of the creatures in the party
            for(Creature creature : party.getCreatures()) {
                DefaultMutableTreeNode childL2 = new DefaultMutableTreeNode(creature.getName());
                childL1.add(childL2);
                
                // Add all of the the treasures to the creature
                for(Treasure treasure : creature.getTreasures()) {
                    DefaultMutableTreeNode childL3 = new DefaultMutableTreeNode(treasure.getType());
                    childL2.add(childL3);
                }
                
                // Add all of the artifacts to the creature
                for(Artifact artifact : creature.getArtifacts()) {
                    DefaultMutableTreeNode childL3 = new DefaultMutableTreeNode(artifact.getType());
                    childL2.add(childL3);
                }
                
                // Add all of the jobs to the creature
                for(Job job : creature.getJobs()) {
                    DefaultMutableTreeNode childL3 = new DefaultMutableTreeNode(job.getName());
                    childL2.add(childL3);
                }
            }
        }
    }
    
    // Clears all button node trees and all elements within each one.
    //  also clears all of the GUI elements from this surface.
    private void clearButtonNodeTrees() {
    	//Ensure the trees are all cleared
        for(ButtonNodeTree bnt : buttonNodeTrees)
            if(!bnt.isEmpty())
                bnt.clearAll();
        
        // Remove all trees from the list
        buttonNodeTrees.clear();
        
        // Remove all components from this JPanel
        removeAll();
        
        // Force repainting
        validate();
    }
    
    // Create a Tree from a single root
    //  Convenience method for single tree.
    public void updateTreeView(GameObject root) {
        ArrayList<GameObject> roots = new ArrayList<GameObject>();
        roots.add(root);
        
        updateTreeView(roots);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		 if(e.getActionCommand().equals("JTree View")) {
		    
		    // Set the view option to JTree
			viewOption = ViewOption.JTree;
			
        	// Remove and clean up all button node trees if there are any
    		clearButtonNodeTrees();
    		
    		// Create and show the JTree view
    		updateTreeView(cave);
        }
        else if(e.getActionCommand().equals("ButtonNodeTree View")) {

            // Set the view option to ButtonNodeTree
        	viewOption = ViewOption.ButtonNodeTree;
        	
        	// Create the entire tree representation
        	updateTreeView(cave);
        }
	}
	
	// Getters and setters
	/**
	 * @return viewOption
	 */
    public ViewOption getViewOption() {
		return viewOption;
	}
    
    /**
     * @return buttonNodeTrees
     */
    public ArrayList<ButtonNodeTree> getButtonNodeTrees() {
        return buttonNodeTrees;
    }
}