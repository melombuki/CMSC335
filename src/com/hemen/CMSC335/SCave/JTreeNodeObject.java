package com.hemen.CMSC335.SCave;

// This inner class is used with the DefaultMutableTreeNode to contain
//  both the name of the game object, as well as the index. The toString()
//  method is overridden to populate the JTree desirably.
public class JTreeNodeObject {
    public final String label;
    public final int index;
    
    public JTreeNodeObject(String label, int index) {
        this.label = label;
        this.index = index;
    }
    
    public JTreeNodeObject(JTreeNodeObject jtno) {
        String label = new String(jtno.label);
        this.label = label;
        this.index = jtno.index;
    }
    
    @Override
    public String toString() {
        return index + ": " + label;
    }
}
