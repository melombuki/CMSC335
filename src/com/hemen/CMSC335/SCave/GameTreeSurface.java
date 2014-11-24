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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTree;

@SuppressWarnings("serial")
public class GameTreeSurface extends JPanel implements ActionListener {
    
    private ArrayList<ButtonNodeTree> buttonNodeTrees; //holds all of the trees
    private ActionListener listener;
    private ArrayList<JTree> jTrees;
    
    private enum ViewOption { ButtonNodeTree, JTree };
    private ViewOption viewOption = ViewOption.ButtonNodeTree;
    
    // Constructor
    public GameTreeSurface(ActionListener listener) {
        buttonNodeTrees = new ArrayList<ButtonNodeTree>();
        this.listener = listener;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(Color.BLACK);
    }

    // Create multiple trees and display them all
    public void updateTreeView(ArrayList<GameObject> roots) {
    	
    	switch(viewOption) {
    	case ButtonNodeTree:
	        //Ensure the trees are all cleared
	        for(ButtonNodeTree bnt : buttonNodeTrees)
	            if(!bnt.isEmpty())
	                bnt.clearAll();
	        
	        // Remove all trees from the list
	        buttonNodeTrees.clear();
	        
	        // Remove all components from this JPanel
	        this.removeAll();
	        
	        // Set up all of the new trees
	        for(GameObject g : roots) {
	        	ButtonNodeTree bnt = new ButtonNodeTree(listener);
	            buttonNodeTrees.add(bnt);
	            bnt.initTree(g);
	            add(bnt);
	        }
	        break;
    	case JTree:
    		break;
    	}
        
        // Force objects to be drawn on start
        validate();
    }
    
    // Create a Tree from a single root
    //  Convenience method for single tree.
    public void updateTreeView(GameObject root) {
        ArrayList<GameObject> r = new ArrayList<GameObject>();
        r.add(root);
        
        updateTreeView(r);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		 if(e.getActionCommand().equals("JTreeView")) {
			//TODO: Use the JTree view stuff
            System.out.println("JTree Menu Item");
        }
        else if(e.getActionCommand().equals("ButtonNodeTreeView")) {
        	//TODO: Use the stuff I made(that's way better :p)
            System.out.println("ButtonNodeTree Menu Item");
        }
	}
}