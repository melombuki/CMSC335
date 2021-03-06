/*
 * Filename: Creature.java
 * Date: 1 Nov. 2014
 * Last Modified: 14 Dec. 2014
 * Author: Joshua P. Hemen
 * Purpose: This class represents a creature in the game.
 */

package com.hemen.CMSC335.SCave;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Creature extends GameObject {
    private final ArrayList<Treasure> treasures;
    private final ArrayList<Artifact> artifacts;
    private final LinkedBlockingQueue<Job> jobs;
    private final ConcurrentHashMap<String, ArrayList<Artifact>> resources;
    private final VerifyingExecutor executor;
    
    private String type = "";
    private String name = "";
    private int empathy;
    private int fear;
    private double carryCapacity;
    
    private final ReentrantLock lock;
    private final Condition canRun;

    // Constructor
    public Creature(Cave cave) {
        super();
        
        treasures = new ArrayList<Treasure>();
        artifacts = new ArrayList<Artifact>();
        jobs      = new LinkedBlockingQueue<Job>();
        resources = new ConcurrentHashMap<String, ArrayList<Artifact>>();
        executor = new VerifyingExecutor(1, 1, Long.MAX_VALUE,
                TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>(), cave);
        
        lock = new ReentrantLock();
        canRun = lock.newCondition();
    }
    
    // This method adds a new treasure to this creature's treasure list
    @Override
    public void add(Treasure treasure) {
        treasures.add(treasure);
    }
    
    // This method adds a new artifact to this creature's artifact list
    @Override
    public void add(Artifact artifact) {
        artifacts.add(artifact);
        // Uncomment this and get rid of the artifacts when fully implementing
        //  the blocking queue resource pool. Rename resources to artifacts
        if(resources.containsKey(artifact.getType())) {
            resources.get(artifact.getType()).add(artifact);
        } else {
            resources.put(artifact.getType(), new ArrayList<Artifact>());
            resources.get(artifact.getType()).add(artifact);
        }
    }
    
    // This method adds a new job to this creature's job list
    @Override
    public void add(Job job) {
        jobs.add(job);
        executor.execute(job);
    }
    
    // This method returns a string with this objects information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(125); //increase initial capacity
        
        sb.append("Index: " + index + "\n" +
                  "Name: " + name  + "\n" +
                  "Party: " + parentIndex + "\n" +
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
    public LinkedBlockingQueue<Job> getJobs() {
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

    /**
     * @return lock
     */
    public ReentrantLock getLock() {
        return lock;
    }

    /**
     * @return canRun
     */
	public Condition getCanRunCondition() {
		return canRun;
	}

	/**
	 * @return resources
	 */
    public ConcurrentHashMap<String, ArrayList<Artifact>> getResources() {
        return resources;
    }
    
}
