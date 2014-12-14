/*
 * Filename: Artifact.java
 * Date: 1 Nov. 2014
 * Last Modified: 14 Dec. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents an artifact in the game.
 */

package com.hemen.CMSC335.SCave;

public class Artifact extends GameObject {
    
    private String type;
    private String name = "";
    
    public Artifact() {
        super();
    }
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(75); //set initial capacity large enough
        
        sb.append("Index: " + index + "\n" +
                  "Name: " + name  + "\n" +
                  "Type: " + type + "\n" +
                  "Creature Index: " + parentIndex + "\n");
        
        return sb.toString();
    }

    // Getters and Setters for each field
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return (name.equals("")) ? type : name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
