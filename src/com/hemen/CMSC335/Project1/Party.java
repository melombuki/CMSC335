/*
 * Filename: Party.java
 * Date: 1 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents a party in the game.
 */

package com.hemen.CMSC335.Project1;

import java.util.ArrayList;

public class Party extends GameObject {
    
    private ArrayList<Creature> creatures;
    
    private String name = "";
    
    // Constructor
    public Party() {
        creatures = new ArrayList<Creature>();
    }
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Index: " + index + "\n" +
                  "Name: " + name + "\n");
        
        return sb.toString();
    }
    
    // Adds a new creature to the creature list
    @Override
    public void add(Creature creature) {
        creatures.add(creature);
    }
    
    // Getters and setters
    /**
     * @return creatures
     */
    public ArrayList<Creature> getCreatures() {
        return creatures;
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
