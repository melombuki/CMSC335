/*
 * Filename: Treasure.java
 * Date: 1 Nov. 2014
 * Last Modified: 14 Dec. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents a treasure in the game.
 */

package com.hemen.CMSC335.SCave;

public class Treasure extends GameObject {
    
    private String type = "";
    private double weight;
    private int value;
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100); //increase initial capacity
        
        sb.append("Index: " + index + "\n" +
                  "Type: " + type  + "\n" +
                  "Creature Index: " + parentIndex + "\n" +
                  "Weight: " + weight + "\n" +
                  "Value: " + value + "\n");
        
        return sb.toString();
    }
    
    // Getters and setters
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
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }
    
    public String getName() {
        return type;
    }

}
