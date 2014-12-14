//// This class handles drag and drop of nodes within the tree.
////  This class was initially written by Craig Wood. It was
////  retrieved from http://www.coderanch.com/t/346509/GUI/java/JTree-drag-drop-tree-Java
////  and was posted on (14 January, 2004). It has been modified by Joshua Hemen.
//
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
    Cave cave;
    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode nodeToRemove;

    public TreeTransferHandler(Cave cave) {
        this.cave = cave;

        nodesFlavor = new DataFlavor(JTreeNodeObject.class, "DefaultMutableTreeNode Type");
        flavors[0] = nodesFlavor;
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
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
        
        // Only allow drop if the source is attached to the cave
        TreePath parentPath = tree.getSelectionPath().getParentPath();
        Object obj = parentPath.getLastPathComponent();
        if(obj != null) {
            if(!obj.equals(tree.getModel().getRoot())) {;
                return false;
            }
        }
        
        // Only allow drop if dest is a creature
        TreePath dest = dl.getPath();
        if(dest == null) {
            return false;
        }
        DefaultMutableTreeNode target =
                (DefaultMutableTreeNode)dest.getLastPathComponent();
        int targetIndex = ((JTreeNodeObject) target.getUserObject()).index;
        for(Party party : cave.getParties()) {
           for(Creature creature : party.getCreatures()) {
               if(creature.index == targetIndex) {
                   return true;
               }
           }
        }

        return false;
    }

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

    /** Defensive copy used in createTransferable. */
    private DefaultMutableTreeNode copy(DefaultMutableTreeNode node) {
        return new DefaultMutableTreeNode(new JTreeNodeObject((JTreeNodeObject) node.getUserObject()));
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
        if((action & MOVE) == MOVE) {
             
        }
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public boolean importData(TransferHandler.TransferSupport support) {   
        if(!canImport(support)) {
            return false;
        }
        
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
        DefaultMutableTreeNode parent =
            (DefaultMutableTreeNode)dest.getLastPathComponent();
        
        // Change the node's parent and add data to model
        int parentIndex = ((JTreeNodeObject) parent.getUserObject()).index;
        Artifact artifact = (Artifact) cave.searchByIndex(userObj.index);
        artifact.setCreature(parentIndex);
        
        // Remove nodes saved in nodesToRemove in createTransferable
        System.out.println("Removing " + nodeToRemove);
        cave.remove((Artifact) cave.searchByIndex(((JTreeNodeObject) nodeToRemove.getUserObject()).index));
        
        System.out.println("Adding\n"  + artifact);
        cave.add(artifact);
        
        return true;
    }

    public String toString() {
        return getClass().getName();
    }

    public class NodeTransferable implements Transferable {
        JTreeNodeObject jtno;

        public NodeTransferable(DefaultMutableTreeNode node) {
            jtno = (JTreeNodeObject) node.getUserObject();
         }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor)) {
                System.out.println("Throwing new UnsupportedFlavorException for " + flavor);
                throw new UnsupportedFlavorException(flavor);
            }
            return jtno;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
