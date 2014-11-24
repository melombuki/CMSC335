package com.hemen.CMSC335.SCave;

import java.util.ArrayList;

public class Job extends GameObject implements Runnable {
    private String name;
    private int creatureIndex;
    private double duration;
    private ArrayList<Requirement> requirements;
    
    private class Requirement {
        private String artifact;
        private int number;
        
        // Constructor
        public Requirement(String artifact, int number) {
            this.artifact = artifact;
            this.number = number;
        }
        
        // Getters and setters
        /**
         * @return the artifact
         */
        public String getArtifact() {
            return artifact;
        }

        /**
         * @return the number
         */
        public int getNumber() {
            return number;
        }
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
    // Returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Index: " + index + "\n" +
                  "Name: " + name  + "\n" +
                  "Creature Index: " + creatureIndex + "\n" +
                  "Time: " + duration + "\n");
        
        return sb.toString();
    }
    
    // Add a requirement to the requirements array list
    public void addRequirement(String name, int number) {
        requirements.add(new Requirement(name, number));
    }
    
    // Getters and setters
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
     * @return the duration
     */
    public double getDuration() {
        return duration;
    }
    /**
     * @param duration the duration to set
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }
    /**
     * @return the artifacts
     */
    public ArrayList<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * @return the creatureIndex
     */
    public int getCreatureIndex() {
        return creatureIndex;
    }

    /**
     * @param creatureIndex the creatureIndex to set
     */
    public void setCreatureIndex(int creatureIndex) {
        this.creatureIndex = creatureIndex;
    }
    

}
