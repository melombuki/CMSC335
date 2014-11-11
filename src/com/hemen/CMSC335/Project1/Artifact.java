/*
 * Filename: Artifact.java
 * Date: 1 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents an artifact in the game.
 */

package com.hemen.CMSC335.Project1;

public class Artifact extends GameObject {
    
    private String type;
    private int creature;
    private String name = "";
    
    // Empty constructor
    public Artifact() {
        
    }
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Index: " + index + "\n" +
                  "Name: " + name  + "\n" +
                  "Type: " + type + "\n" +
                  "Creature Index: " + creature + "\n");
        
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
     * @return the creature
     */
    public int getCreature() {
        return creature;
    }

    /**
     * @param creature the creature to set
     */
    public void setCreature(int creature) {
        this.creature = creature;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
