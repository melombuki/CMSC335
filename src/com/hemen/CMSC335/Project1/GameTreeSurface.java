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

package com.hemen.CMSC335.Project1;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameTreeSurface extends JPanel {
    
    private ArrayList<ButtonNodeTree> buttonNodeTrees; //holds all of the trees
    private ActionListener listener;
    
    // Constructor for top level class
    public GameTreeSurface(ActionListener listener) {
        buttonNodeTrees = new ArrayList<ButtonNodeTree>();
        this.listener = listener;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(Color.BLACK);
    }

    // Create multiple trees and display them all
    public void updateTreeView(ArrayList<GameObject> roots) {
        //Ensure the tree nodes are reset
        for(ButtonNodeTree bnt : buttonNodeTrees)
            if(bnt.root != null)
                bnt.clearAll();
        
        buttonNodeTrees.clear();
        
        // Remove all components from this JPanel
        this.removeAll();
        
        // Set up all of the trees
        for(GameObject g : roots) {
        	ButtonNodeTree bnt = new ButtonNodeTree(listener);
            buttonNodeTrees.add(bnt);
            bnt.initTree(g);
            add(bnt);
        }
        
        // Force objects to be drawn on start
        validate();  
    }
    
    // Create a Tree from a single root
    public void updateTreeView(GameObject root) {
        ArrayList<GameObject> r = new ArrayList<GameObject>();
        r.add(root);
        
        updateTreeView(r);
    }
}