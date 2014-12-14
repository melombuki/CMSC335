/*
 * Filename: GameObject.java
 * Date: 1 Nov. 2014
 * Last Modified: 16 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class is the base class for every other
 *  game object of the game. The only shared field among
 *  all of game objects is the index.
 */

package com.hemen.CMSC335.SCave;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class GameObject {
    
    protected int index;
    protected int parentIndex;
    
    // Empty methods for adding objects to game
    //  objects. Only to be implemented in each
    //  child class when applicable.
    public void add(Party party) {}
    public void add(Creature creature) {}
    public void add(Treasure treasure) {}
    public void add(Artifact artifact) {}
    public void add(Job job) {}
    public String getName() {return "";}
    public ArrayList<Creature> getCreatures() {return null;}
    public ArrayList<Treasure> getTreasures() {return null;}
    public ArrayList<Artifact> getArtifacts() {return null;}
    public LinkedBlockingQueue<Job> getJobs() {return null;}
    
    // Getters and setters
    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }
    /**
     * @return the parent
     */
    public int getParent() {
        return parentIndex;
    }
    /**
     * @param parent the parent to set
     */
    public void setParent(int parentIndex) {
        this.parentIndex = parentIndex;
    }
}
