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
import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

@SuppressWarnings("serial")
public class GameTreeSurface extends JPanel implements TreeSelectionListener
, DragGestureListener, DragSourceListener, DropTargetListener {
    
    private final IOSurface ioSurface;
    private DragSource dragSource;
    private DropTarget dropTarget;
    private JTree jTree = null;
    private final Cave cave;
    private boolean isInitialized = false;
    private DefaultMutableTreeNode result = null;
    
    // This inner class is used with the DefaultMutableTreeNode to contain
    //  both the name of the game object, as well as the index. The toString()
    //  method is overridden to populate the JTree desirably.
    private class JTreeNodeObject implements Transferable {
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

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            // TODO Auto-generated method stub
            return null;
        }
    }

	// Top-level class Constructor
    public GameTreeSurface(Cave cave, IOSurface ioSurface) {
        this.ioSurface = ioSurface;
        this.cave = cave;
        dragSource = DragSource.getDefaultDragSource();
        
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
    		jTree.setEditable(false);
    		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    		jTree.addTreeSelectionListener(this);
    		jTree.setDragEnabled(true);
    		DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(jTree, DnDConstants.ACTION_COPY_OR_MOVE, this);
    		dgr.setSourceActions(dgr.getSourceActions());
    		dropTarget = new DropTarget(jTree, this);
		}
         
        // Add the JTree to the panel
        add(jTree, BorderLayout.CENTER);
        
        // Force it to be seen
        synchronized(jTree) {
            revalidate();
            repaint();
        }
    	
    	// The tree will be fully initialized at this point
    	isInitialized = true;
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
    
    		// Force it to be seen
            revalidate();
            repaint();
        }
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

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        DefaultMutableTreeNode dragNode;
        
        dragNode = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
        
        if(dragNode != null) {
            Transferable transferable = (Transferable) dragNode.getUserObject();
            
            Cursor cursor = selectCursor(dge.getDragAction());
            
            try {
                dragSource.startDrag(dge, cursor, transferable, this);
            } catch (Exception e) {
                System.out.println("drag exception encountered");
                return;
            }
            
            System.out.println("In dragGestureRecognized\n" + ((JTreeNodeObject)transferable).label);
        }
    }
    
    private Cursor selectCursor(int action) {
        return (action == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop : DragSource.DefaultCopyDrop;
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
        // TODO Auto-generated method stub
//        System.out.println("dragEnter(DragSourceDragEvent)");
        
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
        // TODO Auto-generated method stub
//        System.out.println("dragOver(DragSourceDragEvent)");
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
        // TODO Auto-generated method stub
//        System.out.println("dragActionChanged(DragSourceDragEvent)");
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
        // TODO Auto-generated method stub
//        System.out.println("dragExit(DragSourceEvent)");
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        // TODO Auto-generated method stub
//        System.out.println("dragDropEnd(DragSourceDropEvent)");
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
//        System.out.println("dragEnter(DropTargetDragEven)");
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
//        System.out.println("dragOver(DropTargetDragEvent)");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
//        System.out.println("dragActionChanged(DropTargetDragEvent)");
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub
//        System.out.println("dragExit(DropTargetEvent)");
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        // TODO Auto-generated method stub
        Transferable transferable = dtde.getTransferable();
        
//        //flavor not supported, reject drop
//        if(!transferable.isDataFlavorSupported(flavor)) {
//            dtde.rejectDrop();
//            return;
//        }
        
        System.out.println("drop(DropTargetDropEvent)");
    }

}