/*
 * Filename: TreeTransferHandler.java
 * Date: 13 Nov. 2014
 * Last Modified: 14 Dec. 2014
 * Author: This class handles drag and drop of nodes within the tree.
 *  This class was initially written by Craig Wood. It was
 *  retrieved from http://www.coderanch.com/t/346509/GUI/java/JTree-drag-drop-tree-Java
 *  and was posted on (14 January, 2004). It has been modified by Joshua Hemen.
 *  Purpose: This class handles drag and drop capabilities within a JTree. In this
 *   application, game objects are reassigned to the object they are dragged onto.
 *   Only free objects within the cave can be moved. Creatures can only be moved
 *   into parties. Treasures and Artifacts can only be moved into Creatures.
 */

package com.hemen.CMSC335.SCave;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class TreeTransferHandler extends TransferHandler {
    private final Cave cave;
    private final DataFlavor nodesFlavor;
    private static final DataFlavor[] flavors = new DataFlavor[1];
    private DefaultMutableTreeNode nodeToRemove;

    // Constructor
    public TreeTransferHandler(Cave cave) {
        this.cave = cave;

        nodesFlavor = new DataFlavor(JTreeNodeObject.class, "DefaultMutableTreeNode Type");
        flavors[0] = nodesFlavor;
    }

    // This method determines whether or not the source can be dropped on the destination
    public boolean canImport(TransferHandler.TransferSupport support) {
        // Do not allow a drop if the data flavor is not supported
        if(!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        if(!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }
        
        // Do not allow a drop on the drag source selections, or the root
        JTree.DropLocation dl =
                (JTree.DropLocation)support.getDropLocation();
        JTree tree = (JTree)support.getComponent();
        int dropRow = tree.getRowForPath(dl.getPath());
        int[] selRows = tree.getSelectionRows();
        for(int i = 0; i < selRows.length; i++) {
            if(selRows[i] == 0 || selRows[i] == dropRow) {
                return false;
            }
        }
    
        // Bail if there is no destination
        TreePath dest = dl.getPath();
        if(dest == null) {
            return false;
        }

        // Get parent, source and target information
        TreePath parentPath = tree.getSelectionPath().getParentPath();
        DefaultMutableTreeNode parentNode = 
                (DefaultMutableTreeNode) parentPath.getLastPathComponent();
        JTreeNodeObject parentObj = (JTreeNodeObject) parentNode.getUserObject();
        
        TreePath sourcePath = tree.getSelectionPath();
        DefaultMutableTreeNode sourceNode = 
                (DefaultMutableTreeNode) sourcePath.getLastPathComponent();
        JTreeNodeObject sourceObj = (JTreeNodeObject) sourceNode.getUserObject();
        
        DefaultMutableTreeNode target =
                (DefaultMutableTreeNode)dest.getLastPathComponent();
        JTreeNodeObject targetObj = (JTreeNodeObject) target.getUserObject();
        
        // Only allow drop if the source is attached to the cave
        if(parentNode != null) {
            if(parentObj.index != 0) {
                return false;
            }
        }
        
        // If source is an Artifact or Treasure
        //  only allow drop if destination is a creature
        if(cave.searchByIndex(sourceObj.index) instanceof Artifact ||
                cave.searchByIndex(sourceObj.index) instanceof Treasure) {
            if(cave.searchByIndex(targetObj.index) instanceof Creature) {
                return true;
            }
        } 
        // If source is a Creature, only allow drop if destination is a Party
        else if(cave.searchByIndex(sourceObj.index) instanceof Creature) {
            if(cave.searchByIndex(targetObj.index) instanceof Party) {
                return true;
            }
        }

        return false;
    }

    // Packages the transferable data into a Transferable object
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree)c;
        TreePath path = tree.getSelectionPath();
        if(path != null) {
            // Make a copy of a node for transfer and
            // another for the node that will be removed in
            // exportDone after a successful drop
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode)path.getLastPathComponent();

            DefaultMutableTreeNode copy = copy(node);
          
            nodeToRemove = node;
            return new NodeTransferable(copy);
        }
        
        return null;
    }

    // Defensive copy used in createTransferable.
    private DefaultMutableTreeNode copy(DefaultMutableTreeNode node) {
        return new DefaultMutableTreeNode(new JTreeNodeObject((JTreeNodeObject) node.getUserObject()));
    }

    // Not needed
    protected void exportDone(JComponent source, Transferable data, int action) {
    }

    // This method sets the default action to Copy or Move
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    // This method pulls the information from a Transferable object and 
    //  handles it appropriately.
    public boolean importData(TransferHandler.TransferSupport support) {   
        if(!canImport(support)) {
            return false;
        }
        
        // Only import the data if it was a move action
        int action = support.getUserDropAction();
        if((action & MOVE) == MOVE) {
            // Extract transfer data
            JTreeNodeObject userObj = null;
            try {
                Transferable t = support.getTransferable();
                userObj = (JTreeNodeObject) t.getTransferData(nodesFlavor);
            } catch(UnsupportedFlavorException ufe) {
                System.out.println("UnsupportedFlavor: " + ufe.getMessage());
            } catch(java.io.IOException ioe) {
                System.out.println("I/O error: " + ioe.getMessage());
            }
            
            // Get drop location info
            JTree.DropLocation dl =
                    (JTree.DropLocation)support.getDropLocation();
            TreePath dest = dl.getPath();
            DefaultMutableTreeNode destNode =
                (DefaultMutableTreeNode)dest.getLastPathComponent();

            // Change the node's parent and add data to model
            int destIndex = ((JTreeNodeObject) destNode.getUserObject()).index;
            GameObject gameObj = cave.searchByIndex(userObj.index);
            Class<? extends GameObject> classType = gameObj.getClass();

            // Handle the info based on the source class type
            gameObj.setParent(destIndex);
            if(classType.equals(Artifact.class)) {
                // Remove nodes saved in nodesToRemove in createTransferable
                cave.remove((Artifact) cave.searchByIndex(((JTreeNodeObject) nodeToRemove.getUserObject()).index));
                // Add the artifact back into the cave and the jTree view with new creature index
                cave.add((Artifact) gameObj);
                return true;
            } else if(classType.equals(Treasure.class)) {
                // Remove nodes saved in nodesToRemove in createTransferable
                cave.remove((Treasure) cave.searchByIndex(((JTreeNodeObject) nodeToRemove.getUserObject()).index));
                // Add the artifact back into the cave and the jTree view with new creature index
                cave.add((Treasure) gameObj);
                return true;
            } else if(classType.equals(Creature.class)) {
                if(cave.searchByIndex(destIndex) instanceof Party) {
                    // Remove nodes saved in nodesToRemove in createTransferable
                    cave.remove((Creature) cave.searchByIndex(((JTreeNodeObject) nodeToRemove.getUserObject()).index));
                    // Add the artifact back into the cave and the jTree view with new creature index
                    cave.add((Creature) gameObj);
                    
                    // Move all of the creature's artifacts, treasures and jobs
                    //  back into the tree the tree as well
                    for(Artifact artifact : ((Creature) gameObj).getArtifacts()) {
                        cave.add(artifact);
                    }
                    for(Treasure treasure: ((Creature) gameObj).getTreasures()) {
                        cave.add(treasure);
                    }
                    for(Job job : ((Creature) gameObj).getJobs()) {
                        cave.add(job);
                    }
                    return true;
                }
            }
        }
        
        return false;
    }

    // Returns a string with this objects information
    @Override
    public String toString() {
        return getClass().getName();
    }

    // This inner class holds the transferable data in the form of a JTreeNodeObject.
    //  The JTreeNodeObject holds the index and name of the source component that
    //  matches the index in the cave multi-tree structure.
    public class NodeTransferable implements Transferable {
        JTreeNodeObject jtno;

        // Inner Class Constructor
        public NodeTransferable(DefaultMutableTreeNode node) {
            jtno = (JTreeNodeObject) node.getUserObject();
         }

        // This method returns the actual transferable data
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor)) {
                System.out.println("Throwing new UnsupportedFlavorException for " + flavor);
                throw new UnsupportedFlavorException(flavor);
            }
            return jtno;
        }

        // This method returns the supported data flavors. In this case
        //  the only supported data flavor is the JTreeNodeObject class.
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        // This returns true iff the requested data flavor is supported.
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
