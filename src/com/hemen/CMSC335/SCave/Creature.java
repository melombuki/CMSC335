/*
 * Filename: Creature.java
 * Date: 1 Nov. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents a creature in the game.
 */

package com.hemen.CMSC335.SCave;

import java.util.ArrayList;

public class Creature extends GameObject {
    
    private ArrayList<Treasure> treasures;
    private ArrayList<Artifact> artifacts;
    private ArrayList<Job> jobs;
    
    private String type = "";
    private String name = "";
    private int party;
    private int empathy;
    private int fear;
    private double carryCapacity;

    // Constructor
    public Creature() {
        treasures = new ArrayList<Treasure>();
        artifacts = new ArrayList<Artifact>();
        jobs      = new ArrayList<Job>();
    }
    
    // Adds a new treasure to this creature's treasure list
    @Override
    public void add(Treasure treasure) {
        treasures.add(treasure);
    }
    
    // Adds a new artifact to this creature's artifact list
    @Override
    public void add(Artifact artifact) {
        artifacts.add(artifact);
    }
    
    // Adds a new job to this creature's job list
    @Override
    public void add(Job job) {
        jobs.add(job);
    }
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Index: " + index + "\n" +
                  "Name: " + name  + "\n" +
                  "Party: " + party + "\n" +
                  "Empathy: " + empathy + "\n" +
                  "Fear: " + fear + "\n" +
                  "Carry Capacity: " + carryCapacity + "\n");
        
        return sb.toString();
    }
    
    // Getters and setters
    /**
     * @return treasures
     */
    public ArrayList<Treasure> getTreasures() {
        return treasures;
    }
    
    /**
     * @return artifacts
     */
    public ArrayList<Artifact> getArtifacts() {
        return artifacts;
    }
    
    /**
     * @return the jobs
     */
    public ArrayList<Job> getJobs() {
        return jobs;
    }

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
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the party
     */
    public int getParty() {
        return party;
    }

    /**
     * @param party the party to set
     */
    public void setParty(int party) {
        this.party = party;
    }

    /**
     * @return the empathy
     */
    public int getEmpathy() {
        return empathy;
    }

    /**
     * @param empathy the empathy to set
     */
    public void setEmpathy(int empathy) {
        this.empathy = empathy;
    }

    /**
     * @return the fear
     */
    public int getFear() {
        return fear;
    }

    /**
     * @param fear the fear to set
     */
    public void setFear(int fear) {
        this.fear = fear;
    }

    /**
     * @return the carryCapacity
     */
    public double getCarryCapacity() {
        return carryCapacity;
    }

    /**
     * @param carryCapacity the carryCapacity to set
     */
    public void setCarryCapacity(double carryCapacity) {
        this.carryCapacity = carryCapacity;
    }
    
}
