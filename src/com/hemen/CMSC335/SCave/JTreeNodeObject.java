/*
 * Filename: JTreeNodeObject.java
 * Date: 24 Nov. 2014
 * Last Modified: 14 Dec. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class is used with the DefaultMutableTreeNode to contain
 *  both the name of the game object, as well as the index. The toString()
 *  method is overridden to populate the JTree desirably.
 */

package com.hemen.CMSC335.SCave;

public class JTreeNodeObject {
    public final String label; //name or type of a game object
    public final int index;    //index of the game object
    
    // Constructor
    public JTreeNodeObject(String label, int index) {
        this.label = label;
        this.index = index;
    }
    
    // Copy Constructor
    public JTreeNodeObject(JTreeNodeObject jtno) {
        String label = new String(jtno.label);
        this.label = label;
        this.index = jtno.index;
    }
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        return index + ": " + label;
    }
}
