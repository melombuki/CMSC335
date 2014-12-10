/*
 * Filename: GameTreeSurface.java
 * Date: 1 Nov. 2014
 * Last Modified: 19 Nov. 2014
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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

@SuppressWarnings("serial")
public class GameTreeSurface extends JPanel implements TreeSelectionListener {
    
    private final IOSurface ioSurface;
    private JTree jTree = null;
    private final Cave cave;
    private boolean isInitialized = false;
    private DefaultMutableTreeNode result = null;
    
    // This inner class is used with the DefaultMutableTreeNode to contain
    //  both the name of the game object, as well as the index. The toString()
    //  method is overridden to populate the JTree desirably.
    private class JTreeNodeObject {
        private final String label;
        private final int index;
        
        public JTreeNodeObject(String label, int index) {
            this.label = label;
            this.index = index;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }

	// Top-level class Constructor
    public GameTreeSurface(Cave cave, IOSurface ioSurface) {
        this.ioSurface = ioSurface;
        this.cave = cave;
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(Color.BLACK);
    }

    // Create multiple trees and display them all
    public void CreateTreeView(GameObject top) {
    	
		// Populate the jTree
		if(!isInitialized) {
    		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new JTreeNodeObject(cave.getName(), cave.index));
    		createJTreeNodes(root);
    		jTree = new JTree(new DefaultTreeModel(root));
    		jTree.setEditable(true);
    		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    		jTree.addTreeSelectionListener(this);
		}
         
        // Add the JTree to the panel
        add(jTree, BorderLayout.CENTER);
        
        // Force it to be seen
        revalidate();
        repaint();
    	
    	// The tree will be fully initialized at this point
    	isInitialized = true;
        
        // Force objects to be drawn on start
        validate();
    }
    
    public void searchAndRemoveNode(DefaultMutableTreeNode node, int index) {
    	JTreeNodeObject obj; //used to check the UserObject in the TreeNodes
    	
    	// This means the search was found, so bail
    	if(result != null) {
    		return;
    	}
    	
    	// Check this node, if it's found, set result and return, starts peel
    	obj = (JTreeNodeObject)node.getUserObject();
    	if(obj.index == index) {
    		// This is where the node should be removed.    		
    		result = node;
    		((DefaultTreeModel)jTree.getModel()).removeNodeFromParent(result);
    		return;
    	}
    	
    	// Search all of the children
    	for(int i = 0; i < node.getChildCount(); i++) {
    		DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
    		searchAndRemoveNode(child, index);
    	}
    } 
    
    // Remove the  JTrees and display them all
    public void updateTreeView(ActionEvent e) {
    	// JTree will be null until it is fully initialized after opening game file
    	if(!isInitialized)
    		return;
    	
    	// Find the object in the jTree and add or remove it
    	synchronized(jTree) {
    		DefaultMutableTreeNode root = (DefaultMutableTreeNode)jTree.getModel().getRoot();
    		
    		// Look for and remove the offending node from the JTree
	    	searchAndRemoveNode(root, ((GameObject)e.getSource()).index);
	    	
	    	// Reset the result back to null for future search
    		result = null;
    	}
    
        // Force objects to be drawn on start
        validate();
        repaint();
    }
    
    // This method populates the JTree with all of the game objects in the cave.
    private void createJTreeNodes(DefaultMutableTreeNode top) {
        
        // Add all of the loose Treasures in the cave
        for(Treasure caveTreasure : cave.getTreasures()) {
            top.add(new DefaultMutableTreeNode(
                    new JTreeNodeObject(caveTreasure.getType(), caveTreasure.index)));
        }
        
        // Add all of the loose Artifacts in the cave
        for(Artifact caveArtifact : cave.getArtifacts()) {
            top.add(new DefaultMutableTreeNode(
                    new JTreeNodeObject(caveArtifact.getType(), caveArtifact.index)));
        }
        
        // Add all of the Parties in the cave
        for(Party party : cave.getParties()) {
            DefaultMutableTreeNode childL1 = new DefaultMutableTreeNode(
                    new JTreeNodeObject(party.getName(), party.index));
            top.add(childL1);
            
            // Add all of the creatures in the party
            for(Creature creature : party.getCreatures()) {
                DefaultMutableTreeNode childL2 = new DefaultMutableTreeNode(
                        new JTreeNodeObject(creature.getName(), creature.index));
                childL1.add(childL2);
                
                // Add all of the the treasures to the creature
                for(Treasure treasure : creature.getTreasures()) {
                    DefaultMutableTreeNode childL3 = new DefaultMutableTreeNode(
                            new JTreeNodeObject(treasure.getType(), treasure.index));
                    childL2.add(childL3);
                }
                
                // Add all of the artifacts to the creature
                for(Artifact artifact : creature.getArtifacts()) {
                    DefaultMutableTreeNode childL3 = new DefaultMutableTreeNode(
                            new JTreeNodeObject(artifact.getType(), artifact.index));
                    childL2.add(childL3);
                }
                
                // Add all of the jobs to the creature
                for(Job job : creature.getJobs()) {
                    DefaultMutableTreeNode childL3 = new DefaultMutableTreeNode(
                            new JTreeNodeObject(job.getName(), job.index));
                    childL2.add(childL3);
                }
            }
        }
    }

	// This method handles making selections with the JTree and
	//  displaying the results for the user. It relies on single
	//  selection mode.
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        // Get the last element selected
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                jTree.getLastSelectedPathComponent();
        
        // If nothing is selected bail
        if(node == null)
            return;
        
        // Get the custom object with the index of the game object
        JTreeNodeObject jtno = (JTreeNodeObject) node.getUserObject();
        
        // Display the toString() method of said game object in the JTextArea
        ioSurface.setJTextArea(cave.getHashMap().get(jtno.index).toString());
    }

}